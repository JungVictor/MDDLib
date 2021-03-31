package pmdd.components.properties;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * SEQUENCE CONSTRAINT
 * We use a matrix to represent the couples (q, l, u) with q = the size of the sequence, l = lowerbound and u = upperbound.
 * values[q][0] = min, values[q][1] = max.
 * We take into account values passed as parameters.
 */
public class PropertySequence extends NodeProperty {

    private PropertySequence accumulator;
    private int[][] values;
    private Set<Integer> label;

    private PropertySequence(){
        super.setType(DataType.ARRAY2);
        super.setName(SEQ);
    }

    public PropertySequence(int label, int size){
        this(new int[]{label}, size);
    }

    public PropertySequence(int[][] values, int[] label){
        this.values = values;
        this.label = new HashSet<>();
        for (int j : label) this.label.add(j);
        super.setType(DataType.ARRAY2);
        super.setName(SEQ);
    }

    public PropertySequence(int[] label, int size){
        this(new int[1][2], label);
        accumulator = new PropertySequence();
        accumulator.values = new int[size][2];
        for(int i = 0; i < size; i++) accumulator.values[i][0] = i;
    }

    private PropertySequence(Set<Integer> label, int depth){
        this.values = new int[depth+1][2];
        this.label = label;
        super.setType(DataType.ARRAY2);
        super.setName(SEQ);
    }

    private PropertySequence(int[][] values){
        this.values = values;
        super.setType(DataType.ARRAY2);
        super.setName(SEQ);
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
    public int[][] getArray2() {
        return values;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty
    // getResult        || -labelToValue
    @Override
    public NodeProperty createProperty(int value) {
        PropertySequence next = new PropertySequence(label, values.length);
        for(int i = 1; i < values.length+1; i++){
            next.values[i][0] = values[i-1][0]+ labelToValue(value);
            next.values[i][1] = values[i-1][1]+ labelToValue(value);
        }
        accumulator.mergeWithProperty(next);
        next.accumulator = accumulator;
        return next;
    }

    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertySequence property = (PropertySequence) nodeProperty;
        for(int i = 1; i < property.values.length; i++){
            property.values[i][0] = Math.min(values[i-1][0]+ labelToValue(value), property.values[i][0]);
            property.values[i][1] = Math.max(values[i-1][1]+ labelToValue(value), property.values[i][1]);
        }
        accumulator.mergeWithProperty(property);
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertySequence.class) return;
        PropertySequence seq = (PropertySequence) property;
        for(int i = 1; i < seq.values.length; i++){
            values[i][0] = Math.min(values[i][0], seq.values[i][0]);
            values[i][1] = Math.max(values[i][1], seq.values[i][1]);
        }
    }

    @Override
    public NodeProperty getResult(){
        return accumulator;
    }

    /**
     * Check if the label is contained in the set of values in the sequence
     * @param label Label
     * @return 1 if the label is contained, 0 otherwise.
     */
    private int labelToValue(int label){
        if(this.label.contains(label)) return 1;
        return 0;
    }

    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate
    @Override
    public boolean isDegenerate(int v) {
        return false;
    }
}
