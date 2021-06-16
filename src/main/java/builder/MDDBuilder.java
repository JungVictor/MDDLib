package builder;

import builder.constraints.*;
import mdd.MDD;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Binary;
import memory.Memory;
import structures.Binder;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;
import structures.integers.TupleOfInt;

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
        return ConstraintBuilder.sum(mdd, Binary.Set(), min, max, q);
        //return MDDAmong.generate(mdd, q, min, max);
    }
    public static MDD among(MDD mdd, SetOf<Integer> D, SetOf<Integer> V, int q, int min, int max){
        return ConstraintBuilder.sequence(mdd, D, V, q, min, max, q);
    }

    // Special case in binary, it's faster to do it this way ! (because reduction...)
    public static MDD sequence(MDD mdd, int q, int min, int max, int size){
        size++;
        MDD among = MDDBuilder.sum(mdd.MDD(), min, max, q, Binary.Set());
        MDD amongN = among.copy();
        MDD old_amongN = amongN;

        while(amongN.size() < size) {
            amongN = Operation.concatenate(amongN, among);
            Memory.free(old_amongN);
            old_amongN = amongN;
        }

        Memory.free(among);

        int Q = Math.min(size - q, q);
        MDD accumulator = amongN.copy();
        MDD old_accumulator = accumulator;
        for(int i = 1; i < Q - 1; i++) {
            accumulator = Operation.intersection(accumulator, amongN, i, size, size);
            Memory.free(old_accumulator);
            old_accumulator = accumulator;
        }

        Operation.intersection(mdd, accumulator, amongN, Q-1, size, size);

        Memory.free(amongN);
        Memory.free(accumulator);

        return mdd;
    }
    public static MDD sequence(MDD mdd, SetOf<Integer> D, SetOf<Integer> V, int q, int min, int max, int size){
        return ConstraintBuilder.sequence(mdd, D, V, q, min, max, size);
    }

    /* SUM */
    public static MDD sum(MDD mdd, int s_min, int s_max, int n, SetOf<Integer> V){
        return ConstraintBuilder.sum(mdd, V, s_min, s_max, n);
        //return MDDSum.generate(mdd, s_min, s_max, n, V);
    }
    public static MDD sum(MDD mdd, int s, int n, SetOf<Integer> V){
        return ConstraintBuilder.sum(mdd, V, s, s, n);
    }
    public static MDD sum(MDD mdd, int s, int n){
        return ConstraintBuilder.sum(mdd, Binary.Set(), s, s, n);
    }

    /* GCC */
    public static MDD gcc(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, SetOf<Integer> D){
        return ConstraintBuilder.gcc(mdd, D, couples, n);
    }

    /* ALL DIFF */
    public static MDD alldiff(MDD mdd, SetOf<Integer> V, int size){
        return ConstraintBuilder.allDiff(mdd, V, V, size);
        //return MDDAllDifferent.generate(mdd, V, size);
    }
    public static MDD alldiff(MDD mdd, SetOf<Integer> D, SetOf<Integer> V, int size){
        return ConstraintBuilder.allDiff(mdd, D, V, size);
        //return MDDAllDifferent.generate(mdd, V, C, size);
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
