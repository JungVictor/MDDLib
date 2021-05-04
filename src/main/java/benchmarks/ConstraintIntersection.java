package benchmarks;

import builder.MDDBuilder;
import builder.constraints.MDDAllDifferent;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.memory.PMemory;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class ConstraintIntersection {

    public static void main(String[] args) {

    }


    private static void sum(MDD test, int min, int max){
        ArrayOfInt V = Memory.ArrayOfInt(test.getV().size());
        int i = 0;
        for(int v : test.getV()) V.set(i++, v);

        PMDD result;

        Logger.out.information("BEGIN\n");
        result = ConstraintOperation.allDiff(test);
        Logger.out.information("STOP : " + result.nSolutions() + " solutions\n");

        Logger.out.information("BEGIN\n");
        MDDBuilder.sum(Memory.MDD(), min, max, test.size(), V);
        Logger.out.information("STOP\n");
    }

}
