package builder.constraints.states;

import builder.constraints.parameters.ParametersGCC;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.ListOf;
import structures.generics.MapOf;

import java.util.Collections;

public class StateGCC extends NodeState {

    // Private reference
    private MapOf<Integer, Integer> count;
    private int minimum;

    // Shared references : constraint
    private ParametersGCC constraint;

    public StateGCC(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersGCC constraint){
        this.constraint = constraint;
        this.minimum = constraint.minimum();
        this.count = Memory.MapOfIntegerInteger();
    }

    public void initV(){
        for(int v : constraint.V()) count.put(v,0);
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateGCC state = Memory.StateGCC(constraint);
        state.minimum = minimum;
        int potential = size - layer - 1;
        for(int v : count) {
            if(count.get(v) < constraint.min(v) || count.get(v) + potential > constraint.max(v)) state.count.put(v, count.get(v));
        }
        if(state.count.contains(label)){
            if (count.get(label) < constraint.min(label)) state.minimum--;
            // If we are sure that, whatever the value, we satisfy the gcc, we remove the value
            // So we only add the value when we are not sure
            if(count.get(label) + 1 >= constraint.min(label) && count.get(label) + potential + 1 <= constraint.max(label)) state.count.remove(label);
            else state.count.put(label, state.count.get(label) + 1);
            //state.count.put(label, state.count.get(label) + 1);
        }
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size) {
        int potential = size - layer - 1;
        int minimum = this.minimum;

        if(!count.contains(label)) return minimum <= potential;
        int value = count.get(label);
        if(value < constraint.min(label)) minimum--;
        return minimum <= potential && value+1 <= constraint.max(label);
    }

    @Override
    public String hash(int label, int layer, int size){
        ListOf<Integer> integers = Memory.ListOfInteger();
        integers.add(count.keySet());
        Collections.sort(integers.getList());
        StringBuilder builder = new StringBuilder();
        for (int v : integers) {
            if(v == label && count.get(label) + size - layer <= constraint.max(label)) continue;
            else if(count.get(v) >= constraint.min(v) && count.get(v) + (size-1) - layer <= constraint.max(v)) continue;
            builder.append(v);
            builder.append(" -> ");
            if(v != label) builder.append(count.get(v));
            else builder.append(count.get(v)+1);
            builder.append("; ");
        }
        Memory.free(integers);
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(count);
        this.constraint = null;
        super.free();
    }
}
