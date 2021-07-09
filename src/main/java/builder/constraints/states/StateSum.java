package builder.constraints.states;

import builder.constraints.parameters.ParametersSum;
import memory.Memory;
import memory.MemoryPool;

public class StateSum extends NodeState {

    private int sum;
    private ParametersSum constraint;

    public StateSum(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersSum constraint){
        this.constraint = constraint;
        this.sum = 0;
    }

    public String toString(){
        return Integer.toString(sum);
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateSum state = Memory.StateSum(constraint);
        state.sum = sum + label;
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        int minPotential = sum + label + constraint.vMin(layer-1);
        int maxPotential = sum + label + constraint.vMax(layer-1);


        if(maxPotential < constraint.min() || constraint.max() < minPotential) return false;
        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return true;
        return sum + label <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        int minPotential = sum + label + constraint.vMin(layer-1);
        int maxPotential = sum + label + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Integer.toString(sum + label);
    }

    @Override
    public void free(){
        super.free();
        this.constraint = null;
    }

}