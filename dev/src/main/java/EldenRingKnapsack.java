import builder.MDDBuilder;
import dd.mdd.MDD;
import dd.mdd.pmdd.PMDD;
import dd.mdd.pmdd.components.PNode;
import dd.mdd.pmdd.components.properties.PropertySum;
import dd.operations.ConstraintOperation;
import memory.Memory;
import representation.MDD2Dot;
import structures.Domains;
import structures.generics.MapOf;
import utils.ArgumentParser;
import utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EldenRingKnapsack {

    private static HashMap<String, Integer> nameToIndex;
    private static HashMap<Integer, String> indexToName;
    private static final HashMap<String, ArrayList<Integer>>[] dObjects = new HashMap[4];

    // Data indexes
    private static final int NAME = 0,
            PHY = 1, STRIKE = 2, SLASH = 3, PIERCE = 4,
            MAGIC = 5, FIRE = 6, LIGHT = 7, HOLY = 8,
            IMMUNITY = 9, ROBUSTNESS = 10, FOCUS = 11, VITALITY = 12,
            POISE = 13, WEIGHT = 14, TYPE = 15, VALUE = 15;

    // Type of armor
    private static final int HEAD = 0, BODY = 1, ARM = 2, LEG = 3;


    // Stats names
    private static final String[] statName = {
            "Physical", "Strike", "Slash", "Pierce",
            "Magic", "Fire", "Lightning", "Holy",
            "Immunity", "Robustness", "Focus", "Vitality", "Poise", "Weight"
    };

    /**
     * 0 : Max Weight
     * 1--14: Objective function stats
     * 15--29: Minimum stats required
     * @param args
     */
    public static void main(String[] args) {

        Logger.out.setInformation(false);

        nameToIndex = new HashMap<>();
        indexToName = new HashMap<>();

        final int MAX_WEIGHT = (int) (Double.parseDouble(args[0]) * 10);

        final int[] requirements = new int[14];
        for (int i = 0; i < requirements.length; i++) {
            if(i+15 < args.length) requirements[i] = (int) (Double.parseDouble(args[i+15])*10);
            else requirements[i] = Integer.MIN_VALUE;
        }

        final int[] function = new int[14];
        for (int i = 0; i < function.length; i++) function[i] = (int) (Double.parseDouble(args[i+1])*100);
        printArguments(MAX_WEIGHT, function, requirements);

        Domains D = parseData("data/Elden", function);

        MapOf<Integer, Integer> mapWeight = getMap(WEIGHT);
        MDD result = MDD.create();
        MDDBuilder.sum(result, 0, MAX_WEIGHT, 4, D, mapWeight, null);
        for(int i = 0; i < requirements.length; i++) {
            if(requirements[i] > Integer.MIN_VALUE) {
                result = addRequirement(result, i+1, requirements[i]);
            }
        }

        if(result.nSolutions() == 0) {
            System.out.println("NO SOLUTION : Requirements impossible to fulfill");
            return;
        }

        PMDD pResult = PMDD.create();
        result.clearAllAssociations();
        result.copy(pResult);

        MapOf<Integer, Integer> mapValue = getMap(VALUE);
        pResult.addRootProperty("value", PropertySum.create(0, 0, mapValue));
        PropertySum resultValue = (PropertySum) pResult.propagateProperties().get("value");
        int BEST_VALUE = (int) resultValue.getData().get(1);
        result = addRequirement(result, VALUE, BEST_VALUE);

        printSolutions(result);

        // OUTPUT
        try { MDD2Dot.convert(result,  "eldenRing", indexToName); }
        catch (IOException e) { e.printStackTrace();}
    }

    private static void printSolutions(MDD result){
        System.out.println();
        ArrayList<int[]> solutions = result.extractSolutions();
        for (int[] solution : solutions){
            double[] stats = new double[14];
            System.out.println();
            System.out.println("==========================");
            for(int i = 0; i < solution.length; i++) {
                System.out.println(indexToName.get(solution[i]));
                ArrayList<Integer> objectStats = dObjects[i].get(indexToName.get(solution[i]));
                for(int j = 0; j < stats.length; j++) stats[j] += objectStats.get(j+1);
            }
            System.out.println("--------------------------");
            printStats(stats);
            System.out.println("==========================");
        }
    }

    private static void printStats(double[] stats){
        for(int i = 0; i < stats.length; i++){
            if(i >= 1 && i <= 3) System.out.print("-- VS ");
            System.out.print(statName[i]);
            System.out.print(" : ");
            System.out.println(stats[i] / 10.0);
        }
    }

    private static Domains parseData(String file, int[] function){
        Domains D = Domains.create(4);

        // Data
        HashMap<String, ArrayList<Integer>> dHead = new HashMap<>();
        HashMap<String, ArrayList<Integer>> dBody = new HashMap<>();
        HashMap<String, ArrayList<Integer>> dArm = new HashMap<>();
        HashMap<String, ArrayList<Integer>> dLeg = new HashMap<>();

        dObjects[HEAD] = dHead;
        dObjects[BODY] = dBody;
        dObjects[ARM] = dArm;
        dObjects[LEG] = dLeg;

        try {
            String[] files = {"Head", "Body", "Arm", "Legs"};
            int idx = 0;
            for(int f = 0; f < files.length; f++) {
                Scanner reader = new Scanner(new File(file+"_"+files[f]+".csv"));
                // Dump first line (header)
                //reader.nextLine();
                String object;
                while (reader.hasNextLine()) {
                    object = reader.nextLine();
                    String[] exploded = object.split(";");
                    ArrayList<Integer> data = new ArrayList<>();

                    if(!exploded[NAME].equals("Silver Tear Mask") && f == HEAD) continue;
                    //if(!exploded[NAME].equals("Beast Champion Armor") && f == BODY) continue;
                    //if(!exploded[NAME].equals("Beast Champion Gauntlets") && f == ARM) continue;
                    //if(!exploded[NAME].equals("Beast Champion Greaves") && f == LEG) continue;

                    String name = exploded[NAME] + " (" + files[f] + ")";
                    nameToIndex.put(name, idx);
                    indexToName.put(idx, name);

                    data.add(idx);
                    // Not counting first (name) and last (type) values
                    for (int i = 1; i < exploded.length; i++) {
                        if (exploded[i].isBlank()) {
                            if (i == WEIGHT) exploded[i] = "100";
                            else exploded[i] = "0";
                        }
                        //System.out.println(f+ " " + exploded[i]);
                        data.add((int) (Double.parseDouble(exploded[i]) * 10));
                    }
                    data.add(value(data, function));
                    D.put(f, idx);
                    dObjects[f].put(name, data);
                    idx++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return D;
    }

    private static MDD addRequirement(MDD current, int type, int min){
        MDD result = MDD.create();
        MapOf<Integer, Integer> mapType = getMap(type);
        ConstraintOperation.sum(result, current, min, Integer.MAX_VALUE, mapType, null);
        Memory.free(current);
        return result;
    }

    private static MapOf<Integer, Integer> getMap(int type){
        MapOf<Integer, Integer> map = Memory.MapOfIntegerInteger();
        for (HashMap<String, ArrayList<Integer>> dObject : dObjects) {
            for (String object : dObject.keySet()) {
                map.put(nameToIndex.get(object), dObject.get(object).get(type));
            }
        }
        return map;
    }

    private static int value(ArrayList<Integer> object, int[] function){
        int value = 0;
        for(int i = 0; i < function.length; i++) value += function[i] * object.get(i+1);
        return value;
    }

    private static void printArguments(int w, int[] function, int[] requirements){
        String optimisation = "Optimising : ";
        boolean addition = false;
        for(int i = 0; i < function.length; i++) {
            if(function[i] == 0) continue;
            if(addition) optimisation += " + ";
            if(function[i] == 100) optimisation += statName[i];
            else optimisation += (function[i]/100.0) + "*" + statName[i];
            addition = true;
        }
        System.out.println(optimisation);
        System.out.println("Requirement(s) :\n\tWeight < " + (w/10.0));
        for(int i = 0; i < requirements.length; i++) {
            if(requirements[i] > Integer.MIN_VALUE) System.out.println("\t" + statName[i] + " > " + (requirements[i]/10.0));
        }
    }

}
