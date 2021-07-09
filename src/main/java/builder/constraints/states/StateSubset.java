package builder.constraints.states;

import builder.constraints.parameters.ParametersSubset;
import memory.Memory;
import memory.MemoryPool;

public class StateSubset extends NodeState {

    private int setID;
    private ParametersSubset constraint;

    public StateSubset(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersSubset constraint, int setID){
        this.constraint = constraint;
        this.setID = setID;
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        return Memory.StateSubset(constraint, constraint.getNext(setID, label));
    }

    @Override
    public boolean isValid(int label, int layer, int size) {
        if(constraint.isEmpty(setID)) return label == -1;
        return constraint.isIn(setID, label);
    }

    @Override
    public String hash(int label, int layer, int size) {
        return Integer.toString(constraint.getNext(setID, label));
    }
}
