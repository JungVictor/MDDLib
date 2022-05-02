package generation.bench;

import com.fasterxml.jackson.databind.ObjectMapper;
import dd.mdd.MDD;
import dd.mdd.components.StateNode;
import dd.operations.ConstraintOperation;
import generation.states.StateGCCgram;
import generation.states.StateGram;
import generation.utils.Reverso;
import utils.io.MDDReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GCCpos {

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
        reverso.initIntToPOS();

        StateGCCgram.reverso= reverso;
        StateGCCgram.maxCardinality=1;
        StateGCCgram.tagPOS="VER";

        StateGram.reverso = reverso;
        StateGram.SIZE=3;

        MDD UMDD = MDD.create();
        MDDReader.load(UMDD, "src/main/resources/mdds/UMDD.mdd");


        StateNode constraint = StateNode.create();
        constraint.setState(StateGCCgram.create());
        MDD myMDD = MDD.create();
        System.out.println("");
        System.out.println("MDD_universelle /\\ MDD_GCC");
        ConstraintOperation.intersection(myMDD, UMDD, constraint, false);
        myMDD.reduce();


        constraint = StateNode.create();
        constraint.setState(StateGram.create());
        MDD myMDD2 = MDD.create();
        System.out.println("");
        System.out.println("MDD_universelle /\\ MDD_mots");
        ConstraintOperation.intersection(myMDD2, myMDD, constraint, false);
        myMDD2.reduce();

        System.out.println("\n ________  n solutions __________");
        System.out.println(myMDD2.nSolutions());

        MDDReader.save(myMDD2, "MDDm.mdd");
        int[] resulat;
        for(int i=0;i<10;i++){
            resulat = myMDD2.randomWalk(); // precedenty myMDD
            reverso.pretty(resulat,reverso);


        }
    }
}
