package confidence;

import builder.MDDBuilder;
import confidence.structures.PrimeFactorization;
import mdd.MDD;
import structures.Domains;
import structures.generics.MapOf;

import java.math.BigInteger;

public class MyMDDBuilder extends MDDBuilder {

    /**
     * <b>This method return a MDD which respect the multiplicative constraint</b><br>
     * The multiplicative constraint ensure that if we multiply all the variables together, the result belong to the interval [m_min, m_max].
     * This method only works for positives numbers.
     * As the product can be very hugh, the type of m_min and m_max is BigInteger
     * @param mdd
     * @param m_min The lower bound of the constraint.
     * @param m_max The upper bound of the constraint.
     * @param n The number of variables.
     * @param D The domains of the variables.
     * @return The MDD satisfying the multiplicative constraint.
     */
    public static MDD mul(MDD mdd, BigInteger m_min, BigInteger m_max, int n, Domains D){
        return MyConstraintBuilder.mul(mdd, D, m_min, m_max, n);
    }

    public static MDD mulPF(MDD mdd, PrimeFactorization m_min, PrimeFactorization m_max, MapOf<Integer, PrimeFactorization> mapPrimeFact, int n, Domains D){
        return MyConstraintBuilder.mulPF(mdd, D, m_min.toLog10(), m_max.toLog10(), mapPrimeFact, n);
    }

    public static MDD sumDouble(MDD mdd, double s_min, double s_max, MapOf<Integer, Double> mapDouble, int epsilon, int size, Domains D){
        return MyConstraintBuilder.sumDouble(mdd, D, s_min, s_max, mapDouble, epsilon, size);
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

    public static MDD confidencePF(MDD mdd, int gamma, int precision, int n, Domains D){
        MapOf<Integer, PrimeFactorization> mapPrimeFact = MyMemory.MapOfIntegerPrimefactorization();
        for(int i = 0; i < n; i++){
            for(int v : D.get(i)){
                if(!mapPrimeFact.contains(v)){ mapPrimeFact.put(v, PrimeFactorization.create(v)); };
            }
        }
        int step = (int) Math.pow(10, precision);
        PrimeFactorization primeFactStep = PrimeFactorization.create(step);

        PrimeFactorization primeFactGamma = PrimeFactorization.create(gamma);
        PrimeFactorization m_max = primeFactStep;

        for (int i = 0; i < n-1; i++){
            primeFactGamma = primeFactGamma.multiply(primeFactStep);
            m_max = m_max.multiply(primeFactStep);
        }

        return mulPF(mdd, primeFactGamma, m_max, mapPrimeFact, n, D);
    }

    public static strictfp MDD confidence(MDD mdd, double gamma, int precision, int epsilon, int n, Domains D){
        MapOf<Integer, Double> mapLog = MyMemory.MapOfIntegerDouble();
        for(int i = 0; i < n; i++){
            for(int v : D.get(i)){
                mapLog.put(v, -1 * Math.log(v * Math.pow(10, -precision)));
            }
        }
        double s_max = -1 * Math.log(gamma);

        return sumDouble(mdd, 0, s_max, mapLog, epsilon, n, D);
    }
}
