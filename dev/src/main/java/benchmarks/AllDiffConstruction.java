package benchmarks;

import builder.MDDBuilder;
import builder.constraints.MDDAllDifferent;
import dd.mdd.MDD;
import memory.Memory;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOf;
import utils.Logger;

public class AllDiffConstruction {

    public static void main(String[] args) {
        int size = 20;
        ArrayOfInt V;
        SetOf<Integer> V_ = Memory.SetOfInteger();
        if(args.length > 1) {
            size = Integer.parseInt(args[0]);
            V = ArrayOfInt.create(args.length - 1);
            for(int i = 0; i < V.length; i++) {
                V.set(i, Integer.parseInt(args[i+1]));
                V_.add(V.get(i));
            }
        } else {
            if(args.length == 1) size = Integer.parseInt(args[0]);
            V = ArrayOfInt.create(size);
            for(int i = 0; i < size; i++) {
                V.set(i,i);
                V_.add(i);
            }
        }

        Logger.out.information("SIZE : " + size + ", V = " + V + "\n");
        MDDBuilder.alldiff(MDD.create(), V_, size);
        //MDDAllDifferent.generate(MDD.create(), V, size);
        Logger.out.information("END");
    }

}
