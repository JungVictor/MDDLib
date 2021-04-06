package builder.constraints;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import structures.generics.ArrayOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;

import java.util.HashSet;

public class MDDGCC {

    private MDDGCC(){}

    public static MDD generate(MDD mdd, int n, MatrixOfInt couples, ArrayOfInt V){
        ArrayOfInt B = Memory.ArrayOfInt(2);
        B.set(0,0); B.set(1,1);

        ArrayOf<MDD> sums = Memory.ArrayOfMDD(couples.getHeight());
        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();
        int min, max;
        for(int i = 0; i < sums.length; i++) {
            V0.clear(); V1.clear();
            for(int v : V) if(v != couples.get(i, 0)) V0.add(v);
            V1.add(couples.get(i, 0));
            min = couples.get(i, 1);
            if(couples.getLength() == 3) max = couples.get(i, 2);
            else max = min;
            sums.set(i, MDDBuilder.sum(mdd.MDD(), min, max, n, B));
            sums.get(i).replace(V0, V1);
        }
        Operation.intersection(mdd, sums);

        for(int i = 0; i < sums.length; i++) Memory.free(sums.get(i));
        Memory.free(sums);
        Memory.free(V0);
        Memory.free(V1);
        Memory.free(B);
        return mdd;
    }

}
