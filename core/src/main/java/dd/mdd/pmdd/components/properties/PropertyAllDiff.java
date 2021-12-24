package dd.mdd.pmdd.components.properties;

import memory.AllocatorOf;
import memory.Memory;
import structures.generics.MapOf;
import structures.generics.SetOf;

/**
 * <b>All Different constraint</b><br>
 * We simply store into a set the already taken values.
 * If we try to add an already existing value, the constraint is violated.
 * We only take into account some values if specified, otherwise we take everything into account.
 * Result is an int : 0 = violated  -  1 = satisfied
 */
public class PropertyAllDiff extends NodeProperty {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private SetOf<Integer> values;
    private SetOf<Integer> alldiff;


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
    public PropertyAllDiff(int allocatedIndex){
        super(allocatedIndex);
        super.setName(ALLDIFF);
    }

    /**
     * Create a new property corresponding to given parameters.
     * @param values The set of constrained values
     * @return a new property corresponding to the parameters
     */
    public static PropertyAllDiff create(SetOf<Integer> values){
        PropertyAllDiff property = allocator().allocate();
        property.prepare();
        property.addValues(values);
        return property;
    }

    /**
     * Add all values contained in the given set to the property
     * @param values Set of values
     */
    public void addValues(SetOf<Integer> values){
        this.values.add(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return alldiff.toString();
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getSingle

    /**
     * {@inheritDoc}
     */
    @Override
    public MapOf getData(){
        return null;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeProperty createProperty(int value) {
        PropertyAllDiff allDiff = PropertyAllDiff.create(values);
        for(int v : this.alldiff) allDiff.alldiff.add(v);

        if(values.contains(value)) allDiff.alldiff.add(value);
        return allDiff;
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int v, int layer, int size) {
        return !alldiff.contains(v);
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
        for(int v : values) {
            if(alldiff.contains(v)) hash += 1;
            hash += hash;
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hash(int value){
        if(alldiff.contains(value)) return hash();
        int hash = 0;
        for(int v : values) {
            if(alldiff.contains(v)) hash += 1;
            else if(v == value) hash += 1;
            hash += hash;
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hashstr(int value){
        StringBuilder builder = new StringBuilder();
        for(int v : values) {
            if(v == value || alldiff.contains(v)) builder.append("1");
            else builder.append("0");
        }
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
        alldiff = Memory.SetOfInteger();
        values = Memory.SetOfInteger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        values.clear();
        alldiff.clear();
        Memory.free(values);
        Memory.free(alldiff);
        super.free();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the PropertyAllDiff type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PropertyAllDiff> {

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
        protected PropertyAllDiff[] arrayCreation(int capacity) {
            return new PropertyAllDiff[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertyAllDiff createObject(int index) {
            return new PropertyAllDiff(index);
        }
    }
}
