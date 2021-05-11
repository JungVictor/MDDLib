import builder.constraints.MDDAllDifferent;
import builder.constraints.MDDGCC;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import problems.AllDiff;
import problems.AllDiffKN;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;
import structures.integers.TupleOfInt;
import utils.Logger;

public class Main {

    public static void main(String[] args) {

        ArrayOfInt V = Memory.ArrayOfInt(20);
        MapOf<Integer, TupleOfInt> couples = Memory.MapOfIntegerTupleOfInt();

        for(int i = 0; i < 20; i++) {
            V.set(i,i);
            couples.put(i, Memory.TupleOfInt(0,10));
        }

        Logger.out.print("BEGIN");
        MDDGCC.generate_(Memory.MDD(), 50, couples, V);
        Logger.out.print("END");

        Logger.out.print("BEGIN");
        MDDGCC.generate(Memory.MDD(), 50, couples, V);
        Logger.out.print("END");

        /*
        //AllDiffKN kn = new AllDiffKN(4, 70);
        MDD sol = AllDiff.alldiff(9,9);

        MapOf<Integer, TupleOfInt> couples = Memory.MapOfIntegerTupleOfInt();
        ArrayOfInt V = Memory.ArrayOfInt(sol.getV().size());
        int i = 0;
        for(int v : sol.getV()) {
            couples.put(i, Memory.TupleOfInt(1,1));
            V.set(i++, v);
        }

        MDD solution;

        long clock = System.currentTimeMillis();
        Logger.out.information("BEGIN\n");
        //solution = ConstraintOperation.allDiff(Memory.MDD(), sol);
        solution = MDDGCC.intersection(Memory.MDD(), sol, couples);
        Logger.out.information("\rSTOP : " + (System.currentTimeMillis() - clock) + "ms " + solution.nSolutions() + "\n");

        clock = System.currentTimeMillis();
        Logger.out.information("BEGIN\n");
        solution = MDDAllDifferent.intersection(Memory.MDD(), sol, V);
        Logger.out.information("\rSTOP : " + (System.currentTimeMillis() - clock) + "ms " + solution.nSolutions() + "\n");
        */

    }

}
