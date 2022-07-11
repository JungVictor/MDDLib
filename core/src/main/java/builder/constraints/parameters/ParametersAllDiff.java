package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.generics.SetOf;

/**
 * <b>ParametersAllDiff</b><br>
 * Parameters of the All Different constraint. <br>
 * Contains the set of values that are constrained.
 */
public class ParametersAllDiff extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    // References, must not be free or cleaned by the object
    private SetOf<Integer> V;


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
    protected ParametersAllDiff(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters.
     * @param V The set of constrained values
     * @param variables The set of constrained variables
     */
    protected void init(SetOf<Integer> V, SetOf<Integer> variables){
        this.V = V;
        super.setVariables(variables);
    }

    /**
     * Get a ParametersAllDiff object from the allocator.
     * @param V The set of constrained values
     * @param variables The set of constrained variables
     * @return a fresh ParametersAllDiff object
     */
    public static ParametersAllDiff create(SetOf<Integer> V, SetOf<Integer> variables){
        ParametersAllDiff object = allocator().allocate();
        object.init(V, variables);
        return object;
    }

    /**
     * Get a ParametersAllDiff object from the allocator.
     * All variables are considered as constrained.
     * @param V The set of constrained values
     * @return a fresh ParametersAllDiff object
     */
    public static ParametersAllDiff create(SetOf<Integer> V){
        return create(V, null);
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
     * <b>The allocator that is in charge of the ParametersAllDiff type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersAllDiff> {

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
        protected ParametersAllDiff[] arrayCreation(int capacity) {
            return new ParametersAllDiff[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersAllDiff createObject(int index) {
            return new ParametersAllDiff(index);
        }
    }

}
