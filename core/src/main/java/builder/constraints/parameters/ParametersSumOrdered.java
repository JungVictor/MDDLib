package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOf;

/**
 * <b>ParametersSumOrdered</b><br>
 * Parameters of the Sum constraint. <br>
 * Contains the set of constrained variables, the bounds of the sum and the map associating labels with values.
 */
public class ParametersSumOrdered extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    // References, must not be free or cleaned by the object
    private int min, max;
    private ArrayOfInt vMin, vMax;
    private SetOf<Integer> jumps;

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
    protected ParametersSumOrdered(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param variables The set of constrained variables
     */
    protected void init(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax, SetOf<Integer> jumps, SetOf<Integer> variables){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.jumps = jumps;
        super.setVariables(variables);
    }

    /**
     * Get a ParametersSum object from the allocator.
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param variables The set of constrained variables
     * @return a fresh ParametersSum object
     */
    public static ParametersSumOrdered create(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax, SetOf<Integer> jumps, SetOf<Integer> variables){
        ParametersSumOrdered object = allocator().allocate();
        object.init(min, max, vMin, vMax, jumps, variables);
        return object;
    }

    //**************************************//

    /**
     * Get the minimum value of the sum
     * @return The minimum value of the sum
     */
    public int min(){return min;}

    /**
     * Get the maximum value of the sum
     * @return The maximum value of the sum
     */
    public int max(){return max;}

    /**
     * Get the minimum sum achievable from the layer i
     * @param i The index of the layer
     * @return The minimum sum achievable from the layer i
     */
    public int vMin(int i){return vMin.get(i);}

    /**
     * Get the maximum sum achievable from the layer i
     * @param i The index of the layer
     * @return The maximum sum achievable from the layer i
     */
    public int vMax(int i){return vMax.get(i);}

    /**
     * Get the value associated with the given label
     * @param label The label of the arc
     * @return The value associated with the given label
     */
    public int value(int label){
        return label;
    }

    public boolean isJump(int i){return jumps != null && jumps.contains(i);}

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
     * <b>The allocator that is in charge of the ParametersSum type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersSumOrdered> {

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
        protected ParametersSumOrdered[] arrayCreation(int capacity) {
            return new ParametersSumOrdered[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersSumOrdered createObject(int index) {
            return new ParametersSumOrdered(index);
        }
    }
}
