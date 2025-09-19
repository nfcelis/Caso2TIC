// Laura Fonseca
// N Felipe Celis D

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
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

//Simulación de la ejecución 

public void opcion2(int nproc, int NM){
  
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

