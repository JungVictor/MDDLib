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
    private int min, max;

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
     * @param min absolute min value
     * @param max absolute max value
     */
    public void setValue(int v1, int v2, int min, int max){
        this.value = Memory.TupleOfInt();
        this.value.set(v1, v2);
        this.min = min;
        this.max = max;
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
        return null;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int val) {
        NodeProperty next = PMemory.PropertySum(value.getFirst()+val, value.getSecond()+val, min, max);
        return next;
    }

    @Override
    public void mergeWithProperty(int val, NodeProperty nodeProperty){
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
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isValid() {
        return value.getSecond() <= max;
    }

    @Override
    public boolean isValid(int v) {
        return value.getSecond()+v <= max;
    }

    @Override
    public boolean isValid(int v, int layer, int size) {
        if(layer == size) return value.getFirst()+v <= min && value.getSecond()+v <= max;
        return isValid(v);
    }


    //**************************************//
    //           HASH FUNCTIONS             //
    //**************************************//
    // hash

    @Override
    public int hash(){
        return value.getFirst();
    }

    @Override
    public int hash(int value){
        return this.value.getFirst()+value;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(value);
        super.free();
    }

    @Override
    public void prepare(){
        super.prepare();
        this.value = Memory.TupleOfInt();
    }
}