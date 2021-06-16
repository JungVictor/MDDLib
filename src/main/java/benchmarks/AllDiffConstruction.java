package benchmarks;

import builder.MDDBuilder;
import builder.constraints.MDDAllDifferent;
import memory.Memory;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class AllDiffConstruction {

    public static void main(String[] args) {
        int size = 25;
        ArrayOfInt V;
        if(args.length > 1) {
            size = Integer.parseInt(args[0]);
            V = Memory.ArrayOfInt(args.length - 1);
            for(int i = 0; i < V.length; i++) V.set(i, Integer.parseInt(args[i+1]));
        } else {
            if(args.length == 1) size = Integer.parseInt(args[0]);
            V = Memory.ArrayOfInt(size);
            for(int i = 0; i < size; i++) V.set(i,i);
        }

        Logger.out.information("SIZE : " + size + ", V = " + V + "\n");
        MDDAllDifferent.generate(Memory.MDD(), V, size);
        Logger.out.information("END");
    }

}
