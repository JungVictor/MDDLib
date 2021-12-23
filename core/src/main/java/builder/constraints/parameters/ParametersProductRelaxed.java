package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.arrays.ArrayOfDouble;
import structures.generics.SetOf;

/**
 * <b>ParametersProductRelaxed (double)</b><br>
 * Parameters of the relaxed Product constraint. <br>
 * Contains the minimum and maximum value of the product.
 */
public class ParametersProductRelaxed extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    // References, must not be free or cleaned by the object
    private double min, max;
    private ArrayOfDouble vMin, vMax;
    private double maxProbaDomains;
    private double maxProbaEpsilon;

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
    protected ParametersProductRelaxed(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters
     * @param min The minimum value of the product
     * @param max The maximum value of the product
     * @param vMin The minimum product achievable from a certain layer
     * @param vMax The maximum product achievable from a certain layer
     * @param maxProbaDomains The precision of the value of the domain
     * @param maxProbaEpsilon The precision of the value of the product
     * @param variables The set of constrained variables
     */
    protected void init(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, double maxProbaDomains, double maxProbaEpsilon, SetOf<Integer> variables){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.maxProbaDomains = maxProbaDomains;
        this.maxProbaEpsilon = maxProbaEpsilon;
        super.setVariables(variables);
    }

    /**
     * Get a ParametersProductRelaxed object from the allocator.
     * @param min The minimum value of the product
     * @param max The maximum value of the product
     * @param vMin The minimum product achievable from a certain layer
     * @param vMax The maximum product achievable from a certain layer
     * @param maxProbaDomains The precision of the value of the domain
     * @param maxProbaEpsilon The precision of the value of the product
     * @param variables The set of constrained variables
     * @return a fresh ParametersProductRelaxed object
     */
    public static ParametersProductRelaxed create(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, double maxProbaDomains, double maxProbaEpsilon, SetOf<Integer> variables){
        ParametersProductRelaxed object = allocator().allocate();
        object.init(min, max, vMin, vMax, maxProbaDomains, maxProbaEpsilon, variables);
        return object;
    }

    //**************************************//

    /**
     * Get the minimum value of the product
     * @return The minimum value of the product
     */
    public double min(){return min;}

    /**
     * Get the maximum value of the product
     * @return The maximum value of the product
     */
    public double max(){return max;}

    /**
     * Get the minimum product achievable from the layer i
     * @param i The index of the layer
     * @return The minimum product achievable from the layer i
     */
    public double vMin(int i){return vMin.get(i);}

    /**
     * Get the maximum product achievable from the layer i
     * @param i The index of the layer
     * @return The maximum product achievable from the layer i
     */
    public double vMax(int i){return vMax.get(i);}

    /**
     * Get the precision of the value of the domain
     * @return The precision of the value of the domain
     */
    public double maxProbaDomains(){return maxProbaDomains;}

    /**
     * Get the precision of the value of the product
     * @return The precision of the value of the product
     */
    public double maxProbaEpsilon(){return maxProbaEpsilon;}

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
     * <b>The allocator that is in charge of the ParametersProductRelaxed type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersProductRelaxed> {

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
        protected ParametersProductRelaxed[] arrayCreation(int capacity) {
            return new ParametersProductRelaxed[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersProductRelaxed createObject(int index) {
            return new ParametersProductRelaxed(index);
        }
    }
}
