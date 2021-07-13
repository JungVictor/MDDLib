package builder.constraints.states;

import builder.constraints.parameters.ParametersDiff;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.ArrayOf;
import structures.generics.ListOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

import java.util.Collections;

public class StateDiff extends NodeState {

    private static int G_ID = 0;

    private ArrayOfInt values;
    private SetOf<Integer> diff;
    private ParametersDiff constraint;
    private int identifier = -1;

    public StateDiff(MemoryPool<NodeState> pool) {
        super(pool);
        this.identifier = G_ID++;
    }

    public void init(ParametersDiff constraint, int size){
        this.constraint = constraint;
        this.values = Memory.ArrayOfInt(Math.min(constraint.length(), size - constraint.length() - 1));
        this.diff = Memory.SetOfInteger();
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateDiff state = Memory.StateDiff(constraint, size);
        // TODO
        boolean cond1 = layer > constraint.length();
        boolean cond2 = layer < size - constraint.length();
        if (cond1 || cond2) {
            if (cond1) {
                state.diff.add(Math.abs(label - values.get(0)));
                for (int i = 1; i < values.length; i++) state.values.set(i - 1, values.get(i));
            }
            if (cond2) {
                for (int i = 1; i < values.length; i++) state.values.set(i - 1, values.get(i));
                state.values.set(values.length - 1, label);
            } else {
                for (int i = 1; i < values.length; i++) state.values.set(i - 1, values.get(i));
                state.values.set(values.length - 1, -1);
            }
        } else for (int i = 0; i < values.length; i++) state.values.set(i, values.get(i));
        state.diff.add(diff);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size) {
        if(layer <= constraint.length()) return true;
        return !diff.contains(Math.abs(values.get(0) - label));
    }

    @Override
    public boolean isValid(int label, int layer, int size, NodeState nodeState){
        if(layer <= constraint.length()) return true;
        if(diff.contains(Math.abs(values.get(0) - label))) return false;
        StateDiff state = (StateDiff) nodeState;
        if(state.diff.contains(Math.abs(values.get(0) - label))) return false;
        SetOf<Integer> test = Memory.SetOfInteger();
        test.add(diff);
        test.intersect(state.diff);
        return test.size() == 0;
    }

    public boolean isValid(int label, int layer, int size, ArrayOf<NodeState> nodestates){
        ArrayOfInt values = Memory.ArrayOfInt(nodestates.length + 1);
        values.set(0, Math.abs(this.values.get(0) - label));
        for(int i = 0; i < nodestates.length; i++) {
            StateDiff state = (StateDiff) nodestates.get(i);
            if(layer <= state.constraint.length()) values.set(i+1, -1);
            else values.set(i+1, Math.abs(state.values.get(0) - label));
        }

        for(int value : values){
            if(diff.contains(value)) return false;
            for(NodeState nodeState : nodestates) {
                StateDiff state = (StateDiff) nodeState;
                if(state.diff.contains(value)) return false;
            }
        }

        return true;
    }

    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateDiff convert = (StateDiff) state;
        StateDiff merged = Memory.StateDiff(constraint, size);
        merged.diff.add(diff);
        merged.diff.add(convert.diff);
        return merged;
    }

    @Override
    public NodeState copy(){
        StateDiff copy = Memory.StateDiff(constraint, Integer.MAX_VALUE);
        copy.diff.add(diff);
        return copy;
    }

    @Override
    public String hash(int label, int layer, int size) {
        StringBuilder builder = new StringBuilder();
        builder.append(identifier);
        builder.append("[");

        ListOf<Integer> integers = Memory.ListOfInteger();
        integers.add(diff);

        ArrayOfInt next = Memory.ArrayOfInt(values.length);

        // TODO

        boolean cond1 = layer > constraint.length();
        boolean cond2 = layer < size - constraint.length();
        if (cond1 || cond2) {
            if (cond1) {
                integers.add(Math.abs(label - values.get(0)));
                for (int i = 1; i < values.length; i++) next.set(i - 1, values.get(i));
            }
            if (cond2) {
                for (int i = 1; i < values.length; i++) next.set(i - 1, values.get(i));
                next.set(values.length - 1, label);
            } else {
                for (int i = 1; i < values.length; i++) next.set(i - 1, values.get(i));
                next.set(values.length - 1, -1);
            }
        } else for (int i = 0; i < values.length; i++) next.set(i, values.get(i));

        for(int i = 0; i < next.length - 1; i++) {
            builder.append(next.get(i));
            builder.append(", ");
        }
        builder.append(next.get(next.length - 1));

        Memory.free(next);

        builder.append("] {");

        Collections.sort(integers.getList());
        if(integers.size() != 0) {
            for (int i = 0; i < integers.size() - 1; i++) {
                builder.append(integers.get(i));
                builder.append(", ");
            }
            builder.append(integers.get(integers.size() - 1));
        }
        builder.append("}");

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
