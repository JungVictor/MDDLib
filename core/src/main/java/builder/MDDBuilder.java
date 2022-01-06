package builder;

import builder.constraints.*;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import dd.operations.ConstraintOperation;
import dd.operations.Operation;
import memory.Binary;
import memory.Memory;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import structures.tuples.TupleOfInt;

import java.math.BigInteger;

/**
 * <b>MDDBuilder</b><br>
 * Factory class that generate MDDs.
 */
public class MDDBuilder {

    /* UNIVERSAL */

    /**
     * Generate a universal MDD of given size with V as the domain of all variables.
     * @param mdd The MDD result
     * @param V The domain of all variables
     * @param size The size of the MDD
     * @return The universal MDD
     */
    public static MDD universal(MDD mdd, ArrayOfInt V, int size){
        return MDDUniversal.generate(mdd, V, size);
    }

    /**
     * Generate a universal MDD of given size with D = {0...V} as the domain of all variables.
     * @param mdd The MDD result
     * @param V The number of variables in each domain
     * @param size The size of the MDD
     * @return The universal MDD
     */
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

    /**
     * Generate a universal MDD with given domains.
     * @param mdd The MDD result
     * @param domains The domains of the variables
     * @return The universal MDD
     */
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

    /**
     * Generate an MDD satisfying an among constraint with given parameters.
     * The domain is binary (D[i] = {0,1} for each variable).
     * All variables are constrained.
     * @param mdd The MDD stocking the result
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @return an MDD satisfying an among constraint
     */
    public static MDD among(MDD mdd, int q, int min, int max){
        return sum(mdd, min, max, q, Binary.Set());
        //return MDDAmong.generate(dd.mdd, q, min, max);
    }

    /**
     * Generate an MDD satisfying an among constraint with given parameters.
     * All variables are constrained.
     * @param mdd The MDD stocking the result
     * @param D The domain of the variables
     * @param V The set of constrained values
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @return an MDD satisfying an among constraint
     */
    public static MDD among(MDD mdd, Domains D, SetOf<Integer> V, int q, int min, int max){
        ConstraintBuilder.sequence(mdd, D, V, q, min, max, q, null);
        return mdd;
    }

    /**
     * Generate an MDD satisfying an among constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param D The domain of the variables
     * @param V The set of constrained values
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @param variables The set of constrained variables
     * @return an MDD satisfying an among constraint
     */
    public static MDD among(MDD mdd, Domains D, SetOf<Integer> V, int q, int min, int max, SetOf<Integer> variables){
        ConstraintBuilder.sequence(mdd, D, V, q, min, max, q, variables);
        return mdd;
    }


