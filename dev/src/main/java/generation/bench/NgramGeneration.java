package generation.bench;

import com.fasterxml.jackson.databind.ObjectMapper;
import dd.mdd.MDD;
import dd.mdd.components.StateNode;
import dd.operations.ConstraintOperation;
import generation.states.StateGram;
import generation.utils.Reverso;
import utils.io.MDDReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NgramGeneration {

    public static void main(String[] args) throws Exception {


        String inputFile="src/main/resources/dataText/contes#@.json";
        String reversoFile = inputFile + "_reverso.json";



        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(inputFile);
        Map<String, Integer> mydict = objectMapper.readValue(file, HashMap.class);
        //Reverso reverso = new Reverso((HashMap<String, Integer>) mydict);
        //objectMapper.writeValue(new File("./"+reversoFile), reverso);

        Reverso reverso = objectMapper.readValue(new File("./" + reversoFile), Reverso.class);

        reverso.buildIsValid(mydict.keySet());
        reverso.buildCost();



        StateGram.reverso = reverso;
        StateGram.SIZE=5;

        MDD UMDD = MDD.create();
        MDDReader.load(UMDD, "src/main/resources/mdds/UMDD.mdd");

        StateNode constraint = StateNode.create();
        constraint.setState(StateGram.create());
        MDD myMDD = MDD.create();

        System.out.println("");
        System.out.println("MDD_universelle /\\ MDD_mots");
        ConstraintOperation.intersection(myMDD, UMDD, constraint, false);
        myMDD.reduce();

        System.out.println("\n ________  n solutions __________");
        System.out.println(myMDD.nSolutions());

        MDDReader.save(myMDD, "MDDm.mdd");
        int[] resulat;
        for(int i=0;i<10;i++){
            resulat = myMDD.randomWalk(); // precedenty myMDD
            reverso.pretty(resulat,reverso);


        }
    }
}
