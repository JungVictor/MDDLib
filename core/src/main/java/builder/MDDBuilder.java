package builder;

import builder.constraints.*;
import mdd.MDD;
import mdd.components.Node;
import mdd.operations.ConstraintOperation;
import mdd.operations.Operation;
import memory.Binary;
import memory.Memory;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import structures.tuples.TupleOfInt;

import java.math.BigInteger;

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
        return sum(mdd, min, max, q, Binary.Set());
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
        Domains D = Domains.create();
        for(int i = 0; i < n; i++) {
            D.add(i);
            D.get(i).add(V);
        }
        sum(mdd, s_min, s_max, n, D);
        Memory.free(D);
        return mdd;
    }
    public static MDD sum(MDD mdd, int s, int n, Domains D){
        return sum(mdd, s, s, n, D);
    }
    public static MDD sum(MDD mdd, int s, int n, SetOf<Integer> V){
        return sum(mdd, s, s, n, V);
    }
    public static MDD sum(MDD mdd, int s, int n){
        return sum(mdd, s, s, n, Binary.Set());
    }

    /* GCC */
    public static MDD gcc(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, Domains D){
        return ConstraintBuilder.gcc(mdd, D, couples, n);
    }

    /* ALL DIFF */
    public static MDD alldiff(MDD mdd, SetOf<Integer> V, int size){
        Domains D = Domains.create();
        for(int i = 0; i < size; i++) {
            D.add(i);
            D.get(i).add(V);
        }
        ConstraintBuilder.alldiff(mdd, D, V, null, size);
        Memory.free(D);
        return mdd;
        //return MDDAllDifferent.generate(mdd, V, size);
    }
    public static MDD alldiff(MDD mdd, Domains D, SetOf<Integer> V, int size){
        return ConstraintBuilder.alldiff(mdd, D, V, null, size);
        //return MDDAllDifferent.generate(mdd, V, C, size);
    }
    public static MDD alldiff(MDD mdd, SetOf<Integer> D, SetOf<Integer> V, int size){
        Domains domains = Domains.create();
        for(int i = 0; i < size; i++){
            domains.add(i);
            for(int v : D) domains.put(i, v);
        }
        MDD result = ConstraintBuilder.alldiff(mdd, domains, V, null, size);
        Memory.free(domains);
        return result;
        //return MDDAllDifferent.generate(mdd, V, C, size);
    }
    public static MDD alldiff(MDD mdd, Domains D, SetOf<Integer> V, SetOf<Integer> variables, int size){
        return ConstraintBuilder.alldiff(mdd, D, V, variables, size);
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

    public static MDD mulRelaxed(MDD mdd, double m_min, double m_max, double maxProbaDomains, double maxProbaEpsilon, int n, Domains D){
        return ConstraintBuilder.mulRelaxed(mdd, D, m_min, m_max, maxProbaDomains, maxProbaEpsilon, n);
    }

    /**
     * <b>This method return a MDD which respect the multiplicative constraint</b><br>
     * The multiplicative constraint ensure that if we multiply all the variables together, the result belong to the interval [m_min, m_max].
     * This method only works for positives numbers.
     * As the product can be very hugh, the type of m_min and m_max is BigInteger
     * @param mdd The MDD that will stock the result of the operation
     * @param m_min The lower bound of the constraint.
     * @param m_max The upper bound of the constraint.
     * @param n The number of variables.
     * @param D The domains of the variables.
     * @return The MDD satisfying the multiplicative constraint.
     */
    public static MDD mul(MDD mdd, BigInteger m_min, BigInteger m_max, int n, Domains D){
        return ConstraintBuilder.mul(mdd, D, m_min, m_max, n);
    }

    public static MDD sumDouble(MDD mdd, double s_min, double s_max, MapOf<Integer, Double> mapDouble, int epsilon, int size, Domains D){
        return ConstraintBuilder.sumDouble(mdd, D, s_min, s_max, mapDouble, epsilon, size);
    }

    public static MDD sumDoubleULP(MDD mdd, double s_min, double s_max, MapOf<Integer, Double> mapDouble, int epsilon, int size, Domains D){
        return ConstraintBuilder.sumDoubleULP(mdd, D, s_min, s_max, mapDouble, epsilon, size);
    }

    public static MDD sumRelaxed(MDD mdd, long s_min, long s_max, MapOf<Integer, Long> map, int epsilon, int precision, int size, Domains D){
        return ConstraintBuilder.sumRelaxed(mdd, D, s_min, s_max, map, epsilon, precision, size);
    }

    public static  MDD confidenceMulRelaxed(MDD mdd, int gamma, int precision, int epsilon, int n, Domains D){
        return ConstraintOperation.confidenceMulRelaxed(mdd, null, gamma, precision, epsilon, n, D);
    }

    public static MDD confidence(MDD mdd, int gamma, int precision, int n, Domains D){
        int step = (int) Math.pow(10, precision);
        BigInteger bigIntStep = BigInteger.valueOf(step);

        BigInteger bigIntGamma = BigInteger.valueOf(gamma);
        BigInteger m_max = bigIntStep;

        for (int i = 0; i < n-1; i++){
            bigIntGamma = bigIntGamma.multiply(bigIntStep);
            m_max = m_max.multiply(bigIntStep);
        }

        return mul(mdd, bigIntGamma, m_max, n, D);
    }

    public static strictfp MDD confidence(MDD mdd, double gamma, int precision, int epsilon, int n, Domains D){
        return ConstraintOperation.confidence(mdd, null, gamma, precision, epsilon, n, D);
    }

    public static strictfp MDD confidenceULP(MDD mdd, int gamma, int precision, int epsilon, int n, Domains D){
        return ConstraintOperation.confidenceULP(mdd, null, gamma, precision, epsilon, n, D);
    }

    public static MDD confidence(MDD mdd, int gamma, int precision, int epsilon, int n, int logPrecision, Domains D){
        return ConstraintOperation.confidence(mdd, null, gamma, precision, epsilon, n, logPrecision, D);
    }

}
