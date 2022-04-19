package generation.bench;

import com.fasterxml.jackson.databind.ObjectMapper;
import dd.mdd.MDD;
import dd.mdd.components.SNode;
import dd.operations.ConstraintOperation;
import generation.states.StateGCCgram;
import generation.states.StateGram;
import generation.utils.Reverso;
import utils.io.MDDReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GCCposMultiple {

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


        StateGCCgram.maxCardinality=1;
        StateGCCgram.tagPOS="VER";
        SNode constraint = SNode.create();
        constraint.setState(StateGCCgram.create());
        MDD myMDD = MDD.create();
        System.out.println("");
        System.out.println("MDD_universelle /\\ MDD_GCC_1_VER");
        ConstraintOperation.intersection(myMDD, UMDD, constraint, false);
        myMDD.reduce();

        StateGCCgram.maxCardinality=1;
        StateGCCgram.tagPOS="VER:pre";
        constraint = SNode.create();
        constraint.setState(StateGCCgram.create());
        MDD myMDD2 = MDD.create();
        System.out.println("");
        System.out.println("MDD_GCC_1_VER /\\ MDD_GCC_1_VER:PRES");
        ConstraintOperation.intersection(myMDD2, myMDD, constraint, false);
        myMDD2.reduce();

        StateGCCgram.maxCardinality=0;
        StateGCCgram.tagPOS="KON";
        constraint = SNode.create();
        constraint.setState(StateGCCgram.create());
        myMDD = MDD.create();
        System.out.println("");
        System.out.println("MDD_GCC_1_VER /\\ MDD_GCC_1_VER:PRES");
        ConstraintOperation.intersection(myMDD, myMDD2, constraint, false);
        myMDD.reduce();


        constraint = SNode.create();
        constraint.setState(StateGram.create());
        MDD myMDDf = MDD.create();
        System.out.println("");
        System.out.println("MDD_GCC /\\ MDD_mots");
        ConstraintOperation.intersection(myMDDf, myMDD, constraint, false);
        myMDDf.reduce();

        System.out.println("\n ________  n solutions __________");
        System.out.println(myMDDf.nSolutions());

        MDDReader.save(myMDDf, "MDDm.mdd");
        int[] resulat;
        for(int i=0;i<10;i++){
            resulat = myMDDf.randomWalk(); // precedenty myMDD
            reverso.pretty(resulat,reverso);


        }
    }
}
