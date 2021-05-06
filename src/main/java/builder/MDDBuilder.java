package builder;

import builder.constraints.*;
import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import structures.Binder;
import structures.generics.ArrayOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;

public class MDDBuilder {

    /* UNIVERSAL */
    public static MDD universal(MDD mdd, ArrayOfInt V, int size){
        return MDDUniversal.generate(mdd, V, size);
    }
    public static MDD universal(MDD mdd, int V, int size){
        ArrayOfInt values = Memory.ArrayOfInt(V);
        for(int i = 0; i < values.length; i++) values.set(i,i);
        MDD universal = MDDUniversal.generate(mdd, values, size);
        Memory.free(values);
        return universal;
    }

    /* AMONG / SEQ */
    public static MDD among(MDD mdd, int q, int min, int max){
        return MDDAmong.generate(mdd, q, min, max);
    }
    public static MDD sequence(MDD mdd, int q, int min, int max, int size){
        return MDDSequence.generate(mdd, q, min, max, size);
    }

    /* SUM */
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

    /* GCC */
    public static MDD gcc(MDD mdd, int n, MatrixOfInt couples, ArrayOfInt V){
        return MDDGCC.generate(mdd, n, couples, V);
    }

    /* ALL DIFF */
    public static MDD alldiff(MDD mdd, ArrayOfInt V, int size){
        return MDDAllDifferent.generate(mdd, V, size);
    }
    public static MDD alldiff(MDD mdd, ArrayOfInt V, ArrayOfInt C, int size){
        return MDDAllDifferent.generate(mdd, V, C, size);
    }


    /* LT / LEQ / GT / GEQ / EQ / NEQ */
    public static MDD lt(MDD mdd, int size, ArrayOfInt V){
        return MDDCompareNextValue.generate(mdd, size, V, MDDCompareNextValue.OP.LT);
    }
    public static MDD gt(MDD mdd, int size, ArrayOfInt V){
        return MDDCompareNextValue.generate(mdd, size, V, MDDCompareNextValue.OP.GT);
    }
    public static MDD leq(MDD mdd, int size, ArrayOfInt V){
        return MDDCompareNextValue.generate(mdd, size, V, MDDCompareNextValue.OP.LEQ);
    }
    public static MDD geq(MDD mdd, int size, ArrayOfInt V){
        return MDDCompareNextValue.generate(mdd, size, V, MDDCompareNextValue.OP.GEQ);
    }
    public static MDD eq(MDD mdd, int size, ArrayOfInt V){
        return MDDCompareNextValue.generate(mdd, size, V, MDDCompareNextValue.OP.EQ);
    }
    public static MDD neq(MDD mdd, int size, ArrayOfInt V){
        return MDDCompareNextValue.generate(mdd, size, V, MDDCompareNextValue.OP.NEQ);
    }
}
