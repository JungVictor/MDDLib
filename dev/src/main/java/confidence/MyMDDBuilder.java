package confidence;

import builder.MDDBuilder;
import confidence.structures.PrimeFactorization;
import dd.mdd.MDD;
import structures.Domains;
import structures.generics.MapOf;

public class MyMDDBuilder extends MDDBuilder {
    public static MDD mulPF(MDD mdd, PrimeFactorization m_min, PrimeFactorization m_max, MapOf<Integer, PrimeFactorization> mapPrimeFact, int n, Domains D){
        return MyConstraintBuilder.mulPF(mdd, D, m_min.toLog10(), m_max.toLog10(), mapPrimeFact, n);
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


}