    /**
     * Generate an MDD satisfying a sequence constraint with given parameters.
     * The domain is binary (D[i] = {0,1} for each variable).
     * @param mdd The MDD stocking the result
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @param size Size of the MDD
     * @return an MDD satisfying a sequence constraint
     */
    public static MDD sequence(MDD mdd, int q, int min, int max, int size){
        // Special case in binary, it's faster to do it this way ! (because reduction...)
        size++;
        MDD among = MDDBuilder.sum(mdd.DD(), min, max, q, Binary.Set());
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

    /**
     * Generate an MDD satisfying a sequence constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param D The domain of the variables
     * @param V The set of constrained values
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @param size Size of the MDD
     * @return an MDD satisfying a sequence constraint
     */
    public static MDD sequence(MDD mdd, Domains D, SetOf<Integer> V, int q, int min, int max, int size){
        return sequence(mdd, D, V, q, min, max, size, null);
    }

    /**
     * Generate an MDD satisfying a sequence constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param D The domain of the variables
     * @param V The set of constrained values
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @param size Size of the MDD
     * @param variables The set of constrained variables
     * @return an MDD satisfying a sequence constraint
     */
    public static MDD sequence(MDD mdd, Domains D, SetOf<Integer> V, int q, int min, int max, int size, SetOf<Integer> variables){
        ConstraintBuilder.sequence(mdd, D, V, q, min, max, size, variables);
        return mdd;
    }

    /* SUM */

    /**
     * Generate an MDD satisfying a sum constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param s_min The minimum value of the sum
     * @param s_max The maximum value of the sum
     * @param n The size of the MDD
     * @param D The domains of the variables
     * @param variables The set of constrained variables
     * @return an MDD satisfying a sum constraint.
     */
    public static MDD sum(MDD mdd, int s_min, int s_max, int n, Domains D, SetOf<Integer> variables){
        ConstraintBuilder.sum(mdd, D, s_min, s_max, n, variables);
        return mdd;
    }

    /**
     * Generate an MDD satisfying a sum constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param s_min The minimum value of the sum
     * @param s_max The maximum value of the sum
     * @param n The size of the MDD
     * @param D The domains of the variables
     * @return an MDD satisfying a sum constraint.
     */
    public static MDD sum(MDD mdd, int s_min, int s_max, int n, Domains D){
        return sum(mdd, s_min, s_max, n, D, null);
        //return MDDSum.generate(dd.mdd, s_min, s_max, n, V);
    }

    /**
     * Generate an MDD satisfying a sum constraint with given parameters.
     * All variables have the same domain V.
     * @param mdd The MDD stocking the result
     * @param s_min The minimum value of the sum
     * @param s_max The maximum value of the sum
     * @param n The size of the MDD
     * @param V The domains of the variables
     * @return an MDD satisfying a sum constraint.
     */
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

    /**
     * Generate an MDD satisfying a sum constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param s The exact value of the sum
     * @param n The size of the MDD
     * @param D The domains of the variables
     * @return an MDD satisfying a sum constraint.
     */
    public static MDD sum(MDD mdd, int s, int n, Domains D){
        return sum(mdd, s, s, n, D);
    }


    /**
     * Generate an MDD satisfying a sum constraint with given parameters.
     * All variables have the same domain V.
     * @param mdd The MDD stocking the result
     * @param s The exact value of the sum
     * @param n The size of the MDD
     * @param V The domains of the variables
     * @return an MDD satisfying a sum constraint.
     */
    public static MDD sum(MDD mdd, int s, int n, SetOf<Integer> V){
        return sum(mdd, s, s, n, V);
    }

    /**
     * Generate an MDD satisfying a sum constraint with given parameters.
     * All variables have the same domain V = {0,1}
     * @param mdd The MDD stocking the result
     * @param s The exact value of the sum
     * @param n The size of the MDD
     * @return an MDD satisfying a sum constraint.
     */
    public static MDD sum(MDD mdd, int s, int n){
        return sum(mdd, s, s, n, Binary.Set());
    }

    /* GCC */

    /**
     * Generate an MDD satisfying a GCC constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param n The size of the MDD
     * @param couples The values of the GCC
     * @param D The domains of the variables
     * @return an MDD satisfying a GCC constraint
     */
    public static MDD gcc(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, Domains D){
        ConstraintBuilder.gcc(mdd, D, couples, 0, n, null);
        return mdd;
    }

    /**
     * Generate an MDD satisfying a GCC constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param n The size of the MDD
     * @param couples The values of the GCC
     * @param D The domains of the variables
     * @param variables The set of constrained variables
     * @return an MDD satisfying a GCC constraint
     */
    public static MDD gcc(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, Domains D, SetOf<Integer> variables){
        ConstraintBuilder.gcc(mdd, D, couples, 0, n, variables);
        return mdd;
    }

    /**
     * Generate an MDD satisfying a GCC constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param n The size of the MDD
     * @param couples The values of the GCC
     * @param violations The maximum number of violations in the GCC
     * @param D The domains of the variables
     * @param variables The set of constrained variables
     * @return an MDD satisfying a GCC constraint
     */
    public static MDD gcc(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, int violations, Domains D, SetOf<Integer> variables){
        ConstraintBuilder.gcc(mdd, D, couples, violations, n, variables);
        return mdd;
    }

    /* ALL DIFF */

    /**
     * Generate an MDD satisfying an AllDifferent constraint with given parameters.
     * All values and variables are constrained.
     * @param mdd The MDD stocking the result
     * @param V The domains of each variable
     * @param size The size of the MDD
     * @return an MDD satisfying an AllDifferent constraint
     */
    public static MDD allDifferent(MDD mdd, SetOf<Integer> V, int size){
        Domains D = Domains.create();
        for(int i = 0; i < size; i++) {
            D.add(i);
            D.get(i).add(V);
        }
        ConstraintBuilder.allDifferent(mdd, D, V, size, null);
        Memory.free(D);
        return mdd;
        //return MDDAllDifferent.generate(dd.mdd, V, size);
    }

    /**
     * Generate an MDD satisfying an AllDifferent constraint with given parameters.
     * All variables are constrained.
     * @param mdd The MDD stocking the result
     * @param D The domains of the variables
     * @param V The set of constrained values
     * @param size The size of the MDD
     * @return an MDD satisfying an AllDifferent constraint
     */
    public static MDD allDifferent(MDD mdd, Domains D, SetOf<Integer> V, int size){
        ConstraintBuilder.allDifferent(mdd, D, V, size, null);
        return mdd;
        //return MDDAllDifferent.generate(dd.mdd, V, C, size);
    }


    /**
     * Generate an MDD satisfying an AllDifferent constraint with given parameters.
     * All variables are constrained.
     * All variables have the same domain D.
     * @param mdd The MDD stocking the result
     * @param D The domain of each variable
     * @param V The set of constrained values
     * @param size The size of the MDD
     * @return an MDD satisfying an AllDifferent constraint
     */
    public static MDD allDifferent(MDD mdd, SetOf<Integer> D, SetOf<Integer> V, int size){
        Domains domains = Domains.create();
        for(int i = 0; i < size; i++){
            domains.add(i);
            for(int v : D) domains.put(i, v);
        }
        ConstraintBuilder.allDifferent(mdd, domains, V,  size, null);
        Memory.free(domains);
        return mdd;
        //return MDDAllDifferent.generate(dd.mdd, V, C, size);
    }


    /**
     * Generate an MDD satisfying an AllDifferent constraint with given parameters.
     * @param mdd The MDD stocking the result
     * @param D The domains of the variables
     * @param V The set of constrained values
     * @param size The size of the MDD
     * @param variables The set of constrained variables
     * @return an MDD satisfying an AllDifferent constraint
     */
    public static MDD allDifferent(MDD mdd, Domains D, SetOf<Integer> V, int size, SetOf<Integer> variables){
        ConstraintBuilder.allDifferent(mdd, D, V, size, variables);
        return mdd;
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
        return ConstraintBuilder.mulRelaxed(mdd, D, m_min, m_max, maxProbaDomains, maxProbaEpsilon, n, null);
    }
    public static MDD mulRelaxed(MDD mdd, double m_min, double m_max, double maxProbaDomains, double maxProbaEpsilon, int n, Domains D, SetOf<Integer> variables){
        return ConstraintBuilder.mulRelaxed(mdd, D, m_min, m_max, maxProbaDomains, maxProbaEpsilon, n, variables);
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
        return ConstraintBuilder.mul(mdd, D, m_min, m_max, n, null);
    }
    public static MDD mul(MDD mdd, BigInteger m_min, BigInteger m_max, int n, Domains D, SetOf<Integer> variables){
        return ConstraintBuilder.mul(mdd, D, m_min, m_max, n, variables);
    }

    public static MDD sumDouble(MDD mdd, double s_min, double s_max, MapOf<Integer, Double> mapDouble, int epsilon, int size, Domains D){
        return ConstraintBuilder.sumDouble(mdd, D, s_min, s_max, mapDouble, epsilon, size, null);
    }
    public static MDD sumDouble(MDD mdd, double s_min, double s_max, MapOf<Integer, Double> mapDouble, int epsilon, int size, Domains D, SetOf<Integer> variables){
        return ConstraintBuilder.sumDouble(mdd, D, s_min, s_max, mapDouble, epsilon, size, variables);
    }

    public static MDD sumDoubleULP(MDD mdd, double s_min, double s_max, MapOf<Integer, Double> mapDouble, int epsilon, int size, Domains D){
        return ConstraintBuilder.sumDoubleULP(mdd, D, s_min, s_max, mapDouble, epsilon, size, null);
    }
    public static MDD sumDoubleULP(MDD mdd, double s_min, double s_max, MapOf<Integer, Double> mapDouble, int epsilon, int size, Domains D, SetOf<Integer> variables){
        return ConstraintBuilder.sumDoubleULP(mdd, D, s_min, s_max, mapDouble, epsilon, size, variables);
    }

    public static MDD sumRelaxed(MDD mdd, long s_min, long s_max, MapOf<Integer, Long> map, int epsilon, int precision, int size, Domains D){
        return ConstraintBuilder.sumRelaxed(mdd, D, s_min, s_max, map, epsilon, precision, size, null);
    }
    public static MDD sumRelaxed(MDD mdd, long s_min, long s_max, MapOf<Integer, Long> map, int epsilon, int precision, int size, Domains D, SetOf<Integer> variables){
        return ConstraintBuilder.sumRelaxed(mdd, D, s_min, s_max, map, epsilon, precision, size, variables);
    }


    // OPERATION


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
