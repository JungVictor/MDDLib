package pmdd.components.properties;


import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.generics.SetOf;
import structures.integers.MatrixOfInt;

/**
 * SEQUENCE CONSTRAINT
 * We use a matrix to represent the couples (q, l, u) with q = the size of the sequence, l = lowerbound and u = upperbound.
 * values[q][0] = min, values[q][1] = max.
 * We take into account values passed as parameters.
 */
public class PropertySequence extends NodeProperty {

    private PropertySequence accumulator;
    private final MatrixOfInt values;
    private final SetOf<Integer> label = Memory.SetOfInteger();

    public PropertySequence(MemoryPool<NodeProperty> pool, SetOf<Integer> label, int size, boolean base) {
        this(pool, label, 1);
        accumulator = PMemory.PropertySequence(label, size, false);
    }

    public PropertySequence(MemoryPool<NodeProperty> pool, SetOf<Integer> label, int size){
        super(pool);
        for(int j : label) this.label.add(j);
        super.setType(DataType.ARRAY2);
        super.setName(SEQ);

        this.values = Memory.MatrixOfInt(size, 2);
        for(int i = 0; i < size; i++) values.set(i, 0, i);
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
    public MatrixOfInt getArray2() {
        return values;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty
    // getResult        || -labelToValue
    @Override
    public NodeProperty createProperty(int value) {
        PropertySequence next = PMemory.PropertySequence(label, values.getHeight()+1, false);
        for(int i = 1; i < values.getHeight()+1; i++){
            next.values.set(i,0,values.get(i-1,0 )+ labelToValue(value));
            next.values.set(i,1,values.get(i-1,1) + labelToValue(value));
        }
        accumulator.mergeWithProperty(next);
        next.accumulator = accumulator;
        return next;
    }

    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertySequence property = (PropertySequence) nodeProperty;
        for(int i = 1; i < property.values.getHeight(); i++){
            property.values.set(i, 0, Math.min(values.get(i-1, 0)+ labelToValue(value), property.values.get(i,0)));
            property.values.set(i,1, Math.max(values.get(i-1, 1)+ labelToValue(value), property.values.get(i,1)));
        }
        accumulator.mergeWithProperty(property);
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertySequence.class) return;
        PropertySequence seq = (PropertySequence) property;
        for(int i = 1; i < seq.values.getHeight(); i++){
            values.set(i,0, Math.min(values.get(i,0), seq.values.get(i,0)));
            values.set(i,1, Math.max(values.get(i,1), seq.values.get(i,1)));
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

    @Override
    public void free(){
        super.free();
        accumulator = null;
        label.clear();
    }
}
