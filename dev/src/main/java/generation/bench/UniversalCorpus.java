package generation.bench;

import builder.MDDBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import dd.mdd.MDD;
import generation.utils.MyConstraintBuilder;
import generation.utils.Reverso;
import generation.utils.lemma;
import structures.arrays.ArrayOfInt;
import utils.io.MDDReader;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UniversalCorpus {
    public static void main(String[] args) throws Exception{
        int SIZE = 15; //define SIZE of the DD
    
        String inputFile="src/main/resources/dataText/contes#@.json";
        String reversoFile=inputFile+"_reverso.json";

        //load reverso file
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(inputFile);
        Map<String,Integer> mydict = objectMapper.readValue(file, HashMap.class);
        Reverso reverso = new Reverso((HashMap<String, Integer>) mydict);
        objectMapper.writeValue(new File("./"+reversoFile), reverso);

        HashSet<String> cleand = lemma.clean(Reverso.initlemset(),reverso); // build Allowed lexicon
        ArrayOfInt D = MyConstraintBuilder.consArrayFromLemset(cleand,reverso); // build Domain


        //Initialiaze and Build MDD
        MDD myMDD = MDD.create();
        myMDD.setSize(SIZE); 
        myMDD = MDDBuilder.universal(myMDD, D, myMDD.size()); 

        System.out.println(" UMDD DONE");
        System.out.println(myMDD.nSolutions());
        System.out.println(myMDD.arcs());
        System.out.println(myMDD.nodes());

        MDDReader.save(myMDD,"src/main/resources/mdds/UMDD.mdd"); //save in file the MDD
    }
}
