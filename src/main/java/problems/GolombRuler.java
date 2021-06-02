package problems;

import builder.MDDBuilder;
import builder.constraints.ConstraintBuilder;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import mdd.operations.Operation;
import memory.Memory;
import representation.MDDPrinter;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

public class GolombRuler {

    public static MDD generate(MDD result, int domain, int size){
        SetOf<Integer> D = Memory.SetOfInteger();
        for(int i = 0; i < domain+1; i++) D.add(i);
        MDD tmp = ConstraintBuilder.diff(Memory.MDD(), D, 1, size);
        MDD old = tmp;
        for(int i = 2; i <= size; i++) {
            //tmp.accept(new MDDPrinter());
            tmp = ConstraintOperation.diff(Memory.MDD(), tmp, i);
            Memory.free(old);
            old = tmp;
        }
        ArrayOfInt V = Memory.ArrayOfInt(domain+1);
        for (int i = 0; i < domain+1; i++) V.set(i,i);
        Operation.intersection(result, tmp, MDDBuilder.gt(Memory.MDD(), tmp.size(), V));
        return result;
    }

}
