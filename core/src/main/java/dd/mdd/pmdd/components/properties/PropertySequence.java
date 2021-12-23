package dd.mdd.pmdd.components.properties;

import memory.AllocatorOf;
import memory.Memory;
import structures.tuples.TupleOfInt;
import structures.arrays.ArrayOfTupleOfInt;
import structures.generics.MapOf;
import structures.generics.SetOf;

/**
 * <b>SEQUENCE CONSTRAINT</b><br>
 * We use a matrix to represent the couples (q, l, u) with q = the size of the sequence, l = lowerbound and u = upperbound.
 * values[q][0] = min, values[q][1] = max.
 * We take into account values passed as parameters.
 */
public class PropertySequence extends NodeProperty {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    private PropertySequence accumulator;
    private ArrayOfTupleOfInt values;
    private SetOf<Integer> label;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    public PropertySequence(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Create a new property corresponding to given parameters.
     * @param label The set of constrained values
     * @param size The size of the window
     * @return a new property corresponding to given parameters.
     */
    public static PropertySequence create(SetOf<Integer> label, int size){
        PropertySequence property = allocator().allocate();
        property.prepare();
        property.init(label, size);
        return property;
    }

    /**
     * Create the first property corresponding to the given parameters
     * @param label The set of constrained values
     * @param size The size of the window
     * @return the first property corresponding to given parameters.
     */
    public static PropertySequence createFirst(SetOf<Integer> label, int size){
        PropertySequence property = create(label, 1);
        property.accumulator = create(label, size);
        return property;
    }

    /**
     * Initialise the variables of the property
     * @param label The set of constrained values
     * @param size The size of the window
     */
    public void init(SetOf<Integer> label, int size){
        this.label = Memory.SetOfInteger();
        for(int j : label) this.label.add(j);

        this.values = ArrayOfTupleOfInt.create(size);
        for(int i = 0; i < size; i++) values.set(i, TupleOfInt.create(i, 0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return values.toString();
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray2

    /**
     * {@inheritDoc}
     */
    @Override
    public MapOf getData(){
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeProperty getResult(){
        return accumulator;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty
    // -labelToValue

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeProperty createProperty(int value) {
        PropertySequence next = PropertySequence.create(label, values.length()+1);
        for(int i = 1; i < values.length()+1; i++){
            next.values.get(i).setFirst(values.get(i-1).getFirst() + labelToValue(value));
            next.values.get(i).setSecond(values.get(i-1).getSecond() + labelToValue(value));
        }
        accumulator.mergeWithProperty(next);
        next.accumulator = accumulator;
        return next;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    // NOTHING, TODO ?


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        Memory.free(values);
        Memory.free(label);
        accumulator = null;
        super.free();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the PropertySequence type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PropertySequence> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertySequence[] arrayCreation(int capacity) {
            return new PropertySequence[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertySequence createObject(int index) {
            return new PropertySequence(index);
        }
    }
}
