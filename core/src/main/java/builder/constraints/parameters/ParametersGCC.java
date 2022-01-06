package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.tuples.TupleOfInt;

import java.util.Set;

/**
 * <b>ParametersGCC</b><br>
 * Parameters of the GCC constraint. <br>
 * Contains the min. and max. number of occurrences for each constrained value.
 */
public class ParametersGCC extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // Not to free
    private MapOf<Integer, TupleOfInt> gcc;
    private int minimum, violations;


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
    protected ParametersGCC(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters.
     * @param gcc The values of the GCC
     * @param variables The set of constrained variables
     */
    protected void init(MapOf<Integer, TupleOfInt> gcc, SetOf<Integer> variables, int violations){
        this.gcc = gcc;
        this.minimum = 0;
        this.violations = violations;
        for(TupleOfInt tuple : gcc.values()) minimum += tuple.getFirst();
        super.setVariables(variables);
    }

    /**
     * Get a ParametersGCC object from the allocator.
     * @param gcc The values of the GCC
     * @param variables The set of constrained variables
     * @return a fresh ParametersGCC object
     */
    public static ParametersGCC create(MapOf<Integer, TupleOfInt> gcc, SetOf<Integer> variables){
        ParametersGCC object = allocator().allocate();
        object.init(gcc, variables, 0);
        return object;
    }

    /**
     * Get a ParametersGCC object from the allocator.
     * @param gcc The values of the GCC
     * @param violations The maximum number of violations allowed
     * @param variables The set of constrained variables
     * @return a fresh ParametersGCC object
     */
    public static ParametersGCC create(MapOf<Integer, TupleOfInt> gcc, int violations, SetOf<Integer> variables){
        ParametersGCC object = allocator().allocate();
        object.init(gcc, variables, violations);
        return object;
    }

    //**************************************//

    /**
     * Check if the label is a constrained value
     * @param label Value of the label
     * @return True if the label is constrained, false otherwise
     */
    public boolean contains(int label){
        return gcc.contains(label);
    }

    /**
     * Get the minimum occurrences for the given label
     * @param label Value of the label
     * @return The minimum occurrences for the given label
     */
    public int min(int label){
        return gcc.get(label).getFirst();
    }

    /**
     * Get the maximum occurrences for the given label
     * @param label Value of the label
     * @return The maximum occurrences for the given label
     */
    public int max(int label){
        return gcc.get(label).getSecond();
    }

    /**
     * Get the minimum number of variables necessary to satisfy the constraint. <br>
     * That is, the sum of all min() of each variable.
     * @return The minimum number of variables necessary to satisfy the constraint.
     */
    public int minimum(){return minimum;}

    /**
     * Get the maximum number of violations allowed
     * @return The maximum number of violations allowed
     */
    public int violations(){
        return violations;
    }

    /**
     * Get the set of constrained values
     * @return The set of constrained values
     */
    public Set<Integer> V(){return gcc.keySet();}


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
     * <b>The allocator that is in charge of the ParametersGCC type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersGCC> {

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
        protected ParametersGCC[] arrayCreation(int capacity) {
            return new ParametersGCC[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersGCC createObject(int index) {
            return new ParametersGCC(index);
        }
    }

}
