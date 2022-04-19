package generation.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Reverso {
    private HashMap<String,Integer> ngram;
    private HashMap<String, Integer> wordToInt = new HashMap<String, Integer>();
    private HashMap<Integer, String> intToWord = new HashMap<Integer, String>();
    private HashMap<Integer,String> intToPOS = new HashMap<>();
    private HashMap<Integer, Integer> intToCost = new HashMap<Integer, Integer>();

    public HashMap<Integer, Integer> getIntToCost() {
        return intToCost;
    }
    public HashMap<Integer, String> getIntToPOS() { return  intToPOS;}


    public HashSet<String> isValid = new HashSet<>();

    public Reverso(HashMap<String, Integer> ngram) {
        this.ngram=ngram;
        int k = 3;
        wordToInt.put("", 0); //mots vide
        intToWord.put(0, "");

        wordToInt.put("#", 1); //mots vide
        intToWord.put(1, "#");

        wordToInt.put("@", 2); //mots vide
        intToWord.put(2, "@");

        for (String s : ngram.keySet()) {
            if (s.split(" ").length == 1) {
                if (s != null) {
                    wordToInt.put(s, k);
                    intToWord.put(k, s);
                    k++;
                }
            }
        }
    }

    @JsonCreator
    public Reverso(@JsonProperty("wordToInt") HashMap<String, Integer> wordToInt, @JsonProperty("intToWord") HashMap<Integer, String> intToWord) {
        this.wordToInt = wordToInt;
        this.intToWord = intToWord;
    }

    public int getCount(String ngram){
        return this.ngram.get(ngram);
    }
    public void buildCost() {

        for (String k : wordToInt.keySet()) {
            intToCost.put(stringToInt(k), k.length());
        }
        intToCost.replace(0, -1);
    }

    public void buildIsValid(Set<String> kgram) {
        String[] tmp;
        for (String k : kgram) {
            tmp = k.split(" ");
            if (tmp.length == 2) {
                isValid.add(stringToInt(tmp[0]) + ":" + stringToInt(tmp[1]));//+":"+stringToInt(tmp[2]));
            }
            if (tmp.length == 3) { //to generalize

                isValid.add(stringToInt(tmp[0]) + ":" + stringToInt(tmp[1]) + ":" + stringToInt(tmp[2]));
            }
            if (tmp.length == 4) { //to generalize

                isValid.add(stringToInt(tmp[0]) + ":" + stringToInt(tmp[1]) + ":" + stringToInt(tmp[2]) + ":" + stringToInt(tmp[3]));
            }
            if (tmp.length == 5) { //to generalize

                isValid.add(stringToInt(tmp[0]) + ":" + stringToInt(tmp[1]) + ":" + stringToInt(tmp[2]) + ":" + stringToInt(tmp[3]) + ":" + stringToInt(tmp[4]));
            }
        }
    }

    public HashMap<Integer, String> getIntToWord() {
        return intToWord;
    }

    public HashMap<String, Integer> getWordToInt() {
        return wordToInt;
    }

    public int cost(int v) {
        //if(v==0)return 0;
        return intToCost.get(v);
    }

    public int stringToInt(String s) {
        return wordToInt.get(s);
    }

    public String IntToWord(int v) {
        return intToWord.get(v);
    }

    public static HashSet<String> initlemset() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File("src/main/resources/dataText/lemset.json");

        HashSet<String> mydict = objectMapper.readValue(file, HashSet.class);
        return mydict;
    }
    public void initIntToPOS() throws Exception {
        lemma.fillIntToPOS(this);
    }
    public static void pretty(int[] tab, Reverso revers) {
        System.out.println(" ");

        for (int v : tab) {
            System.out.print(revers.IntToWord(v) + " ");
        }
        System.out.println("");
        for (int v : tab) {
            System.out.print(v + ",");
        }
    }
}
