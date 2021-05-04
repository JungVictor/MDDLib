package pmdd.components.properties;


import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.integers.ArrayOfInt;

/**
 * SUM CONSTRAINT
 * We use an interval to represents the [min, max] value of the sum.
 */
public class PropertySum extends NodeProperty {

    private final ArrayOfInt value = Memory.ArrayOfInt(2);
    private int min, max;

    public PropertySum(MemoryPool<NodeProperty> pool, int value){
        this(pool, value, value);
    }

    public PropertySum(MemoryPool<NodeProperty> pool, int v1, int v2){
        this(pool, v1, v2, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    public PropertySum(MemoryPool<NodeProperty> pool, int v1, int v2, int min, int max){
        super(pool);
        this.value.set(0, v1); this.value.set(1, v2);
        this.max = max;
        this.min = min;
        super.setType(DataType.ARRAY);
        super.setName(SUM);
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
    public ArrayOfInt getArray() {
        return value;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int val) {
        return PMemory.PropertySum(value.get(0)+val, value.get(1)+val, min, max);
    }

    @Override
    public void mergeWithProperty(int val, NodeProperty nodeProperty){
        PropertySum property = (PropertySum) nodeProperty;
        property.value.set(0, Math.min(value.get(0)+val, property.value.get(0)));
        property.value.set(1, Math.max(value.get(1)+val, property.value.get(1)));
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertySum.class) return;
        PropertySum sum = (PropertySum) property;

        value.set(0, Math.min(value.get(0), sum.value.get(0)));
        value.set(1, Math.max(value.get(1), sum.value.get(1)));
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isDegenerate() {
        return value.get(1) > max || value.get(0) > max;
    }

    @Override
    public boolean isDegenerate(int v) {
        return value.get(1)+v > max || value.get(0)+v > max;
    }

    @Override
    public boolean isDegenerate(int v, boolean finalLayer) {
        if(finalLayer) return value.get(1)+v > max || value.get(0)+v < min;
        return isDegenerate(v);
    }
}
