// Laura Fonseca
// N Felipe Celis D

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class simulador{



   
//Generación de referencias
    public void opcion1(int tp,int nproc, int[] nfs, int[] ncs)throws IOException{

        //Iteramos por cada proceso
        for(int proceso_actual=0; proceso_actual<nproc; proceso_actual++){
            //Se saca el numero de filas y columnas del proceso actual
            int NF = nfs[proceso_actual]; 
            int NC = ncs[proceso_actual];
            int NR = 3 * NC * NF; //El numero de referencias se saca del numero de filas por el numero de columnas por las 3 matrices simbolicas.
            int NP = (int) Math.ceil(((NF * NC)*3 * 4) / tp);  //El numero de paginas se saca del numero de filas por el numero de columnas por las 3 matrices simbolicas por 4 bytes (tamaño de un entero) dividido entre el tamaño de la página y se redondea hacia arriba.
             
            int baseM1 = 0; //Base de la primera matriz
            int baseM2 = NF * NC * 4; //Base de la segunda matriz
            int baseM3 = 2 * NF * NC * 4; //Base de la tercera matriz

            try(BufferedWriter w = new BufferedWriter(new java.io.FileWriter("proceso"+proceso_actual+".txt"))){

                w.write("TP=" + tp + "\n");
                w.write("NF=" + NF + "\n");
                w.write("NC=" + NC + "\n");
                w.write("NR=" + NR + "\n");
                w.write("NP=" + NP + "\n");

            
            //Accesos por posciones en row-major, donde M1 r, M2 r, M3 w. 
            for(int i =0;i<NF;i++){
                for(int j=0;j<NC;j++){
                    int id= i*NC + j ; //Identificador de la posición en la matriz

                    int div1 = baseM1 + id * 4;

                    escribir(w, "M1", i, j, div1, tp, 'r');
                    int div2 = baseM2 + id *4;
                    escribir(w, "M2", i, j, div2, tp, 'r');
                    int div3 = baseM3 + id *4;
                    escribir(w, "M3", i, j, div3, tp, 'w');
                }
            
            }
        
            }   
        
    }

}

//Simulación de la ejecución opcion 2






public void opcion2(int nproc, int NM) throws IOException{

  //Cargamos los procesos desde sus respectivos archivos, generados por la opción 1
  List<Proceso> procesos = new ArrayList<>();
  for(int i=0;i<nproc;i++){
    procesos.add(cargarProceso("proceso"+i+".txt", i));
  }
  
  //Repartimos equitativamente los marcos 
  Memoria mem = new Memoria(NM);
  int marcosPorProc = NM / nproc;
  int sobrantes = NM % nproc;
  int siguiente =0;
  for(int i=0;i<nproc;i++){
    Proceso p=procesos.get(i);
    int asignar = marcosPorProc + (sobrantes>0?1:0); //Si hay sobrantes, se asigna uno más
    for(int k=0;k<asignar;k++){
      p.marcos.add(siguiente);
      siguiente++;
    }
  }
  
  //Por turnos 
  ArrayDeque<Proceso> cola = new ArrayDeque<>(procesos);
  for(Proceso p: procesos){
    cola.add(p);} 

    //Aqui quede...... 
  }

}







private void escribir(BufferedWriter w, String M, int i, int j, long dv, int tp, char rw) throws IOException {
  long pag = dv / tp, off = dv % tp;
  w.write(M + ":[" + i + "-" + j + "]," + pag + "," + off + "," + rw + "\n");
}



//Lector de archivos de entrada por medio de una clase la que permite almacenar los datos de configuración

static final class Config {
    final int TP, NPROC;
    final int[] nfs, ncs;
    Config(int TP, int NPROC, int[] nfs, int[] ncs) {
      this.TP = TP; this.NPROC = NPROC; this.nfs = nfs; this.ncs = ncs;
    }
  }

  public static Config leerConfig(String ruta) throws IOException {
    Map<String,String> cfg = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
      String linea;
      while ((linea = br.readLine()) != null) {
        linea = linea.trim();
        if (linea.isEmpty() || linea.startsWith("#")) continue;
        int pos = linea.indexOf('=');
        if (pos <= 0 || pos == linea.length() - 1)
          throw new IllegalArgumentException("Línea inválida: " + linea);
        String llave = linea.substring(0, pos).trim();
        String val = linea.substring(pos + 1).trim();
        cfg.put(llave, val);
      }
    }

    // Validaciones
    if (!cfg.containsKey("TP"))    throw new IllegalArgumentException("Falta clave TP");
    if (!cfg.containsKey("NPROC")) throw new IllegalArgumentException("Falta clave NPROC");
    if (!cfg.containsKey("TAMS"))  throw new IllegalArgumentException("Falta clave TAMS");

    int TP = parsePos(cfg.get("TP"), "TP");
    int NPROC = parsePos(cfg.get("NPROC"), "NPROC");

    String tams = cfg.get("TAMS");
    String[] parts = tams.split(",");

    //Se valida que el numero de tamaños sea igual al numero de procesos y se guardan en arreglos los tamaños de filas y columnas
    if (parts.length != NPROC)
      throw new IllegalArgumentException("TAMS debe tener " + NPROC + " tamaños (tiene " + parts.length + ")");

    int[] nfs = new int[NPROC];
    int[] ncs = new int[NPROC];
    for (int i = 0; i < NPROC; i++) {
        String item = parts[i].trim();
        String[] xy = item.split("x");
        if (xy.length != 2) throw new IllegalArgumentException("Formato inválido en TAMS: " + item);
        nfs[i] = parsePos(xy[0], "NF");
        ncs[i] = parsePos(xy[1], "NC");
    }

    return new Config(TP, NPROC, nfs, ncs);
  }

  //Se hace el parseo a numeros y se validan que sean positivos
  private static int parsePos(String s, String nombre) {
    int v;
    try { v = Integer.parseInt(s.trim()); }
    catch (NumberFormatException e) { throw new IllegalArgumentException(nombre + " no numérico: " + s); }
    if (v <= 0) throw new IllegalArgumentException(nombre + " debe ser > 0");
    return v;
  }


