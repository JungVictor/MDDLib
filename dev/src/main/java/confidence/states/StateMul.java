package confidence.states;

import builder.constraints.states.NodeState;
import memory.MemoryPool;
import confidence.MyMemory;
import confidence.parameters.ParametersMul;

import java.math.BigInteger;

public class StateMul extends NodeState {
    private BigInteger mul;
    private ParametersMul constraint;

    public StateMul(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersMul constraint){
        this.constraint = constraint;
        this.mul = BigInteger.ONE;
    }

    public String toString(){
        return mul.toString();
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateMul state = MyMemory.StateMul(constraint);
        BigInteger bigIntLabel = BigInteger.valueOf(label);
        state.mul = mul.multiply(bigIntLabel);
        return state;
    }

    //Revoir cette méthode pour le cas des négatifs
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

        //FLAG
        BigInteger minPotential = newMul.multiply(constraint.vMin(layer-1));
        BigInteger maxPotential = newMul.multiply(constraint.vMax(layer-1));

        if(constraint.min().compareTo(minPotential) <= 0 && maxPotential.compareTo(constraint.max()) <= 0) return "";
        return newMul.toString();
    }

    @Override
    public void free(){
        super.free();
        this.constraint = null;
    }
}
