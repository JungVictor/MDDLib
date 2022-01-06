package confidence.states;

import builder.constraints.states.NodeState;
import confidence.parameters.ParametersMulPF;
import confidence.structures.PrimeFactorization;
import memory.AllocatorOf;

public class StateMulPF extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private PrimeFactorization mul;
    private ParametersMulPF constraint;

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

    private StateMulPF(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersMulPF constraint){
        this.constraint = constraint;
        this.mul = PrimeFactorization.create(1);
    }

    public static StateMulPF create(ParametersMulPF constraint){
        StateMulPF object = allocator().allocate();
        object.init(constraint);
        return object;
    }

    public String toString(){
        return mul.toString();
    }

    //**************************************//


    @Override
    public NodeState createState(int label, int layer, int size) {
        StateMulPF state = StateMulPF.create(constraint);
        state.mul = mul.multiply(constraint.mapPrimeFact(label));
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        PrimeFactorization newMul = mul.multiply(constraint.mapPrimeFact(label));

        //Lignes à revoir pour l'ordre dans lequel les multiplications sont faites
        PrimeFactorization minPotential = newMul.multiply(constraint.vMin(layer-1));
        PrimeFactorization maxPotential = newMul.multiply(constraint.vMax(layer-1));

        //Si l'intervalle [minPotential, maxPotential] intersection [constraint.min(), constraint.max] est vide
        if(maxPotential.toLog10() < constraint.min() || constraint.max() < minPotential.toLog10()) return false;
        //Si l'intervalle [minPotential, maxPotential] est inclus dans l'intervalle [constraint.min(), constraint.max]
        if(constraint.min() <= minPotential.toLog10() && maxPotential.toLog10() <= constraint.max()) return true;

        boolean result = newMul.toLog10() <= constraint.max();

        newMul.free();
        minPotential.free();
        maxPotential.free();

        return result;
    }

    @Override
    public String signature(int label, int layer, int size){
        PrimeFactorization newMul = mul.multiply(constraint.mapPrimeFact(label));

        PrimeFactorization minPotential = newMul.multiply(constraint.vMin(layer-1));
        PrimeFactorization maxPotential = newMul.multiply(constraint.vMax(layer-1));

        if(constraint.min() <= minPotential.toLog10() && maxPotential.toLog10() <= constraint.max()) return "";
        String result = newMul.toString();

        newMul.free();
        minPotential.free();
        maxPotential.free();

        return result;
    }

    @Override
    public void free(){
        this.constraint = null;
        this.mul.free();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateMul type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateMulPF> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateMulPF[] arrayCreation(int capacity) {
            return new StateMulPF[capacity];
        }

        @Override
        protected StateMulPF createObject(int index) {
            return new StateMulPF(index);
        }
    }
}
