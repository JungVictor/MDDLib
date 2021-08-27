package builder;

import builder.constraints.*;
import mdd.MDD;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Binary;
import memory.Memory;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import structures.integers.TupleOfInt;

public class MDDBuilder {

    /* UNIVERSAL */
    public static MDD universal(MDD mdd, ArrayOfInt V, int size){
        return MDDUniversal.generate(mdd, V, size);
    }
    public static MDD universal(MDD mdd, int V, int size){
        ArrayOfInt values = ArrayOfInt.create(V);
        for(int i = 0; i < values.length; i++) values.set(i,i);

        mdd.setSize(size+1);
        Node current = mdd.getRoot();
        Node next;

        for(int i = 1; i < size+1; i++){
            next = mdd.Node();
            mdd.addNode(next, i);
            for(int v : values) mdd.addArc(current, v, next, i-1);
            current = next;
        }

        mdd.reduce();
        Memory.free(values);
        return mdd;
    }
    public static MDD universal(MDD mdd, Domains domains){
        mdd.setSize(domains.size()+1);
        Node current = mdd.getRoot();
        Node next;

        for(int i = 1; i < domains.size()+1; i++){
            next = mdd.Node();
            mdd.addNode(next, i);
            for(int v : domains.get(i-1)) mdd.addArc(current, v, next, i-1);
            current = next;
        }

        mdd.reduce();
        return mdd;
    }

    /* AMONG / SEQ */
    public static MDD among(MDD mdd, int q, int min, int max){
        return ConstraintBuilder.sum(mdd, Binary.Set(), min, max, q);
        //return MDDAmong.generate(mdd, q, min, max);
    }
    public static MDD among(MDD mdd, Domains D, SetOf<Integer> V, int q, int min, int max){
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
    public static MDD sequence(MDD mdd, Domains D, SetOf<Integer> V, int q, int min, int max, int size){
        return ConstraintBuilder.sequence(mdd, D, V, q, min, max, size);
    }

    /* SUM */
    public static MDD sum(MDD mdd, int s_min, int s_max, int n, Domains D){
        return ConstraintBuilder.sum(mdd, D, s_min, s_max, n);
        //return MDDSum.generate(mdd, s_min, s_max, n, V);
    }
    public static MDD sum(MDD mdd, int s_min, int s_max, int n, SetOf<Integer> V){
        return ConstraintBuilder.sum(mdd, V, s_min, s_max, n);
    }
    public static MDD sum(MDD mdd, int s, int n, Domains D){
        return ConstraintBuilder.sum(mdd, D, s, s, n);
    }
    public static MDD sum(MDD mdd, int s, int n, SetOf<Integer> V){
        return ConstraintBuilder.sum(mdd, V, s, s, n);
    }
    public static MDD sum(MDD mdd, int s, int n){
        return ConstraintBuilder.sum(mdd, Binary.Set(), s, s, n);
    }

    /* GCC */
    public static MDD gcc(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, Domains D){
        return ConstraintBuilder.gcc(mdd, D, couples, n);
    }

    /* ALL DIFF */
    public static MDD alldiff(MDD mdd, SetOf<Integer> V, int size){
        return ConstraintBuilder.allDiff(mdd, V, size);
        //return MDDAllDifferent.generate(mdd, V, size);
    }
    public static MDD alldiff(MDD mdd, Domains D, SetOf<Integer> V, int size){
        return ConstraintBuilder.allDiff(mdd, D, V, size);
        //return MDDAllDifferent.generate(mdd, V, C, size);
    }
    public static MDD alldiff(MDD mdd, SetOf<Integer> D, SetOf<Integer> V, int size){
        Domains domains = Domains.create();
        for(int i = 0; i < size; i++){
            domains.add(i);
            for(int v : D) domains.put(i, v);
        }
        MDD result = ConstraintBuilder.allDiff(mdd, domains, V, size);
        Memory.free(domains);
        return result;
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
