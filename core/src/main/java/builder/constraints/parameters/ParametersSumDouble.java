package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.generics.MapOf;
import structures.arrays.ArrayOfDouble;
import structures.generics.SetOf;

/**
 * <b>ParametersSumDouble</b><br>
 * Parameters of the Sum constraint, represented by double. <br>
 * Here, the value of the label is not the label itself : we get the value of the label from a map.<br>
 * Contains the set of constrained variables, the bounds of the sum and the map associating labels with values.
 */
public class ParametersSumDouble extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // References, must not be free or cleaned by the object
    private double min, max;
    private ArrayOfDouble vMin, vMax;
    private MapOf<Integer, Double> mapDouble;
    private int epsilon;


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
    protected ParametersSumDouble(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param mapDouble The map associating labels with values
     * @param epsilon The precision of the sum
     * @param scope The set of constrained variables
     */
    protected void init(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, MapOf<Integer, Double> mapDouble, int epsilon, SetOf<Integer> scope){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.mapDouble = mapDouble;
        this.epsilon = epsilon;
        super.setScope(scope);
    }

    /**
     * Get a ParametersSumDouble object from the allocator.
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param vMin The minimum sum achievable from a certain layer
     * @param vMax The maximum sum achievable from a certain layer
     * @param mapDouble The map associating labels with values
     * @param precision The precision of the sum
     * @param scope The set of constrained variables
     * @return a fresh ParametersSumDouble object
     */
    public static ParametersSumDouble create(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, MapOf<Integer, Double> mapDouble, int precision, SetOf<Integer> scope){
        ParametersSumDouble object = allocator().allocate();
        object.init(min, max, vMin, vMax, mapDouble, precision, scope);
        return object;
    }

    //**************************************//


    /**
     * Get the minimum value of the sum
     * @return The minimum value of the sum
     */
    public double min(){return min;}

    /**
     * Get the maximum value of the sum
     * @return The maximum value of the sum
     */
    public double max(){return max;}

    /**
     * Get the minimum sum achievable from the layer i
     * @param i The index of the layer
     * @return The minimum sum achievable from the layer i
     */
    public double vMin(int i){return vMin.get(i);}

    /**
     * Get the maximum sum achievable from the layer i
     * @param i The index of the layer
     * @return The maximum sum achievable from the layer i
     */
    public double vMax(int i){return vMax.get(i);}

    /**
     * Get the value associated with the label
     * @param label The label
     * @return The value associated with the label
     */
    public double mapDouble(int label){return mapDouble.get(label);}

    /**
     * Get the precision of the sum
     * @return The precision of the sum
     */
    public int epsilon(){return epsilon;}

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
     * <b>The allocator that is in charge of the ParametersSumDouble type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersSumDouble> {

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
        protected ParametersSumDouble[] arrayCreation(int capacity) {
            return new ParametersSumDouble[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersSumDouble createObject(int index) {
            return new ParametersSumDouble(index);
        }
    }
}
