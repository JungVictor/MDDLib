package builder;

import builder.constraints.*;
import mdd.MDD;
import memory.Memory;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;

public class MDDBuilder {

    public static MDD universal(MDD mdd, ArrayOfInt V, int size){
        return MDDUniversal.generate(mdd, V, size);
    }
    public static MDD among(MDD mdd, int q, int min, int max){
        return MDDAmong.generate(mdd, q, min, max);
    }
    public static MDD sequence(MDD mdd, int q, int min, int max, int size){
        return MDDSequence.generate(mdd, q, min, max, size);
    }
    public static MDD sum(MDD mdd, int s_min, int s_max, int n, ArrayOfInt V){
        return MDDSum.generate(mdd, s_min, s_max, n, V);
    }
    public static MDD sum(MDD mdd, int s, int n, ArrayOfInt V){
        return MDDSum.generate(mdd, s, s, n, V);
    }
    public static MDD sum(MDD mdd, int s, int n){
        ArrayOfInt V = Memory.ArrayOfInt(2);
        V.set(0, 0); V.set(1, 1);
        MDDSum.generate(mdd, s, s, n, V);
        Memory.free(V);
        return mdd;
    }
    public static MDD gcc(MDD mdd, int n, MatrixOfInt couples, ArrayOfInt V){
        return MDDGCC.generate(mdd, n, couples, V);
    }
    public static MDD alldiff(MDD mdd, ArrayOfInt V, int size){
        return MDDAllDiff.generate(mdd, V, size);
    }
    public static MDD alldiff(MDD mdd, ArrayOfInt V, ArrayOfInt C, int size){
        return MDDAllDiff.generate(mdd, V, C, size);
    }
}
