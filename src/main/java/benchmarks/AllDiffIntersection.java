package benchmarks;

import builder.constraints.MDDAllDifferent;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import problems.AllDiff;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class AllDiffIntersection {

    public static void main(String[] args){
        alldiff(5,6,5,10,0);
    }

    public static void alldiff(int MIN_SGROUP, int MAX_SGROUP, int MIN_NGROUP, int MAX_NGROUP, int PROBA) {

        long clock, memory, tmp_clock, tmp_mem;
        MDD test, result, randomized;
        ArrayOfInt values = Memory.ArrayOfInt(100);

        Logger.out.setInformation(false);
        Logger.out.setNormal(true);

        long[][][] results = new long[MAX_SGROUP-MIN_SGROUP+1][MAX_NGROUP-MIN_NGROUP+1][3];


        for (int sGroup = MIN_SGROUP; sGroup <= MAX_SGROUP; sGroup++) {
            for (int nGroup = MIN_NGROUP; nGroup <= MAX_NGROUP; nGroup++) {
                test = AllDiff.universal(sGroup, nGroup);

                values.setLength(sGroup * nGroup);
                for (int i = 0; i < values.length; i++) values.set(i, i);

                randomized = test.copy();
                int nArcs = AllDiff.randomArcs(randomized, PROBA);
                Logger.out.print("[" + sGroup + " " + nGroup + " " + nArcs + "] BEGIN");

                clock = time();
                memory = memory();

                //ConstraintOperation.allDiff(randomized);
                result = MDDAllDifferent.intersection(Memory.MDD(), randomized, values);

                tmp_clock = time();
                tmp_mem = memory();

                results[sGroup - MIN_SGROUP][nGroup - MIN_NGROUP][0] = tmp_clock - clock;
                results[sGroup - MIN_SGROUP][nGroup - MIN_NGROUP][1] = tmp_mem - memory;
                results[sGroup - MIN_SGROUP][nGroup - MIN_NGROUP][2] = nArcs;

                Logger.out.print("\r[" + sGroup + " " + nGroup + " " + nArcs + "] STOP : " +
                        results[sGroup - MIN_SGROUP][nGroup - MIN_NGROUP][0] + "ms, " +
                        results[sGroup - MIN_SGROUP][nGroup - MIN_NGROUP][1] + "mb \n");

                // Memory.free(result);
                // Memory.free(randomized);
            }
            // Memory.free(test);
        }
    }


    private static final long timer = System.currentTimeMillis();
    private static final Runtime rt = Runtime.getRuntime();;

    private static long memory(){
        return (rt.totalMemory() - rt.freeMemory())/1048576;
    }

    private static long time(){
        return System.currentTimeMillis() - timer;
    }

}
