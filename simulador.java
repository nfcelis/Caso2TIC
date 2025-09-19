// Laura Fonseca
// N Felipe Celis D



public class simulador{



   
//Generación de referencias
    public void opcion1(int tp,int nproc, int[] nfs, int[] ncs){

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

            //Accesos por posciones en row-major. 
            for(int i =0;i<NF;i++){
                for(int j=0;j<NC;j++){
                    int id= i*NC + j ; //Identificador de la posición en la matriz



                }
            }



            }
        }



   

    

    public static void main( String[] args){



    }




}

