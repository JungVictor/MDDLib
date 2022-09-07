package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.generics.SetOf;

/**
 * <b>ParametersAllDiffMem</b><br>
 * Parameters of the All Different constraint. <br>
 * Contains the set of values that are constrained.
 */
public class ParametersAllDiffMem extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    // References, must not be free or cleaned by the object
    private SetOf<Integer> V;
    private int capacity;

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
     * Private constructor of the parameters.
     * Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator
     */
    protected ParametersAllDiffMem(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters.
     * @param V The set of constrained values
     * @param scope The set of constrained variables
     */
    protected void init(int capacity, SetOf<Integer> V, SetOf<Integer> scope){
        this.V = V;
        this.capacity = capacity;
        super.setScope(scope);
    }

    /**
     * Get a ParametersAllDiffMem object from the allocator.
     * @param V The set of constrained values
     * @param scope The set of constrained variables
     * @return a fresh ParametersAllDiffMem object
     */
    public static ParametersAllDiffMem create(int capacity, SetOf<Integer> V, SetOf<Integer> scope){
        ParametersAllDiffMem object = allocator().allocate();
        object.init(capacity, V, scope);
        return object;
    }

    /**
     * Get a ParametersAllDiffMem object from the allocator.
     * All variables are considered as constrained.
     * @param V The set of constrained values
     * @return a fresh ParametersAllDiffMem object
     */
    public static ParametersAllDiffMem create(int capacity, SetOf<Integer> V){
        return create(capacity, V, null);
    }

    //**************************************//

    /**
     * Check if the value is constrained.
     * @param label Value of the label
     * @return True if the value is constrained, false otherwise
     */
    public boolean contains(int label){
        return V == null || V.contains(label);
    }

    /**
     * Get the set of all constrained values
     * @return The set of all constrained values
     */
    public SetOf<Integer> set(){
        return V;
    }

    public int getCapacity(){
        return capacity;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the ParametersAllDiffMem type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersAllDiffMem> {

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
        protected ParametersAllDiffMem[] arrayCreation(int capacity) {
            return new ParametersAllDiffMem[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersAllDiffMem createObject(int index) {
            return new ParametersAllDiffMem(index);
        }
    }

}
