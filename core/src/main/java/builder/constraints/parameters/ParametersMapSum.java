package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.arrays.ArrayOfInt;
import structures.generics.MapOf;
import structures.generics.SetOf;

/**
 * <b>ParametersMapSum</b><br>
 * Parameters of the Sum constraint. <br>
 * Here, the value of the label is not the label itself : we get the value of the label from a map.<br>
 * Contains the set of constrained variables, the bounds of the sum and the map associating labels with values.
 */
public class ParametersMapSum extends ParametersSum {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    // References, must not be free or cleaned by the object
    private MapOf<Integer, Integer> map;

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
    protected ParametersMapSum(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param map The map associating labels with values
     * @param scope The set of constrained variables
     */
    protected void init(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax, MapOf<Integer, Integer> map, SetOf<Integer> scope){
        this.map = map;
        super.init(min, max, vMin, vMax, scope);
    }

    /**
     * Get a ParametersMapSum object from the allocator.
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param map The map associating labels with values
     * @param scope The set of constrained variables
     * @return a fresh ParametersMapSum object
     */
    public static ParametersMapSum create(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax, MapOf<Integer, Integer> map, SetOf<Integer> scope){
        ParametersMapSum object = allocator().allocate();
        object.init(min, max, vMin, vMax, map, scope);
        return object;
    }

    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public int value(int label){return map.get(label);}


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
    static final class Allocator extends AllocatorOf<ParametersMapSum> {

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
        protected ParametersMapSum[] arrayCreation(int capacity) {
            return new ParametersMapSum[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersMapSum createObject(int index) {
            return new ParametersMapSum(index);
        }
    }
}
