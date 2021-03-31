package pmdd.components.properties;


import java.util.Arrays;

/**
 * SUM CONSTRAINT
 * We use an interval to represents the [min, max] value of the sum.
 */
public class PropertySum extends NodeProperty {

    private final int[] value;
    private final int max;

    public PropertySum(int value){
        this(new int[]{value, value}, Integer.MAX_VALUE);
    }

    public PropertySum(int[] value, int max){
        this.value = value;
        this.max = max;
        super.setType(DataType.ARRAY);
        super.setName(SUM);
    }

    @Override
    public String toString(){
        return Arrays.toString(value);
    }

    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray

    @Override
    public int[] getArray() {
        return value;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int val) {
        return new PropertySum(new int[]{value[0]+val, value[1]+val}, max);
    }

    @Override
    public void mergeWithProperty(int val, NodeProperty nodeProperty){
        PropertySum property = (PropertySum) nodeProperty;
        property.value[0] = Math.min(value[0]+val, property.value[0]);
        property.value[1] = Math.max(value[1]+val, property.value[1]);
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertySum.class) return;
        PropertySum sum = (PropertySum) property;

        value[0] = Math.min(value[0], sum.value[0]);
        value[1] = Math.max(value[1], sum.value[1]);
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isDegenerate() {
        return value[1] > max || value[0] > max;
    }

    @Override
    public boolean isDegenerate(int v) {
        return value[1]+v > max || value[0]+v > max;
    }
}
