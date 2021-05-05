import builder.MDDBuilder;
import builder.constraints.MDDAllDifferent;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import problems.AllDiff;
import problems.AllDiffKN;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class Main {

    public static void main(String[] args) {

        int sGroup = 8;
        int nGroup = 8;

        AllDiffKN problem = new AllDiffKN(4, 40);
        MDD test = problem.solve(Memory.MDD());

        //MDD test = AllDiff.universal(sGroup,nGroup);
        //test.accept(new MDDPrinter());


        ArrayOfInt values = Memory.ArrayOfInt(test.getV().size());
        MapOf<Integer, Integer> V = Memory.MapOfIntegerInteger();
        int vi = 0;
        for(int v : test.getV()) values.set(vi++, v);

        PMDD allDiffResult = PMemory.PMDD();
        Logger.out.information("BEGIN\n");
        MDDAllDifferent.intersection(allDiffResult, test, values);
        Logger.out.information("STOP : " + allDiffResult.nSolutions() + " solutions\n");

        Logger.out.information("BEGIN\n");
        allDiffResult = ConstraintOperation.allDiff(test);
        Logger.out.information("STOP : " + allDiffResult.nSolutions() + " solutions\n");

        Logger.out.information("BEGIN\n");
        MDDBuilder.alldiff(Memory.MDD(), values, test.size());
        Logger.out.information("STOP\n");


        /*
        seq.accept(new MDDPrinter());
        seq.addRootProperty("Sequence", PMemory.PropertySequence(V, seq.size()));
        properties = seq.propagateProperties();
        for(String key : properties) System.out.println(key + " : " + properties.get(key));

        ArrayOfInt B = Memory.ArrayOfInt(4);
        B.set(0,0); B.set(1,1); B.set(2,2); B.set(3,3);
        MatrixOfInt couples = Memory.MatrixOfInt(1, 3);
        couples.set(0, 0, 1);
        couples.set(0, 1, 2);
        couples.set(0, 2, 5);
        //couples.set(1, new int[]{3, 2, 3});

        MDD sum = MDDBuilder.gcc(Memory.MDD(), 5, couples, B);
        sum.accept(new MDDPrinter());


        CarSequencing cs = new CarSequencing(
                new int[]{1, 2, 1, 2, 1},
                new int[]{2, 3, 3, 5, 5},
                new int[][]{
                        // 0 0
                        {4, 0, 0, 0, 0, 1},
                        {8, 0, 0, 0, 1, 0},
                        {3, 0, 0, 1, 0, 0},
                        {5, 0, 0, 1, 1, 0},
                        // 0 1
                        {15, 0, 1, 0, 0, 0},
                        {4, 0, 1, 0, 0, 1},
                        {8, 0, 1, 0, 1, 0},
                        {2, 0, 1, 1, 0, 0},
                        {1, 0, 1, 1, 1, 0},
                        // 1 0
                        {10, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 1},
                        {6, 1, 0, 0, 1, 0},
                        {3, 1, 0, 0, 1, 1},
                        {2, 1, 0, 1, 0, 0},
                        {1, 1, 0, 1, 0, 1},
                        {2, 1, 0, 1, 1, 0},
                        // 1 1
                        {4, 1, 1, 0, 0, 0},
                        {2, 1, 1, 0, 0, 1},
                        {6, 1, 1, 0, 1, 0},
                        {10, 1, 1, 1, 0, 0},
                        {1, 1, 1, 1, 0, 1},
                        {1, 1, 1, 1, 1, 1},
                }, 0, 1);

        long clock = System.currentTimeMillis();
        System.out.println("Génération du MDDsol100 : en cours...");
        MDD solution = cs.solve();
        System.out.println("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs");
        solution = cs.solve(solution, 2);
        System.out.println("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs");

        System.out.println("finished");
*/

    }

}
