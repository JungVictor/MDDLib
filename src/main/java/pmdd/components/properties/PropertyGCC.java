package pmdd.components.properties;


import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;
import structures.integers.TupleOfInt;

import java.util.Arrays;

/**
 * GLOBAL CARDINALITY CONSTRAINT
 * We use an interval to represent the number of time a value is taken : [min, max].
 * By doing this for each value, we have a matrix.
 */
public class PropertyGCC extends NodeProperty {

    private final MapOf<Integer, Integer> maxValues = Memory.MapOfIntegerInteger();
    // TODO : memory allocation
    private final MapOf<Integer, TupleOfInt> currentValues = new MapOf<>(null);

    public PropertyGCC(MemoryPool<NodeProperty> pool){
        super(pool);
        super.setType(DataType.ARRAY2);
        super.setName(GCC);
    }

    public void setMaxValues(MapOf<Integer, Integer> maxValues){
        this.maxValues.clear();
        for(int value : maxValues) {
            this.maxValues.put(value, maxValues.get(value));
            this.currentValues.put(value, Memory.TupleOfInt());
        }
    }

    @Override
    public String toString(){
        return currentValues.toString();
    }

    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray2

    @Override
    public MapOf getData(){
        return currentValues;
    }

    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int value) {
        PropertyGCC next = PMemory.PropertyGCC(maxValues);
        TupleOfInt tuple;
        for(int v : currentValues){
            tuple = currentValues.get(v);
            next.currentValues.put(v, Memory.TupleOfInt(tuple.getFirst(), tuple.getSecond()));
        }

        if(maxValues.contains(value)) next.currentValues.get(value).incr(1,1);
        return next;
    }

    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertyGCC property = (PropertyGCC) nodeProperty;
        int add, min, max;
        for(int v : maxValues){
            add = v == value ? 1 : 0;
            TupleOfInt tuple = currentValues.get(v);
            TupleOfInt pTuple = property.currentValues.get(v);
            min = tuple.getFirst() + add;
            max = tuple.getSecond() + add;
            if(min < pTuple.getFirst()) pTuple.setFirst(min);
            if(max > pTuple.getSecond()) pTuple.setSecond(max);
        }
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertyGCC.class) return;
        PropertyGCC gcc = (PropertyGCC) property;

        for(int v : maxValues){
            TupleOfInt tuple = currentValues.get(v), pTuple = gcc.currentValues.get(v);
            if(pTuple.getFirst() < tuple.getFirst()) tuple.setFirst(pTuple.getFirst());
            if(tuple.getSecond() < pTuple.getSecond()) tuple.setSecond(pTuple.getSecond());
        }
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isDegenerate(int v) {
        if(!maxValues.contains(v)) return false;
        return currentValues.get(v).getSecond()+1 > maxValues.get(v);
    }



    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface
    @Override
    public void prepare() {
        setName(GCC);
    }

    @Override
    public void free(){
        for(int v : currentValues) Memory.free(currentValues.get(v));
        maxValues.clear();
        currentValues.clear();
        super.free();
    }
}
