package confidence.states;

import builder.constraints.states.NodeState;
import memory.MemoryPool;
import confidence.MyMemory;
import confidence.parameters.ParametersSumDouble;

public class StateSumDouble extends NodeState {

    private double sum;
    private ParametersSumDouble constraint;

    public StateSumDouble(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersSumDouble constraint){
        this.constraint = constraint;
        this.sum = 0;
    }

    public String toString(){
        return Double.toString(sum);
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateSumDouble state = MyMemory.StateSumDouble(constraint);
        state.sum = sum + constraint.mapDouble(label);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        double doubleLabel = constraint.mapDouble(label);
        double minPotential = sum + doubleLabel + constraint.vMin(layer-1);
        double maxPotential = sum + doubleLabel + constraint.vMax(layer-1);


        if(maxPotential < constraint.min() || constraint.max() < minPotential) return false;
        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return true;
        return sum + doubleLabel <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        double doubleLabel = constraint.mapDouble(label);
        double minPotential = sum + doubleLabel + constraint.vMin(layer-1);
        double maxPotential = sum + doubleLabel + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Math.floor((sum + doubleLabel) * Math.pow(10, constraint.precision())) + "";
    }

    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateSumDouble stateDouble = (StateSumDouble) state;
        double s = stateDouble.sum + constraint.mapDouble(label);
        if(s < sum) sum = s;
        return null;
    }

    @Override
    public void free(){
        super.free();
        this.constraint = null;
    }

}
