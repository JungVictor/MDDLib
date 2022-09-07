package builder.constraints.states;

import builder.constraints.parameters.ParametersProductRelaxed;
import memory.AllocatorOf;
import utils.SmallMath;

/**
 * <b>StateProductRelaxed</b><br>
 * Represent the state of a relaxed Product constraint.
 */
public strictfp class StateProductRelaxed extends NodeState {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private double mul;
    private ParametersProductRelaxed constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateProductRelaxed(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersProductRelaxed constraint){
        this.constraint = constraint;
        this.mul = constraint.maxProbaEpsilon();
    }

    /**
     * Create a StateProductRelaxed with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateProductRelaxed with given parameters
     */
    public static StateProductRelaxed create(ParametersProductRelaxed constraint){
        StateProductRelaxed object = allocator().allocate();
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
        return Double.toString(mul);
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
        StateProductRelaxed state = StateProductRelaxed.create(constraint);
        if(constraint.inScope(layer-1)) {
            state.mul = SmallMath.multiplyCeil(mul, label, constraint.maxProbaDomains());
        } else state.mul = mul;
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        if(!constraint.inScope(layer-1)) return true;
        double newMul = SmallMath.multiplyCeil(mul, label, constraint.maxProbaDomains());

        //Lignes à revoir pour l'ordre dans lequel les multiplications sont faites
        double minPotential = SmallMath.multiplyCeil(newMul, constraint.vMin(layer-1), constraint.maxProbaEpsilon());
        double maxPotential = SmallMath.multiplyCeil(newMul, constraint.vMax(layer-1), constraint.maxProbaEpsilon());

        //Si l'intervalle [minPotential, maxPotential] intersection [constraint.min(), constraint.max] est vide
        if(maxPotential < constraint.min() || constraint.max() < minPotential ) return false;
        //Si l'intervalle [minPotential, maxPotential] est inclus dans l'intervalle [constraint.min(), constraint.max]
        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return true;

        return newMul <= constraint.max();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signature(int label, int layer, int size){
        double newMul = mul;
        if(constraint.inScope(layer-1)) newMul = SmallMath.multiplyCeil(mul, label, constraint.maxProbaDomains());

        double minPotential = SmallMath.multiplyCeil(newMul, constraint.vMin(layer-1), constraint.maxProbaEpsilon());
        double maxPotential = SmallMath.multiplyCeil(newMul, constraint.vMax(layer-1), constraint.maxProbaEpsilon());

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Double.toString(newMul);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateProductRelaxed stateMulrelaxed = (StateProductRelaxed) state;
        double m = SmallMath.multiplyCeil(stateMulrelaxed.mul, label, constraint.maxProbaDomains());
        if(m < mul) mul = m;
        return null;
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
     * <b>The allocator that is in charge of the StateProductRelaxed type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateProductRelaxed> {

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
        protected StateProductRelaxed[] arrayCreation(int capacity) {
            return new StateProductRelaxed[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateProductRelaxed createObject(int index) {
            return new StateProductRelaxed(index);
        }
    }
}
