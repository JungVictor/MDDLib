package pmdd.components.properties;

import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.generics.MapOf;
import structures.integers.TupleOfInt;

/**
 * SUM CONSTRAINT
 * We use an interval to represents the [min, max] value of the sum.
 */
public class PropertySum extends NodeProperty {

    private TupleOfInt value;
    private MapOf<Integer, Integer> bindings;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public PropertySum(MemoryPool<NodeProperty> pool){
        super(pool);
        super.setName(SUM);
    }

    /**
     * Set the current values of the property and the minimum and maximum values
     * @param v1 current min value
     * @param v2 current max value
     */
    public void setValue(int v1, int v2){
        this.value = TupleOfInt.create();
        this.value.set(v1, v2);
    }

    public void setBindings(MapOf<Integer, Integer> bindings){
        this.bindings = bindings;
    }

    @Override
    public String toString(){
        return value.toString();
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray

    @Override
    public MapOf getData(){
        MapOf<Integer, Integer> data = Memory.MapOfIntegerInteger();
        data.put(0, value.getFirst());
        data.put(1, value.getSecond());
        return data;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int val) {
        if(bindings != null) val = bindings.get(val);
        return PMemory.PropertySum(value.getFirst()+val, value.getSecond()+val, bindings);
    }

    @Override
    public void mergeWithProperty(int val, NodeProperty nodeProperty){
        if(bindings != null) val = bindings.get(val);
        PropertySum property = (PropertySum) nodeProperty;
        property.value.setFirst(Math.min(value.getFirst()+val, property.value.getFirst()));
        property.value.setSecond(Math.max(value.getSecond()+val, property.value.getSecond()));
        System.out.print("");
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertySum.class) return;
        PropertySum sum = (PropertySum) property;

        value.setFirst(Math.min(value.getFirst(), sum.value.getFirst()));
        value.setSecond(Math.max(value.getSecond(), sum.value.getSecond()));
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(value);
        this.bindings = null;
        super.free();
    }

    @Override
    public void prepare(){
        super.prepare();
        this.value = TupleOfInt.create();
    }
}
