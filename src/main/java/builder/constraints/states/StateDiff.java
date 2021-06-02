package builder.constraints.states;

import builder.constraints.parameters.ParametersDiff;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.ListOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

import java.util.Collections;

public class StateDiff extends NodeState {

    private ArrayOfInt values;
    private SetOf<Integer> diff;
    private ParametersDiff constraint;

    public StateDiff(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersDiff constraint, int size){
        this.constraint = constraint;
        this.values = Memory.ArrayOfInt(Math.min(constraint.length(), size - constraint.length()));
        this.diff = Memory.SetOfInteger();
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateDiff state = Memory.StateDiff(constraint, size);
        // TODO

        state.diff.add(diff);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size) {
        // TODO
        return !diff.contains(Math.abs(values.get(0) - label));
    }

    @Override
    public boolean isValid(int label, int layer, int size, NodeState nodeState){
        // TODO
        StateDiff state = (StateDiff) nodeState;
        SetOf<Integer> test = Memory.SetOfInteger();
        test.add(diff);
        test.intersect(state.diff);
        return test.size() == 0;
    }

    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateDiff convert = (StateDiff) state;
        StateDiff merged = Memory.StateDiff(constraint, size);
        merged.diff.add(diff);
        merged.diff.add(convert.diff);
        return merged;
    }

    public NodeState copy(){
        StateDiff copy = Memory.StateDiff(constraint, -1);
        copy.diff.add(diff);
        return copy;
    }

    @Override
    public String hash(int label, int layer, int size) {
        StringBuilder builder = new StringBuilder();

        // TODO

        ListOf<Integer> integers = Memory.ListOfInteger();
        integers.add(diff);
        Collections.sort(integers.getList());

        Memory.free(integers);
        return builder.toString();
    }


    @Override
    public void free(){
        super.free();
        Memory.free(diff);
        Memory.free(values);
        this.constraint = null;
        this.diff = null;
        this.values = null;
    }

}
