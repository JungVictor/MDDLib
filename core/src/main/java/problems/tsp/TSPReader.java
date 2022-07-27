package problems.tsp;

import dd.mdd.MDD;
import memory.Memory;
import structures.generics.MapOf;
import structures.generics.SetOf;

public class TSPReader {

    private TSPReader(){}

    public static MDD xml(MDD result, String file){
        MapOf<Integer, SetOf<Integer>> edges = Memory.MapOfIntegerSetOfInteger();

        

        for(SetOf<Integer> sets : edges.values()) Memory.free(edges);
        Memory.free(edges);
        return result;
    }

}
