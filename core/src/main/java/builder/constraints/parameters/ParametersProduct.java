package builder.constraints.parameters;

import structures.arrays.ArrayOfBigInteger;
import memory.AllocatorOf;
import structures.generics.SetOf;

import java.math.BigInteger;

/**
 * <b>ParametersProduct (BigInteger)</b><br>
 * Parameters of the Product constraint. <br>
 * Contains the minimum and maximum value of the product
 */
public class ParametersProduct extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    // References, must not be free or cleaned by the object
    private BigInteger min, max;
    private ArrayOfBigInteger vMin, vMax;

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
    protected ParametersProduct(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters
     * @param min The minimum value of the product
     * @param max The maximum value of the product
     * @param vMin The minimum product achievable from a certain layer
     * @param vMax The maximum product achievable from a certain layer
     * @param scope The set of constrained variables
     */
    protected void init(BigInteger min, BigInteger max, ArrayOfBigInteger vMin, ArrayOfBigInteger vMax, SetOf<Integer> scope){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        super.setScope(scope);
    }

    /**
     * Get a ParametersProduct object from the allocator.
     * @param min The minimum value of the product
     * @param max The maximum value of the product
     * @param vMin The minimum product achievable from a certain layer
     * @param vMax The maximum product achievable from a certain layer
     * @param scope The set of constrained variables
     * @return a fresh ParametersProduct object
     */
    public static ParametersProduct create(BigInteger min, BigInteger max, ArrayOfBigInteger vMin, ArrayOfBigInteger vMax, SetOf<Integer> scope){
        ParametersProduct object = allocator().allocate();
        object.init(min, max, vMin, vMax, scope);
        return object;
    }

    //**************************************//

    /**
     * Get the minimum value of the product
     * @return The minimum value of the product
     */
    public BigInteger min(){return min;}

    /**
     * Get the maximum value of the product
     * @return The maximum value of the product
     */
    public BigInteger max(){return max;}

    /**
     * Get the minimum product achievable from the layer i
     * @param i The index of the layer
     * @return The minimum product achievable from the layer i
     */
    public BigInteger vMin(int i){return vMin.get(i);}

    /**
     * Get the maximum product achievable from the layer i
     * @param i The index of the layer
     * @return The maximum product achievable from the layer i
     */
    public BigInteger vMax(int i){return vMax.get(i);}


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
     * <b>The allocator that is in charge of the ParametersProduct type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersProduct> {

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
        protected ParametersProduct[] arrayCreation(int capacity) {
            return new ParametersProduct[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersProduct createObject(int index) {
            return new ParametersProduct(index);
        }
    }
}
