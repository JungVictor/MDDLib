package benchmarks;

import builder.MDDBuilder;
import builder.constraints.ConstraintBuilder;
import builder.constraints.MDDAllDifferent;
import builder.constraints.MDDGCC;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.memory.PMemory;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.TupleOfInt;
import utils.Logger;

import java.util.Random;

public class ConstraintIntersection {

    private static final Random random = new Random();

    public static void main(String[] args) {
        MapOf<Integer, TupleOfInt> tuples = Memory.MapOfIntegerTupleOfInt();
        for(int i = 0; i < 5; i++) tuples.put(i, Memory.TupleOfInt(2, 8));


        construction_sum(64, 64, 100, 2);
        //construction_gcc(tuples, 30, 10);
        //construction_seq(15, 2, 8, 50, 2, 1);
    }

    private static void construction_sum(int min, int max, int size, int V){
        SetOf<Integer> domain = Memory.SetOfInteger();
        for(int i = 0; i < V; i++) domain.add(i);
        MDD sum = ConstraintBuilder.sum(Memory.MDD(), domain, min, max, size);
        MDD sum2 = MDDBuilder.sum(Memory.MDD(), min, max, size, domain);

        System.out.println(sum.size() == sum2.size());
        System.out.println(sum.nodes() == sum2.nodes());
        System.out.println(sum.arcs() == sum2.arcs());
        System.out.println(Operation.inclusion(sum2, sum));
        System.out.println(Operation.inclusion(sum, sum2));
    }

    private static void construction_gcc(MapOf<Integer, TupleOfInt> tuples, int size, int D){
        SetOf<Integer> domain = Memory.SetOfInteger();
        for(int i = 0; i < D; i++) domain.add(i);
        MDD gcc = ConstraintBuilder.gcc(Memory.MDD(), domain, tuples, size);
    }

    private static void construction_seq(int q, int min, int max, int size, int D, int V){
        SetOf<Integer> domain = Memory.SetOfInteger(), values = Memory.SetOfInteger();
        for(int i = 0; i < D; i++) domain.add(i);
        for(int i = V; i > 0; i--) values.add(i);
        MDD seq = ConstraintBuilder.sequence(Memory.MDD(), domain, values, q, min, max, size);
        Logger.out.information("\n"+seq.arcs() + " " + seq.nodes()+"\n");

        // Verification
        MDD seq2 = MDDBuilder.sequence(Memory.MDD(), q, min, max, size);
        Logger.out.information("\n"+seq2.arcs() + " " + seq2.nodes()+"\n");
        System.out.println(Operation.inclusion(seq, seq2));
        System.out.println(Operation.inclusion(seq2, seq));
    }



    private static void sum(MDD test, int min, int max){
        SetOf<Integer> V = Memory.SetOfInteger();
        for(int v : test.getV()) V.add(v);

        MDD result1, result2;

        Logger.out.information("#Solutions : " + test.nSolutions() + "\n");

        Logger.out.information("BEGIN\n");
        result1 = ConstraintOperation.sum(Memory.MDD(), test, min, max);
        Logger.out.information("STOP : " + result1.nSolutions() + " solutions\n");

        result2 = Memory.MDD();

        Logger.out.information("BEGIN\n");
        MDD sum = MDDBuilder.sum(Memory.MDD(), min, max, test.size(), V);
        Operation.intersection(result2, test, sum);
        Logger.out.information("STOP : " + result2.nSolutions() + " solutions\n");

        System.out.println(Operation.inclusion(result1, result2));
        System.out.println(Operation.inclusion(result2, result1));

    }

    private static void alldiff(MDD test){
        ArrayOfInt V = Memory.ArrayOfInt(test.getV().size());
        int i = 0;
        for(int v : test.getV()) V.set(i++, v);

        MDD result;

        Logger.out.information("#Solutions : " + test.nSolutions() + "\n");

        Logger.out.information("BEGIN\n");
        result = ConstraintOperation.allDiff(Memory.MDD(), test);
        Logger.out.information("STOP : " + result.nSolutions() + " solutions\n");

        result = PMemory.PMDD();

        Logger.out.information("BEGIN\n");
        MDD sum = MDDAllDifferent.generate(Memory.MDD(), V, test.size());
        Operation.intersection(result, test, sum);
        Logger.out.information("STOP : " + result.nSolutions() + " solutions\n");
    }

    private static void gcc(MDD test, MapOf<Integer, TupleOfInt> tuples){
        ArrayOfInt V = Memory.ArrayOfInt(test.getV().size());
        int i = 0;
        for(int v : test.getV()) V.set(i++, v);

        MDD result;

        Logger.out.information("#Solutions : " + test.nSolutions() + "\n");

        Logger.out.information("BEGIN\n");
        result = ConstraintOperation.gcc(Memory.MDD(), test, tuples);
        Logger.out.information("STOP : " + result.nSolutions() + " solutions\n");

        result = PMemory.PMDD();

        Logger.out.information("BEGIN\n");
        MDD sum = MDDGCC.generate(Memory.MDD(), test.size(), tuples, V);
        Operation.intersection(result, test, sum);
        Logger.out.information("STOP : " + result.nSolutions() + " solutions\n");
    }


}
