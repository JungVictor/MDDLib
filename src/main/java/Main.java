import builder.MDDBuilder;
import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import representation.MDDPrinter;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

public class Main {

    public static void main(String[] args) {
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


        MDD sum = MDDBuilder.sum(Memory.MDD(), 4, 10);
        sum.accept(new MDDPrinter());
    }

}
