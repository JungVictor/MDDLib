package pmdd.components.properties;

import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.MatrixOfInt;
import structures.integers.TupleOfInt;

/**
 * SEQUENCE CONSTRAINT
 * We use a matrix to represent the couples (q, l, u) with q = the size of the sequence, l = lowerbound and u = upperbound.
 * values[q][0] = min, values[q][1] = max.
 * We take into account values passed as parameters.
 */
public class PropertySequence extends NodeProperty {

    // TODO : hash

    private PropertySequence accumulator;
    private final ArrayOf<TupleOfInt> values;
    private final SetOf<Integer> label = Memory.SetOfInteger();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public PropertySequence(MemoryPool<NodeProperty> pool, SetOf<Integer> label, int size, boolean base) {
        this(pool, label, 1);
        accumulator = PMemory.PropertySequence(label, size, false);
    }

    public PropertySequence(MemoryPool<NodeProperty> pool, SetOf<Integer> label, int size){
        super(pool);
        for(int j : label) this.label.add(j);
        super.setName(SEQ);

        this.values = Memory.ArrayOfTupleOfInt(size);
        for(int i = 0; i < size; i++) values.get(i).set(0, i);
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
    public MapOf getData(){
        return null;
    }

    @Override
    public NodeProperty getResult(){
        return accumulator;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty
    // -labelToValue

    @Override
    public NodeProperty createProperty(int value) {
        PropertySequence next = PMemory.PropertySequence(label, values.length()+1, false);
        for(int i = 1; i < values.length()+1; i++){
            next.values.get(i).setFirst(values.get(i-1).getFirst() + labelToValue(value));
            next.values.get(i).setSecond(values.get(i-1).getSecond() + labelToValue(value));
        }
        accumulator.mergeWithProperty(next);
        next.accumulator = accumulator;
        return next;
    }

    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertySequence property = (PropertySequence) nodeProperty;
        for(int i = 1; i < property.values.length(); i++){
            int min = Math.min(values.get(i-1).getFirst() + labelToValue(value), property.values.get(i).getFirst());
            int max = Math.max(values.get(i-1).getSecond() + labelToValue(value), property.values.get(i).getSecond());
            property.values.get(i).set(min, max);
        }
        accumulator.mergeWithProperty(property);
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertySequence.class) return;
        PropertySequence seq = (PropertySequence) property;
        for(int i = 1; i < seq.values.length(); i++){
            values.get(i).setFirst(Math.min(values.get(i).getFirst(), seq.values.get(i).getFirst()));
            values.get(i).setSecond(Math.max(values.get(i).getSecond(), seq.values.get(i).getSecond()));
        }
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


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        accumulator = null;
        label.clear();
        super.free();
    }
}
