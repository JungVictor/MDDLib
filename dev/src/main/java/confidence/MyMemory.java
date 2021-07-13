package confidence;

import builder.constraints.states.NodeState;
import memory.Memory;
import memory.MemoryPool;
import confidence.parameters.ParametersMul;
import confidence.parameters.ParametersSumDouble;
import confidence.states.StateMul;
import confidence.states.StateSumDouble;
import structures.generics.ArrayOf;
import structures.generics.MapOf;

import java.math.BigInteger;

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

    //**************************************//
    //               ARRAYS                 //
    //**************************************//

    private static final MemoryPool<ArrayOf<BigInteger>> arrayOfBigIntegerPool = new MemoryPool<>();
    public static ArrayOf<BigInteger> ArrayOfBigInteger(int capacity){
        ArrayOf<BigInteger> object = arrayOfBigIntegerPool.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfBigIntegerPool, capacity);
            arrayOfBigIntegerPool.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

    private static final MemoryPool<ArrayOf<Double>> arrayOfDoublePool = new MemoryPool<>();
    public static ArrayOf<Double> ArrayOfDouble(int capacity){
        ArrayOf<Double> object = arrayOfDoublePool.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfDoublePool, capacity);
            arrayOfDoublePool.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

    //**************************************//
    //        STATES AND PARAMETERS         //
    //**************************************//

    private static final MemoryPool<NodeState> stateMulPool = new MemoryPool<>();
    public static StateMul StateMul(ParametersMul constraint){
        StateMul object = (StateMul) stateMulPool.get();
        if(object == null){
            object = new StateMul(stateMulPool);
            stateMulPool.add(object);
        }
        object.prepare();
        object.init(constraint);
        return  object;
    }

    private static final MemoryPool<NodeState> stateSumDoublePool = new MemoryPool<>();
    public static StateSumDouble StateSumDouble(ParametersSumDouble constraint){
        StateSumDouble object = (StateSumDouble) stateSumDoublePool.get();
        if(object == null){
            object = new StateSumDouble(stateSumDoublePool);
            stateSumDoublePool.add(object);
        }
        object.prepare();
        object.init(constraint);
        return object;
    }

    private static final MemoryPool<ParametersMul> parametersMulPool = new MemoryPool<>();
    public static ParametersMul ParametersMul(BigInteger min, BigInteger max, ArrayOf<BigInteger> vMin, ArrayOf<BigInteger> vMax){
        ParametersMul object = parametersMulPool.get();
        if(object == null){
            object = new ParametersMul(parametersMulPool);
            parametersMulPool.add(object);
        }
        object.prepare();
        object.init(min, max, vMin, vMax);
        return object;
    }

    private static final MemoryPool<ParametersSumDouble> parametersSumDoublePool = new MemoryPool<>();
    public static ParametersSumDouble ParametersSumDouble(double min, double max,
                                                          ArrayOf<Double> vMin, ArrayOf<Double> vMax,
                                                          MapOf<Integer, Double> mapIntegerDouble, int precision){
        ParametersSumDouble object = parametersSumDoublePool.get();
        if(object == null){
            object = new ParametersSumDouble(parametersSumDoublePool);
            parametersSumDoublePool.add(object);
        }
        object.prepare();
        object.init(min, max, vMin, vMax, mapIntegerDouble, precision);
        return object;
    }
}
