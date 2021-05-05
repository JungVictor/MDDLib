package benchmarks;

import builder.constraints.MDDAllDifferent;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import problems.AllDiff;
import structures.integers.ArrayOfInt;
import utils.Logger;

import java.util.Arrays;

public class AllDiffIntersection {

    public static void main(String[] args){
        alldiff(5,10,5,10,0);
    }

    public static void alldiff(int MIN_SGROUP, int MAX_SGROUP, int MIN_NGROUP, int MAX_NGROUP, int PROBA) {

        long clock, memory, tmp_clock, tmp_mem;
        MDD test, result;
        ArrayOfInt values = Memory.ArrayOfInt(100);

        Logger.out.setInformation(false);
        Logger.out.setNormal(true);

        long[][][] results = new long[3][MAX_NGROUP-MIN_NGROUP+1][MAX_SGROUP-MIN_SGROUP+1];


        for (int sGroup = MIN_SGROUP; sGroup <= MAX_SGROUP; sGroup++) {
            for (int nGroup = MIN_NGROUP; nGroup <= MAX_NGROUP; nGroup++) {
                test = AllDiff.universal(sGroup, nGroup);

                values.setLength(sGroup * nGroup);
                for (int i = 0; i < values.length; i++) values.set(i, i);

                int nArcs = AllDiff.randomArcs(test, PROBA);
                Logger.out.print("[" + sGroup + " " + nGroup + " " + nArcs + "] BEGIN");

                clock = time();
                memory = memory();

                //ConstraintOperation.allDiff(test);
                result = MDDAllDifferent.intersection(Memory.MDD(), test, values);

                tmp_clock = time();
                tmp_mem = memory();

                results[0][nGroup - MIN_NGROUP][sGroup - MIN_SGROUP] = tmp_clock - clock;
                results[1][nGroup - MIN_NGROUP][sGroup - MIN_SGROUP] = tmp_mem - memory;
                results[2][nGroup - MIN_NGROUP][sGroup - MIN_SGROUP] = nArcs;

                Logger.out.print("\r[" + sGroup + " " + nGroup + " " + nArcs + "] STOP : " +
                        results[0][nGroup - MIN_NGROUP][sGroup - MIN_SGROUP] + "ms, " +
                        results[1][nGroup - MIN_NGROUP][sGroup - MIN_SGROUP] + "mb \n");

                // Memory.free(result);
                // Memory.free(randomized);
            }
            // Memory.free(test);
        }
        for(int j = 0; j < results.length; j++){
            for(int k = 0; k < results[j].length; j++) {
                System.out.print(k+MIN_NGROUP+";");
                for(int i = 0; i < results[j][k].length; i++) {
                    System.out.print(results[j][k][i]+";");
                }
                System.out.println();
            }
            System.out.println();
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
