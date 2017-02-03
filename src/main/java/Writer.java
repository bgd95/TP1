import org.omg.CORBA.WrongTransaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Writer {
    /**
     * Escribe las llaves y los valores de un mapa sring, integer en un archivo de texto en la direccion senalada
     * por path
     * @param map mapa cyos valores se quieren escribir
     * @param path ruta del achivo donde se quiere escribir
     */
    public static void writeWordFrequencies(Map<String, Integer> map, String path){
        try{
            File file = new File(path);
            if(!file.exists())
                file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            Set<Map.Entry<String, Integer>> info = map.entrySet();
            Iterator<Map.Entry<String, Integer>> iterator = info.iterator();
            while(iterator.hasNext()){
                Map.Entry<String, Integer> currentEntry = iterator.next();
                writer.write(currentEntry.getKey() + " " + currentEntry.getValue() + "\n");
            }
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Escribe dos doubles y un entero en el archio de texto senalado por path.
     * Se usa para escribir en el archivo de texto los parametros necesarios en el programa,
     * los cuales son la probabilidad de spam, la probabilidad a la que se tiene que llegar para
     * saber que un correo es spam y la cantidad de correos para entrenar el programa.
     * Cuando el programa inicia, lee los ultimos valores definidos por el usuariopara estas variables.
     * @param filename archivo donde se van a escribir los datos
     * @param spamProbability probailidad de que un correo sea spam
     * @param spamThreshold probabilidad que se tiene que igualar o superar para decir que un correo es spam
     * @param mailNumber numero de correos utilizados para entrenar el programa
     */
    public static void writeParameters(String filename, Double spamProbability, Double spamThreshold, Integer mailNumber){
        try{
            File file = new File(filename);
            if(file.exists())
                file.delete();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(spamProbability.toString() + "\n");
            writer.write(spamThreshold.toString() + "\n");
            writer.write(mailNumber.toString() + "\n");
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String... args){
        Map<String, Integer> map = new HashMap<>();
        for(int i = 0; i < 20; i++){
            String s = "";
            for(int j = 0; j < i; j++)
                s += "a";
            map.put(s, i);
        }

        Writer.writeWordFrequencies(map, "WordFrequencies.txt");
    }
}
