package builder.constraints.states;

import memory.AllocatorOf;
import builder.constraints.parameters.ParametersProduct;

import java.math.BigInteger;

/**
 * <b>StateProduct</b><br>
 * Represent the state of a Product constraint.
 */
public class StateProduct extends NodeState {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private BigInteger mul;
    private ParametersProduct constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateProduct(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersProduct constraint){
        this.constraint = constraint;
        this.mul = BigInteger.ONE;
    }

    /**
     * Create a StateProduct with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateProduct with given parameters
     */
    public static StateProduct create(ParametersProduct constraint){
        StateProduct object = allocator().allocate();
        object.init(constraint);
        return object;
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return mul.toString();
    }


    //**************************************//
    //           STATE FUNCTIONS            //
    //**************************************//
    // Implementation of NodeState functions

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeState createState(int label, int layer, int size) {
        StateProduct state = StateProduct.create(constraint);
        if(constraint.isVariable(layer-1)) {
            BigInteger bigIntLabel = BigInteger.valueOf(label);
            state.mul = mul.multiply(bigIntLabel);
        } else state.mul = mul;
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        if(!constraint.isVariable(layer-1)) return true;
        BigInteger bigIntLabel = BigInteger.valueOf(label);
        BigInteger newMul = mul.multiply(bigIntLabel);

        //Lignes à revoir pour l'ordre dans lequel les multiplications sont faites
        BigInteger minPotential = newMul.multiply(constraint.vMin(layer-1));
        BigInteger maxPotential = newMul.multiply(constraint.vMax(layer-1));

        //Si l'intervalle [minPotential, maxPotential] intersection [constraint.min(), constraint.max] est vide
        if(maxPotential.compareTo(constraint.min()) < 0 || constraint.max().compareTo(minPotential) < 0 ) return false;
        //Si l'intervalle [minPotential, maxPotential] est inclus dans l'intervalle [constraint.min(), constraint.max]
        if(constraint.min().compareTo(minPotential) <= 0 && maxPotential.compareTo(constraint.max()) <= 0) return true;

        return newMul.compareTo(constraint.max()) <= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hash(int label, int layer, int size){
        BigInteger newMul;
        if(constraint.isVariable(layer-1)) {
            BigInteger bigIntLabel = BigInteger.valueOf(label);
            newMul = mul.multiply(bigIntLabel);
        } else newMul = mul;

        BigInteger minPotential = newMul.multiply(constraint.vMin(layer-1));
        BigInteger maxPotential = newMul.multiply(constraint.vMax(layer-1));

        if(constraint.min().compareTo(minPotential) <= 0 && maxPotential.compareTo(constraint.max()) <= 0) return "";
        return newMul.toString();
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateProduct type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateProduct> {

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
        protected StateProduct[] arrayCreation(int capacity) {
            return new StateProduct[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateProduct createObject(int index) {
            return new StateProduct(index);
        }
    }
}
