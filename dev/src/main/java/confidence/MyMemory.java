package confidence;

import confidence.structures.PrimeFactorization;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.MapOf;

public class MyMemory extends Memory {

    //**************************************//
    //                MAPS                  //
    //**************************************//

    private static final MemoryPool<MapOf<Integer, Double>> mapOfIntegerDoublePool = new MemoryPool<>();
    public static MapOf<Integer, Double> MapOfIntegerDouble(){
        MapOf<Integer, Double> object = mapOfIntegerDoublePool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerDoublePool);
            mapOfIntegerDoublePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, PrimeFactorization>> mapOfIntegerPrimeFactorizationPool = new MemoryPool<>();
    public static MapOf<Integer, PrimeFactorization> MapOfIntegerPrimefactorization(){
        MapOf<Integer, PrimeFactorization> object = mapOfIntegerPrimeFactorizationPool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerPrimeFactorizationPool);
            mapOfIntegerPrimeFactorizationPool.add(object);
        }
        object.prepare();
        return object;
    }

}
