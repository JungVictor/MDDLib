package pmdd.components.properties;


import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;

import java.util.Arrays;

/**
 * GLOBAL CARDINALITY CONSTRAINT
 * We use an interval to represent the number of time a value is taken : [min, max].
 * By doing this for each value, we have a matrix.
 */
public class PropertyGCC extends NodeProperty {

    private final ArrayOfInt max = memory.Memory.ArrayOfInt(2);
    private final MatrixOfInt values = memory.Memory.MatrixOfInt(2, 2);

    public PropertyGCC(MemoryPool<NodeProperty> pool, ArrayOfInt max){
        super(pool);
        this.max.copy(max);
        this.values.setSize(max.length, 2);
        super.setType(DataType.ARRAY2);
        super.setName(GCC);
    }

    @Override
    public String toString(){
        return values.toString();
    }

    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray2

    @Override
    public MatrixOfInt getArray2(){
        return values;
    }

    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int value) {
        value--;
        PropertyGCC next = PMemory.PropertyGCC(max);
        for(int i = 0; i < next.values.getHeight(); i++){
            next.values.set(i, 0, values.get(i, 0));
            next.values.set(i, 1, values.get(i, 1));
        }
        if(max.get(value) >= 0) {
            next.values.incr(value, 0, 1);
            next.values.incr(value, 1, 1);
        }
        return next;
    }

    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertyGCC property = (PropertyGCC) nodeProperty;
        int add;
        value--;
        for(int i = 0; i < max.length; i++) {
            add = i == value ? 1 : 0;
            int min = values.get(i, 0) + add, max = values.get(i, 1) + add;
            if(min < property.values.get(i, 0)) property.values.set(i, 0, min);
            if(property.values.get(i, 1) < max) property.values.set(i, 1, max);
        }
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertyGCC.class) return;
        PropertyGCC gcc = (PropertyGCC) property;

        for(int i = 0; i < max.length; i++) {
            if(gcc.values.get(i,0) < values.get(i,0)) values.set(i,0, gcc.values.get(i, 0));
            if(values.get(i,1) < gcc.values.get(i,1)) values.set(i,1, gcc.values.get(i, 1));
        }
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isDegenerate(int v) {
        v--;
        return values.get(v,1)+1 > max.get(v) && max.get(v) > 0;
    }



    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface
    @Override
    public void prepare() {

    }
}
