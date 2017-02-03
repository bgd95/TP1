import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Reader {
    public static TreeSet<String> getStopWords(String path){
        TreeSet<String> stopWords = new TreeSet<>();
        try{
            BufferedReader reader  = new BufferedReader(new FileReader(path));
            String currentLine = "";
            while((currentLine = reader.readLine()) != null){
                stopWords.add(currentLine);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return stopWords;
    }

    public static Map<String, Integer> getWordFrequencies(String path){
        Map<String, Integer> map = new HashMap<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String currentLine = "";
            while((currentLine = reader.readLine()) != null){
                String[] wordInfo = currentLine.split(" ");
                map.put(wordInfo[0], Integer.parseInt(wordInfo[1]));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String... args){
        TreeSet<String> ts = Reader.getStopWords("StopWords.txt");
        /*Iterator<String> i = ts.iterator();
        while(i.hasNext())
            System.out.println(i.next());*/

        Map<String, Integer> map = Reader.getWordFrequencies("WordFrequencies.txt");
        Set<Map.Entry<String, Integer>> s = map.entrySet();
        Iterator<Map.Entry<String, Integer>> i = s.iterator();
        while(i.hasNext()){
            Map.Entry<String, Integer> c = i.next();
            System.out.println(c.getKey() + " " + c.getValue());
        }
        Frequencies f = new Frequencies();
        //Map<String, Integer> frequencies = f.generateFrequencies();
        //Map<String, Double> probabilities = f.generateProbabilities(frequencies, f.getTotalWords());
    }
}
