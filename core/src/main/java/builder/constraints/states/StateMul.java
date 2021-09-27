package builder.constraints.states;

import memory.AllocatorOf;
import builder.constraints.parameters.ParametersMul;

import java.math.BigInteger;

public class StateMul extends NodeState {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private BigInteger mul;
    private ParametersMul constraint;

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

    private StateMul(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersMul constraint){
        this.constraint = constraint;
        this.mul = BigInteger.ONE;
    }

    public static StateMul create(ParametersMul constraint){
        StateMul object = allocator().allocate();
        object.init(constraint);
        return object;
    }

    public String toString(){
        return mul.toString();
    }

    //**************************************//


    @Override
    public NodeState createState(int label, int layer, int size) {
        StateMul state = StateMul.create(constraint);
        BigInteger bigIntLabel = BigInteger.valueOf(label);
        state.mul = mul.multiply(bigIntLabel);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
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

    @Override
    public String hash(int label, int layer, int size){
        BigInteger bigIntLabel = BigInteger.valueOf(label);
        BigInteger newMul = mul.multiply(bigIntLabel);

        BigInteger minPotential = newMul.multiply(constraint.vMin(layer-1));
        BigInteger maxPotential = newMul.multiply(constraint.vMax(layer-1));

        if(constraint.min().compareTo(minPotential) <= 0 && maxPotential.compareTo(constraint.max()) <= 0) return "";
        return newMul.toString();
    }

    @Override
    public void free(){
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateMul type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateMul> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateMul[] arrayCreation(int capacity) {
            return new StateMul[capacity];
        }

        @Override
        protected StateMul createObject(int index) {
            return new StateMul(index);
        }
    }
}
