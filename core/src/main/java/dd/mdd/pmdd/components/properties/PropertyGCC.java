package dd.mdd.pmdd.components.properties;

import memory.AllocatorOf;
import memory.Memory;
import structures.generics.MapOf;
import structures.tuples.TupleOfInt;
import structures.lists.ListOfInt;

/**
 * <b>Global Cardinality Constraint (GCC)</b><br>
 * We use an interval to represent the number of time a value is taken : [min, max].
 * By doing this for each value, we have a matrix.
 */
public class PropertyGCC extends NodeProperty {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    private MapOf<Integer, TupleOfInt> bounds;
    private MapOf<Integer, TupleOfInt> currentValues;
    private int BASE = 2;


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
    public PropertyGCC(int allocatedIndex){
        super(allocatedIndex);
        super.setName(GCC);
    }

    /**
     * Create a new property corresponding to given parameters.
     * @param max The values of the GCC
     * @return a new property corresponding to given parameters.
     */
    public static PropertyGCC create(MapOf<Integer, TupleOfInt> max){
        PropertyGCC property = allocator().allocate();
        property.prepare();
        property.setMaxValues(max);
        return property;
    }

    /**
     * Initialise the minimum and maximum values possible for the GCC.
     * @param bounds the mapping of value → (min, max)
     */
    public void setMaxValues(MapOf<Integer, TupleOfInt> bounds){
        this.bounds.clear();
        int val;
        for(int value : bounds) {
            val = bounds.get(value).getSecond();
            this.bounds.put(value, bounds.get(value));
            this.currentValues.put(value, TupleOfInt.create());
            if(val > BASE) BASE = val;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        ListOfInt integers = ListOfInt.create();
        integers.add(currentValues.keySet());
        integers.sort();
        StringBuilder builder = new StringBuilder();
        for (int v : integers) {
            builder.append(v);
            builder.append(" -> ");
            builder.append(currentValues.get(v));
            builder.append("; ");
        }
        return builder.toString();
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
        return currentValues;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeProperty createProperty(int value) {
        PropertyGCC next = PropertyGCC.create(bounds);
        for(int v : currentValues) next.currentValues.put(v, TupleOfInt.create(currentValues.get(v)));
        if(bounds.contains(value)) next.currentValues.get(value).incr(1,1);
        return next;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeWithProperty(int value, NodeProperty nodeProperty){
        PropertyGCC property = (PropertyGCC) nodeProperty;
        int add, min, max;
        for(int v : bounds){
            add = v == value ? 1 : 0;
            TupleOfInt tuple = currentValues.get(v);
            TupleOfInt pTuple = property.currentValues.get(v);
            min = tuple.getFirst() + add;
            max = tuple.getSecond() + add;
            if(min < pTuple.getFirst()) pTuple.setFirst(min);
            if(max > pTuple.getSecond()) pTuple.setSecond(max);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertyGCC.class) return;
        PropertyGCC gcc = (PropertyGCC) property;

        for(int v : bounds){
            TupleOfInt tuple = currentValues.get(v), pTuple = gcc.currentValues.get(v);
            if(pTuple.getFirst() < tuple.getFirst()) tuple.setFirst(pTuple.getFirst());
            if(tuple.getSecond() < pTuple.getSecond()) tuple.setSecond(pTuple.getSecond());
        }
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    // TODO : Change implementation
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int v) {
        if(bounds.contains(v)) return true;
        return currentValues.get(v).getSecond()+1 <= bounds.get(v).getSecond();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int v, int layer, int size){
        if(!bounds.contains(v)) return false;
        TupleOfInt t1 = currentValues.get(v);
        TupleOfInt t2 = bounds.get(v);
        if(size == layer) return t1.getSecond() + 1 <= t2.getSecond() && t1.getFirst() + 1 >= t2.getFirst();
        return t1.getSecond() + 1 <= t2.getSecond();
    }


    //**************************************//
    //           HASH FUNCTIONS             //
    //**************************************//
    // hash

    /**
     * {@inheritDoc}
     */
    @Override
    public int hash(){
        int hash = 0;
        for(int value : bounds){
            hash += currentValues.get(value).getSecond();
            hash *= BASE;
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hash(int v){
        int hash = 0;
        for(int value : bounds){
            hash += currentValues.get(value).getSecond();
            if(value == v) hash++;
            hash *= BASE;
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hashstr(int value){
        ListOfInt integers = ListOfInt.create();
        integers.add(currentValues.keySet());
        integers.sort();
        StringBuilder builder = new StringBuilder();
        for (int v : integers) {
            builder.append(v);
            builder.append(" -> ");
            if(v != value) builder.append(currentValues.get(v));
            else {
                TupleOfInt tmp = TupleOfInt.create(currentValues.get(v));
                tmp.incr(1,1);
                builder.append(tmp);
                Memory.free(tmp);
            }
            builder.append("; ");
        }
        Memory.free(integers);
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        bounds = Memory.MapOfIntegerTupleOfInt();
        currentValues = Memory.MapOfIntegerTupleOfInt();
        setName(GCC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        for(int v : currentValues) Memory.free(currentValues.get(v));
        Memory.free(bounds);
        Memory.free(currentValues);
        BASE = 2;
        super.free();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the PropertyGCC type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PropertyGCC> {

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
        protected PropertyGCC[] arrayCreation(int capacity) {
            return new PropertyGCC[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertyGCC createObject(int index) {
            return new PropertyGCC(index);
        }
    }
}
