package pmdd.components.properties;

import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.generics.ListOf;
import structures.generics.MapOf;
import structures.integers.TupleOfInt;

import java.util.Collections;

/**
 * GLOBAL CARDINALITY CONSTRAINT
 * We use an interval to represent the number of time a value is taken : [min, max].
 * By doing this for each value, we have a matrix.
 */
public class PropertyGCC extends NodeProperty {

    private MapOf<Integer, TupleOfInt> bounds;
    private MapOf<Integer, TupleOfInt> currentValues;
    private int BASE = 2;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public PropertyGCC(MemoryPool<NodeProperty> pool){
        super(pool);
        super.setName(GCC);
    }

    /**
     * Initialise the minimum and maximum values possible for the GCC.
     * @param bounds the mapping of value -> (min, max)
     */
    public void setMaxValues(MapOf<Integer, TupleOfInt> bounds){
        this.bounds.clear();
        int val;
        for(int value : bounds) {
            val = bounds.get(value).getSecond();
            this.bounds.put(value, bounds.get(value));
            this.currentValues.put(value, Memory.TupleOfInt());
            if(val > BASE) BASE = val;
        }
    }

    @Override
    public String toString(){
        ListOf<Integer> integers = Memory.ListOfInteger();
        integers.add(currentValues.keySet());
        Collections.sort(integers.getList());
        StringBuilder builder = new StringBuilder();
        for (int v : integers) {
            builder.append(v);
            builder.append(" -> ");
            builder.append(currentValues.get(v));
            builder.append("; ");
        }
        return builder.toString();
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
        PropertyGCC next = PMemory.PropertyGCC(bounds);
        TupleOfInt tuple;
        for(int v : currentValues){
            tuple = currentValues.get(v);
            next.currentValues.put(v, Memory.TupleOfInt(tuple.getFirst(), tuple.getSecond()));
        }

        if(bounds.contains(value)) next.currentValues.get(value).incr(1,1);
        return next;
    }

    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertyGCC property = (PropertyGCC) nodeProperty;
        int add, min, max;
        for(int v : bounds){
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

        for(int v : bounds){
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
        if(!bounds.contains(v)) return false;
        return currentValues.get(v).getSecond()+1 > bounds.get(v).getSecond();
    }

    @Override
    public boolean isDegenerate(int v, boolean finalLayer){
        if(!bounds.contains(v)) return false;
        TupleOfInt t1 = currentValues.get(v);
        TupleOfInt t2 = bounds.get(v);
        if(finalLayer) return t1.getSecond() + 1 > t2.getSecond() || t1.getFirst() + 1 < t2.getFirst();
        return t1.getSecond() + 1 > t2.getSecond();
    }


    //**************************************//
    //           HASH FUNCTIONS             //
    //**************************************//
    // hash

    @Override
    public int hash(){
        int hash = 0;
        for(int value : bounds){
            hash += currentValues.get(value).getSecond();
            hash *= BASE;
        }
        return hash;
    }

    @Override
    public int hash(int v){
        int hash = 0;
        for(int value : bounds){
            hash += currentValues.get(value).getSecond();
            if(value == v) hash++;
            hash *= BASE;
        }
        return hash;
    }

    @Override
    public String hashstr(int value){
        ListOf<Integer> integers = Memory.ListOfInteger();
        integers.add(currentValues.keySet());
        Collections.sort(integers.getList());
        StringBuilder builder = new StringBuilder();
        for (int v : integers) {
            builder.append(v);
            builder.append(" -> ");
            if(v != value) builder.append(currentValues.get(v));
            else {
                TupleOfInt tmp = Memory.TupleOfInt(currentValues.get(v));
                tmp.incr(1,1);
                builder.append(tmp);
                Memory.free(tmp);
            }
            builder.append("; ");
        }
        Memory.free(integers);
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        bounds = Memory.MapOfIntegerTupleOfInt();
        currentValues = Memory.MapOfIntegerTupleOfInt();
        setName(GCC);
    }

    @Override
    public void free(){
        for(int v : currentValues) Memory.free(currentValues.get(v));
        Memory.free(bounds);
        Memory.free(currentValues);
        BASE = 2;
        super.free();
    }
}
