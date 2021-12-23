package builder.constraints.parameters;


import memory.AllocatorOf;
import structures.generics.SetOf;

/**
 * <b>ParametersAmong</b><br>
 * Parameters of the Among constraint. <br>
 * Contains the set of values that are constrained, the size of the window (q),
 * and the min. and max. number of occurrences.
 */
public class ParametersAmong extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // References, must not be free or cleaned by the object
    private int q, min, max;
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
    protected ParametersAmong(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters.
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @param V The set of constrained values
     * @param variables The set of constrained variables
     */
    protected void init(int q, int min, int max, SetOf<Integer> V, SetOf<Integer> variables){
        this.q = q;
        this.min = min;
        this.max = max;
        this.V = V;
        super.setVariables(variables);
    }

    /**
     * Get a ParametersAmong object from the allocator.
     * @param q The size of the window
     * @param min The minimum number of occurrences
     * @param max The maximum number of occurrences
     * @param V The set of constrained values
     * @param variables The set of constrained variables
     * @return a fresh ParametersAmong object.
     */
    public static ParametersAmong create(int q, int min, int max, SetOf<Integer> V, SetOf<Integer> variables){
        ParametersAmong object = allocator().allocate();
        object.init(q, min, max, V, variables);
        return object;
    }

    //**************************************//

    /**
     * Get the size of the window
     * @return The size of the window
     */
    public int q(){return q;}

    /**
     * Get the minimum number of occurrences
     * @return The minimum number of occurrences
     */
    public int min(){return min;}

    /**
     * Get the maximum number of occurrences
     * @return The maximum number of occurrences
     */
    public int max(){return max;}

    /**
     * Get the set of constrained values
     * @return The set of constrained values
     */
    public SetOf<Integer> V(){return V;}

    /**
     * Check if the label is a constrained value
     * @param label The value of the label
     * @return True if the label is a constrained value, false otherwise
     */
    public boolean contains(int label){
        return V.contains(label);
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the ParametersAmong type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersAmong> {

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
        protected ParametersAmong[] arrayCreation(int capacity) {
            return new ParametersAmong[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersAmong createObject(int index) {
            return new ParametersAmong(index);
        }
    }

}
