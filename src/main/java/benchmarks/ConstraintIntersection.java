package benchmarks;

import builder.MDDBuilder;
import builder.constraints.MDDAllDifferent;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.memory.PMemory;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class ConstraintIntersection {

    public static void main(String[] args) {
        MDD test = MDDBuilder.sequence(Memory.MDD(), 6, 2, 3, 90);
        sum(test, 35, 35);
    }


    private static void sum(MDD test, int min, int max){
        ArrayOfInt V = Memory.ArrayOfInt(test.getV().size());
        int i = 0;
        for(int v : test.getV()) V.set(i++, v);

        PMDD result;

        Logger.out.information("#Solutions : " + test.nSolutions() + "\n");

        Logger.out.information("BEGIN\n");
        result = ConstraintOperation.sum(test, min, max);
        Logger.out.information("STOP : " + result.nSolutions() + " solutions\n");

        result = PMemory.PMDD();

        Logger.out.information("BEGIN\n");
        MDD sum = MDDBuilder.sum(Memory.MDD(), min, max, test.size(), V);
        Operation.intersection(result, test, sum);
        Logger.out.information("STOP : " + result.nSolutions() + " solutions\n");
    }

}
