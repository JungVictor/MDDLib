package builder.constraints.states;

import builder.constraints.parameters.ParametersAllDiff;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.SetOf;

public class StateAllDiff extends NodeState {

    private SetOf<Integer> alldiff;
    private ParametersAllDiff constraint;

    public StateAllDiff(MemoryPool<NodeState> pool) {
        super(pool);
    }

    public void init(ParametersAllDiff constraint){
        this.alldiff = Memory.SetOfInteger();
        this.constraint = constraint;
    }


    @Override
    public NodeState createState(int label, int layer, int size) {
        StateAllDiff state = Memory.StateAllDiff(constraint);
        state.alldiff.add(alldiff);
        if(constraint.contains(label)) alldiff.add(label);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        return !constraint.contains(label) || !alldiff.contains(label);
    }

    @Override
    public String hash(int label, int layer, int size) {
        StringBuilder builder = new StringBuilder();
        for(int v : alldiff) {
            if(v == label || constraint.contains(v)) builder.append("1");
            else builder.append("0");
        }
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(alldiff);
        super.free();
    }
}