// Estructuras para la Opcion 2


static final class Ref {
  final int pag; //Numero de pagina virtual a la que accede esta referencia
  final boolean write; //True si es escritura, false si es lectura

  Ref(int pag, boolean write) 
  { this.pag = pag; this.write = write; }

}

static final class EntradaPagina {

  int marco = -1; //Marco en memoria física donde está cargada la página, -1 si no está cargada
  boolean presente = false; //True si está cargada en memoria física, false si no
  boolean modificado = false; //True si ha sido modificada (escritura), false si solo lectura
  int uso = 0; // Para LRU y saber cual fue la última vez que se usó esta página
}

static final class Proceso{
  final int id, TP, NF, NC, NR, NP;
  final List<Ref> refs = new ArrayList<>(); //Lista de referencias del proceso 
  final List<Integer> marcos = new ArrayList<>();
  final Map<Integer,EntradaPagina> tabla = new HashMap<>(); //Tabla de páginas del proceso

  int  hits = 0, fallos = 0, swaps=0,indiceRef=0;
  Proceso(int id, int TP, int NF, int NC, int NR, int NP){
    this.id = id; this.TP = TP; this.NF = NF; this.NC = NC; this.NR = NR; this.NP = NP;
  }  
//Para saber si el proceso ha terminado sus referencias
  boolean terminado(){
    return indiceRef >= refs.size();
  }
  //Para obtener la siguiente referencia del proceso
  Ref refActual(){
    return refs.get(indiceRef);
  }

static final class Memoria {

  final int[] dueños; // id del proceso dueño del marco, -1 si libre
  final int[] pagMarco; // Página virtual cargada en el marco, -1 si libre
  Memoria(int marcos){
    dueños = new int[marcos];
    pagMarco = new int[marcos];
    Arrays.fill(dueños, -1);
    Arrays.fill(pagMarco, -1);
  }

}

  //Carga de procesos para Opcion 2: 

  private static Proceso cargarProceso(String ruta, int id) throws IOException {
    try(BufferedReader br = new BufferedReader(new FileReader(ruta))){
    int TP = parseIntReq(br.readLine(), "TP");
    int NF = parseIntReq(br.readLine(), "NF");
    int NC = parseIntReq(br.readLine(), "NC");
    int NR = parseIntReq(br.readLine(), "NR");
    int NP = parseIntReq(br.readLine(), "NP");
    Proceso p = new Proceso(id, TP, NF, NC, NR, NP);

    // Referencias: Mx:[i-j],pag,off,r|w  -> nos importa pag y r/w
    String ln;
    while((ln = br.readLine()) != null){
      ln = ln.trim(); if(ln.isEmpty()) continue;
      // Formato: algo, PAG, OFF, RW
      String[] parts = ln.split(",");
      if(parts.length < 4) continue;
      int pag = Integer.parseInt(parts[1].trim());
      char rw = parts[3].trim().charAt(0);
      p.refs.add(new Ref(pag, rw=='w'));
    }
    return p;
  }
}

private static int parseIntReq(String linea, String key){
  if(linea == null) throw new IllegalArgumentException("Cabecera incompleta: falta " + key);
  linea = linea.trim();
  int pos = linea.indexOf('=');
  if(pos <= 0) throw new IllegalArgumentException("Línea inválida ("+key+"): " + linea);
  String k = linea.substring(0,pos).trim();
  String v = linea.substring(pos+1).trim();
  if(!k.equals(key)) throw new IllegalArgumentException("Esperaba "+key+" y llegó "+k);
  return Integer.parseInt(v);
}

  


//Main del programa
    public static void main( String[] args) throws IOException{
        if(args.length < 1){
            System.out.println("Falta archivo de configuración");
            return;
        }
        Config config = leerConfig(args[0]);
        new simulador().opcion1(config.TP, config.NPROC, config.nfs, config.ncs);
        System.out.println("Archivos generados");

    }




}

