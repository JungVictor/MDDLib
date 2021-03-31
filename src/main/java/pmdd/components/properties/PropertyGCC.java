package pmdd.components.properties;


import memory.MemoryPool;
import pmdd.memory.Memory;

import java.util.Arrays;

/**
 * GLOBAL CARDINALITY CONSTRAINT
 * We use an interval to represent the number of time a value is taken : [min, max].
 * By doing this for each value, we have a matrix.
 */
public class PropertyGCC extends NodeProperty {

    private final int[] max;
    private int[][] values;

    public PropertyGCC(MemoryPool<NodeProperty> pool, int size){
        this(pool, new int[size]);
    }

    public PropertyGCC(MemoryPool<NodeProperty> pool, int[] max){
        super(pool);
        this.max = max;
        this.values = new int[max.length][2];
        super.setType(DataType.ARRAY2);
        super.setName(GCC);
    }

    @Override
    public String toString(){
        return Arrays.deepToString(values);
    }

    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray2

    @Override
    public int[][] getArray2(){
        return values;
    }

    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int value) {
        value--;
        PropertyGCC next = Memory.PropertyGCC(max);
        int[][] nVals = new int[max.length][2];
        for(int i = 0; i < nVals.length; i++){
            nVals[i][0] = values[i][0];
            nVals[i][1] = values[i][1];
        }
        if(max[value] >= 0) {
            nVals[value][0] += 1;
            nVals[value][1] += 1;
        }
        next.values = nVals;
        return next;
    }

    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertyGCC property = (PropertyGCC) nodeProperty;
        int add;
        value--;
        for(int i = 0; i < max.length; i++) {
            add = i == value ? 1 : 0;
            if(values[i][0]+add < property.values[i][0]) property.values[i][0] = values[i][0]+add;
            if(property.values[i][1] < values[i][1]+add) property.values[i][1] = values[i][1]+add;
        }
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertyGCC.class) return;
        PropertyGCC gcc = (PropertyGCC) property;

        for(int i = 0; i < max.length; i++) {
            if(gcc.values[i][0] < values[i][0]) values[i][0] = gcc.values[i][0];
            if(values[i][1] < gcc.values[i][1]) values[i][1] = gcc.values[i][1];
        }
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isDegenerate(int v) {
        v--;
        return values[v][1]+1 > max[v] && max[v] > 0;
    }



    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface
    @Override
    public void prepare() {

    }
}
