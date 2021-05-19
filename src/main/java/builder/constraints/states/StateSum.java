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
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateSum state = Memory.StateSum(constraint);
        state.sum = sum + label;
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        int minPotential = (size - layer - 1) * constraint.vMin();
        int maxPotential = (size - layer - 1) * constraint.vMax();

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return true;
        return constraint.min() <= sum + label && sum + label <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        int minPotential = (size - layer - 1) * constraint.vMin();
        int maxPotential = (size - layer - 1) * constraint.vMax();

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Integer.toString(sum);
    }

}
