package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.arrays.ArrayOfLong;
import structures.generics.MapOf;
import structures.generics.SetOf;

/**
 * <b>ParametersSumBinaryRelaxed</b><br>
 * Parameters of the binary relaxed Sum constraint. <br>
 * Here, the value of the label is not the label itself : we get the value of the label from a map.<br>
 * Contains the set of constrained variables, the bounds of the sum and the map associating labels with values.
 */
public class ParametersSumBinaryRelaxed extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // References, must not be free or cleaned by the object
    private long min, max;
    private ArrayOfLong vMin, vMax;
    private MapOf<Integer, Long> map;
    private int epsilon;
    private int precision;
    private int maxBitNumber;


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
    protected ParametersSumBinaryRelaxed(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param map The map associating labels with values
     * @param epsilon The precision of the sum
     * @param precision The precision of the values
     * @param maxBitNumber The maximum number of bit used to represent the values in the map
     * @param scope The set of constrained variables
     */
    protected void init(long min, long max, ArrayOfLong vMin, ArrayOfLong vMax, MapOf<Integer, Long> map, int epsilon, int precision, int maxBitNumber, SetOf<Integer> scope){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.map = map;
        this.epsilon = epsilon;
        this.precision = precision;
        this.maxBitNumber = maxBitNumber;
        super.setScope(scope);
    }

    /**
     * Get a ParametersSumBinaryRelaxed object from the allocator.
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param map The map associating labels with values
     * @param epsilon The precision of the sum
     * @param precision The precision of the values
     * @param maxBitNumber The maximum number of bit used to represent the values in the map
     * @param scope The set of constrained variables
     * @return a fresh ParametersSumBinaryRelaxed object
     */
    public static ParametersSumBinaryRelaxed create(long min, long max, ArrayOfLong vMin, ArrayOfLong vMax, MapOf<Integer, Long> map, int epsilon, int precision, int maxBitNumber, SetOf<Integer> scope){
        ParametersSumBinaryRelaxed object = allocator().allocate();
        object.init(min, max, vMin, vMax, map, epsilon, precision, maxBitNumber, scope);
        return object;
    }

    //**************************************//

    /**
     * Get the minimum value of the sum
     * @return The minimum value of the sum
     */
    public long min(){return min;}

    /**
     * Get the maximum value of the sum
     * @return The maximum value of the sum
     */
    public long max(){return max;}

    /**
     * Get the minimum sum achievable from the layer i
     * @param i The index of the layer
     * @return The minimum sum achievable from the layer i
     */
    public long vMin(int i){return vMin.get(i);}

    /**
     * Get the maximum sum achievable from the layer i
     * @param i The index of the layer
     * @return The maximum sum achievable from the layer i
     */

    public long vMax(int i){return vMax.get(i);}

    /**
     * Get the value associated with the label
     * @param label The label
     * @return The value associated with the label
     */
    public long map(int label){return map.get(label);}

    /**
     * Get the precision of the sum
     * @return The precision of the sum
     */
    public int epsilon(){return epsilon;}

    /**
     * Get the precision of the values
     * @return The precision of the values
     */
    public int precision(){return precision;}

    /**
     * Get the maximum number of bit used to represent the values in the map
     * @return The maximum number of bit used to represent the values in the map
     */
    public int maxBitNumber(){return maxBitNumber;}

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the ParametersSumBinaryRelaxed type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersSumBinaryRelaxed> {

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
        protected ParametersSumBinaryRelaxed[] arrayCreation(int capacity) {
            return new ParametersSumBinaryRelaxed[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersSumBinaryRelaxed createObject(int index) {
            return new ParametersSumBinaryRelaxed(index);
        }
    }
}
