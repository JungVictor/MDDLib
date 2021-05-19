package builder.constraints.states;

import builder.constraints.parameters.ParametersGCC;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.MapOf;

public class StateGCC extends NodeState {

    // Private reference
    private MapOf<Integer, Integer> count;
    private int minimum = 0;

    // Shared references : constraint
    private ParametersGCC constraint;

    public StateGCC(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersGCC constraint){
        this.constraint = constraint;
        this.count = Memory.MapOfIntegerInteger();
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateGCC state = Memory.StateGCC(constraint);
        state.minimum = minimum;
        for(int v : count) state.count.put(v, count.get(v));
        if(constraint.contains(label)){
            if(count.get(label) < constraint.min(label)) state.minimum--;
            // If we are sure that, whatever the value, we satisfy the gcc, we remove the value
            // So we only add the value when we are not sure
            else if(count.get(label) + size - layer - 1 > constraint.max(label)) {
                state.count.put(label, state.count.get(label) + 1);
            }
        }
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size) {
        int potential = size - label - 1;
        int minimum = this.minimum;

        if(!constraint.contains(label)) return minimum <= potential;
        int value = count.get(label);
        if(value < constraint.min(label)) minimum--;
        return minimum <= potential && value+1 <= constraint.max(label);
    }

    @Override
    public String hash(int label, int layer, int size){
        return count.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(count);
        super.free();
    }
}
