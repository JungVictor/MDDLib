package benchmarks;

import builder.constraints.MDDAllDifferent;
import mdd.MDD;
import structures.arrays.ArrayOfInt;
import utils.Logger;

public class AllDiffConstruction {

    public static void main(String[] args) {
        int size = 25;
        ArrayOfInt V;
        if(args.length > 1) {
            size = Integer.parseInt(args[0]);
            V = ArrayOfInt.create(args.length - 1);
            for(int i = 0; i < V.length; i++) V.set(i, Integer.parseInt(args[i+1]));
        } else {
            if(args.length == 1) size = Integer.parseInt(args[0]);
            V = ArrayOfInt.create(size);
            for(int i = 0; i < size; i++) V.set(i,i);
        }

        Logger.out.information("SIZE : " + size + ", V = " + V + "\n");
        MDDAllDifferent.generate(MDD.create(), V, size);
        Logger.out.information("END");
    }

}
