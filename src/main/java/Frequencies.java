import java.util.*;

public class Frequencies {
    private int totalWords = 0;

    /**
     * Devuelve un mapa con la cantidad de correos en los que aparecen las palabras encontradas n ellos
     * @param list lista de strings de la cual van a ser sacadas las frecuencias
     * @return un mapa con la cantidad de correos en los que aparecen las parabras
     */
    public Map<String, Integer> generateFrequencies(List<String> list){
        Map<String, Integer> map = new HashMap<>();
        Set<String> stopWords = Reader.getStopWords("StopWords.txt");
        for(int i = 0; i < list.size(); i++){
            String messageWords[] = list.get(i).split(" ");
            for(int j = 0; j < messageWords.length; j++){
                String currentWord = messageWords[j].toLowerCase();
                currentWord = currentWord.replaceAll("[!?<>()\".,;:-_-&/123+456789*°¬'¡#$%{}|0©€à¿“ ]","");
                currentWord = currentWord.trim();

                if(!stopWords.contains(currentWord) && currentWord.length() > 1 && !currentWord.contains("http")){
                    totalWords++;
                    if(!map.containsKey(currentWord)){
                        map.put(currentWord, 1);
                    }else{
                        int frequency = map.get(currentWord);
                        map.put(currentWord, frequency + 1);
                    }
                }
            }
        }
        return this.generateFrequencies(list, map.keySet());
    }

    /**
     * Devuelve un mapa de string y double con las probabilidades de que una palabra sea spam
     * @param frequencies mapa que contiene las frecuencias de la palabras
     * @param totalMessages total de palabras leidas
     * @return Devuelve un mapa de string y double con las probabilidades de que una palabra sea spam.
     */
    public Map<String, Double> generateProbabilities(Map<String, Integer> frequencies , int totalMessages){
        Map<String, Double> probabilities = new HashMap<>();
        Set<Map.Entry<String, Integer>> entries = frequencies.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Integer> currentEntry = iterator.next();
            probabilities.put(currentEntry.getKey(), (double)currentEntry.getValue() / totalMessages);
        }
        return probabilities;
    }


    /**
     * Genera un mapa con las frecuencias de las palabras. Esto es que por cada palabra en el conjunto pasado por parametro
     * el algoritmo inserta en otro mapa la cantidad de correos en los que aparece la palabra
     * @param list lista de strings con los mensajes de los correos
     * @param words conjunto de palabras de las cuales se va a sacar la freuencia
     * @returnun mapa con las frecuencias de las palabras
     */
    private Map<String,Integer> generateFrequencies(List<String> list, Set<String> words){
        Map<String, Integer> freqMap = new HashMap<>();
        Iterator<String> iterator = words.iterator();
        while(iterator.hasNext()){
            String currentString = iterator.next();
            int counter = 0;
            for(int i = 0; i < list.size(); i++){
                if(list.get(i).toLowerCase().contains(currentString)){
                    counter++;
                }
            }
            freqMap.put(currentString, counter);
        }
        return freqMap;
    }

    /**
     * Algoritmo que dice si un mensaje dado es spam o no. Se le pasan mapas con las palabras que son y que no son
     * spam y las probabilidades de que sean o no spam, un string que representa el mensaje, un double que es la
     * probabilidad definida por el usuario como la probabilidad de que un correo sea spam y otro double
     * que es el threshold que se tiee que igualar o superar para que un mensaje sea considerado como spam.
     * Prmero el algoritmo separa el mensaje en un arreglo de strings que representa sus palabras. Luego, por
     * cada palabra calcula la probabildad de que sea spam usando el teorema de Bayes y esa proba se le va
     * agregando a la proba total de que el mensaje sea spam, finalmente la proba total se divide entre la
     * cantidad de palabras. Luego de esto, sui el numero resultante e mayor o igual al threshold, el
     * booleano de retorno se pone en true, de otra manera se deja en false.
     * @param spamMap mapa con las palabras de los correos considerados spam y las probabilidades de que sean encntradas dado que son spam.
     * @param notSpamMap mapa con las palabras de los correos que no son considerados spam y las probabilidades de que sean encntradas dado que no son spam.
     * @param message mensaje que se esta evaluando.
     * @param spamProbability probabilidad default de que un mensaje sea spam.
     * @param spamThreshold threshold que se tiene que superar para que el mensaje dado sea considerado spam.
     * @return true si el correo puede ser considerado spam, false de otra manera.
     */
    public static boolean spam(Map<String, Double> spamMap, Map<String, Double> notSpamMap, String message, double spamProbability, double spamThreshold) {
        String[] words = message.split(" ");
        double threshold = 0;
        int sumwords = 0;
        for (int i = 0; i < words.length; i++) {
            String currentWord = words[i].toLowerCase();
            currentWord = currentWord.replaceAll("[!?<>()\".,;:-_-&/123+456789*°¬'¡#$%{}|0©€à¿“ ]", "");
            currentWord = currentWord.trim();
            Double spamProb = spamMap.get(currentWord);
            Double spamComplementProb = notSpamMap.get(currentWord);

            if (spamProb != null && spamComplementProb != null) {
                threshold += (spamProb * spamProbability) / (spamProb * spamProbability + spamComplementProb * (1 - spamProbability));
                sumwords++;
            } else if (spamProb != null && spamComplementProb == null) {
                threshold++;
                sumwords++;
            }
        }

        boolean isSpam = false;
        if (sumwords != 0) {
            threshold = threshold / sumwords;
            isSpam = threshold >= spamThreshold;
        }
        return isSpam;
    }
}
