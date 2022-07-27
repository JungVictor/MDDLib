package dd.mdd.pmdd.components.properties;

import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfBoolean;
import structures.arrays.ArrayOfInt;
import structures.generics.MapOf;
import structures.generics.SetOf;

import java.lang.management.MemoryManagerMXBean;
import java.util.HashSet;

public class PropertyTSP extends NodeProperty {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // One node can have multiple paths with different origins
    private final HashSet<ArrayOfBoolean> values = new HashSet<>();
    private int size;


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
    public PropertyTSP(int allocatedIndex){
        super(allocatedIndex);
        super.setName("TSP");
    }

    /**
     * Create a new property corresponding to given parameters.
     * @param size The size of the path
     * @return a new property corresponding to the parameters
     */
    public static PropertyTSP create(int size){
        PropertyTSP property = allocator().allocate();
        property.prepare();
        return property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return "";
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
        PropertyTSP tsp = PropertyTSP.create(size);
        for(ArrayOfBoolean v : this.values) {
            if(v.get(value)) continue;
            ArrayOfBoolean vcopy = ArrayOfBoolean.create(size);
            vcopy.copy(v);
            vcopy.set(value, true);
            tsp.values.add(vcopy);
        }
        return tsp;
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
        for(ArrayOfBoolean value : this.values) {
            if (!value.get(v)) return true;
        }
        return false;
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
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hash(int value){
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hashstr(int value){
        return "";
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        for(ArrayOfBoolean b : values) Memory.free(b);
        values.clear();
        size = 0;
    }


    /**
     * <b>The allocator that is in charge of the PropertyAllDiff type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PropertyTSP> {

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
        protected PropertyTSP[] arrayCreation(int capacity) {
            return new PropertyTSP[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertyTSP createObject(int index) {
            return new PropertyTSP(index);
        }
    }
}
