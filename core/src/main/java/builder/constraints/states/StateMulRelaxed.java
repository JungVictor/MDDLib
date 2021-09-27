package builder.constraints.states;

import builder.constraints.parameters.ParametersMulRelaxed;
import confidence.utils.SpecialOperations;
import memory.AllocatorOf;
import utils.SmallMath;

public strictfp class StateMulRelaxed extends NodeState {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private double mul;
    private ParametersMulRelaxed constraint;

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

    private StateMulRelaxed(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersMulRelaxed constraint){
        this.constraint = constraint;
        this.mul = constraint.maxProbaEpsilon();
    }

    public static StateMulRelaxed create(ParametersMulRelaxed constraint){
        StateMulRelaxed object = allocator().allocate();
        object.init(constraint);
        return object;
    }

    public String toString(){
        return Double.toString(mul);
    }

    //**************************************//


    @Override
    public NodeState createState(int label, int layer, int size) {
        StateMulRelaxed state = StateMulRelaxed.create(constraint);
        state.mul = SmallMath.multiplyCeil(mul, label, constraint.maxProbaDomains());
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){

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

    @Override
    public String hash(int label, int layer, int size){
        double newMul = SmallMath.multiplyCeil(mul, label, constraint.maxProbaDomains());

        double minPotential = SmallMath.multiplyCeil(newMul, constraint.vMin(layer-1), constraint.maxProbaEpsilon());
        double maxPotential = SmallMath.multiplyCeil(newMul, constraint.vMax(layer-1), constraint.maxProbaEpsilon());

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Double.toString(newMul);
    }

    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateMulRelaxed stateMulrelaxed = (StateMulRelaxed) state;
        double m = SmallMath.multiplyCeil(stateMulrelaxed.mul, label, constraint.maxProbaDomains());
        if(m < mul) mul = m;
        return null;
    }

    @Override
    public void free(){
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateMulRelaxed type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateMulRelaxed> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateMulRelaxed[] arrayCreation(int capacity) {
            return new StateMulRelaxed[capacity];
        }

        @Override
        protected StateMulRelaxed createObject(int index) {
            return new StateMulRelaxed(index);
        }
    }
}
