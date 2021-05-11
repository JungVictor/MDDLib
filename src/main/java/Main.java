import builder.MDDBuilder;
import mdd.MDD;
import mdd.operations.ConstraintMDD;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import representation.MDDPrinter;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class Main {

    public static void main(String[] args) {

        int size = 100;

        PMDD seq = PMemory.PMDD();
        MDDBuilder.sequence(seq, 5, 2, 3, size);

        double nSol1 = seq.nSolutions();

        MDD univ = MDDBuilder.universal(Memory.MDD(), 2, size);

        ConstraintMDD constraint = new ConstraintMDD();
        SetOf<Integer> V1 = Memory.SetOfInteger(), V0 = Memory.SetOfInteger();
        V1.add(1); V0.add(0);
        constraint.sequence(5, 2, 3, V1);
        PMDD solution = PMemory.PMDD();
        constraint.intersection(solution, univ);

        PMDD propagate = solution;

        //propagate.addRootProperty("seq", PMemory.PropertySequence(V1, propagate.size()));
        propagate.addRootProperty("sum", PMemory.PropertySum(0, 0));
        MapOf<String, NodeProperty> properties = propagate.propagateProperties();
        for(String property : properties) System.out.println(properties.get(property).getResult());

        double nSol2 = solution.nSolutions();

        System.out.println(Operation.inclusion(seq, solution));
        System.out.println(Operation.inclusion(solution, seq));

        System.out.println(nSol1 + " " + nSol2);

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
