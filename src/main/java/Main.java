import builder.MDDBuilder;
import mdd.MDD;
import memory.Memory;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class Main {

    public static void main(String[] args) {

        ArrayOfInt V = Memory.ArrayOfInt(25);
        for(int i = 0; i < V.length; i++) V.set(i,i);

        MDD test = MDDBuilder.alldiff(Memory.MDD(), V, V.length);
        Logger.out.print("\r"+test.nSolutions());

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
