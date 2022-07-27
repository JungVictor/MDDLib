package coins;

import dd.mdd.MDD;
import memory.Memory;
import structures.Domains;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOf;

import java.util.Random;

public class CoinUtil {

    /**
     * Generate a coin system for the given value.<br>
     * All values in a domain are divisors of T.
     * @param T The sum of the coin change problem
     * @return A coin system (domains) for the given value T.
     */
    public static Domains generateSystem(int T){
        Domains domains = Domains.create(T);
        for(int i = 0; i < T+1; i++) domains.fillAll(i, 0, T);
        return domains;
    }

    /**
     * Decompose the value T into k values, such that the sum of the decomposition
     * is equal to T
     * @param T The sum of the coin change problem
     * @param k The number of components
     * @param result The array that contains the result
     * @return The array filled with the decomposition
     */
    public static ArrayOfInt decomposeInto(int T, int k, Random random, ArrayOfInt result){
        for(int i = 0; i < k - 1; i++){
            int v = random.nextInt(T - k + i);
            T -= v;
            result.set(i, v);
        }
        result.set(k-1, T);
        return result;
    }

    public static Domains generateSystemUniversal(MDD mdd){
        return generateSystem(mdd.size());
    }

}
