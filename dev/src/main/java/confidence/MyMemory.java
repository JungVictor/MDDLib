package confidence;

import confidence.properties.PropertySumDouble;
import confidence.structures.PrimeFactorization;
import memory.Memory;
import memory.MemoryPool;
import pmdd.components.properties.NodeProperty;
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

    private static final MemoryPool<NodeProperty> mapOfPropertySumDouble = new MemoryPool<>();
    public static PropertySumDouble PropertySumDouble(double v1, double v2, MapOf<Integer, Double> bindings){
        PropertySumDouble object = (PropertySumDouble) mapOfPropertySumDouble.get();
        if(object == null){
            object = new PropertySumDouble(mapOfPropertySumDouble);
            mapOfPropertySumDouble.add(object);
        }
        object.prepare();
        object.setValue(v1, v2);
        object.setBindings(bindings);
        return object;
    }
    public static PropertySumDouble PropertySumDouble(double v1, double v2) {
        return PropertySumDouble(v1, v2, null);
    }
    public static PropertySumDouble PropertySumDouble(){
        return PropertySumDouble(0,0,null);
    }

}
