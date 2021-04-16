import builder.MDDBuilder;
import builder.constraints.MDDAllDiff;
import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import problems.AllDiffKN;
import problems.CarSequencing;
import representation.MDDPrinter;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;
import utils.Logger;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Logger.out.information("BEGIN");
        MDD mdd = Memory.MDD();
        mdd.setSize(6);
        mdd.addPath(0, 1, 2, 3, 4);
        mdd.addPath(0, 2, 2, 3, 4);
        mdd.addPath(0, 2, 2, 5, 4);
        mdd.reduce();

        MDD mdd2 = Memory.MDD();
        mdd2.setSize(6);
        mdd2.addPath(0, 1, 2, 3, 4);
        mdd2.addPath(4, 3, 2, 1, 0);
        mdd2.reduce();

        MDD mdd3 = Operation.intersection(mdd, mdd2);

        mdd3.accept(new MDDPrinter());

        Memory.free(mdd);
        Memory.free(mdd2);
        Memory.free(mdd3);


        mdd = Memory.MDD();
        mdd.setSize(5);
        mdd.addPath(8, 1, 2, 5);
        mdd.reduce();

        mdd.accept(new MDDPrinter());
        Memory.free(mdd);

        PMDD pmdd = PMemory.PMDD();
        MDDBuilder.among(pmdd, 4, 2, 3);

        SetOf<Integer> V = Memory.SetOfInteger();
        V.add(1);
        pmdd.addRootProperty("Sum", PMemory.PropertySum(0, 0));
        pmdd.addRootProperty("Sequence", PMemory.PropertySequence(V, pmdd.size()));
        MapOf<String, NodeProperty> properties = pmdd.propagateProperties();
        for(String key : properties) System.out.println(key + " : " + properties.get(key));

        //Memory.free(V);

        PMDD pmdd_copy1 = (PMDD) pmdd.copy();
        PMDD pmdd_copy2 = (PMDD) pmdd.copy();

        Memory.free(pmdd_copy1);
        Memory.free(pmdd_copy2);

        PMDD seq = PMemory.PMDD();
        MDDBuilder.sequence(seq, 5, 2, 3, 10);

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

        //cs.solve(solution, 2);

        System.out.println("finished");

        Logger.out.information("START\n");
        AllDiffKN kn = new AllDiffKN(4, 70);
        PMDD sol = PMemory.PMDD();
        kn.solve(sol);
        sol.accept(new MDDPrinter());

        System.out.println(sol.nSolutions() + " " + (System.currentTimeMillis() - clock));
        Logger.out.information("FINISH");



        for(int i = 0; i < 200000; i++) {
            int[] random = sol.randomWalk();
            int[] cpt = new int[74];
            for(int j = 0; j < cpt.length; j++) {
                for(int r : random) if (r == j) cpt[j]++;
                if(cpt[j] > 1) System.out.println(Arrays.toString(random));
            }
        }

    }

}
