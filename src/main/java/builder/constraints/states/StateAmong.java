package builder.constraints.states;

import builder.constraints.parameters.ParametersAmong;
import memory.Memory;
import memory.MemoryPool;
import structures.integers.ArrayOfInt;

public class StateAmong extends NodeState {

    private ArrayOfInt among;

    // Must not be free
    private ParametersAmong constraint;

    public StateAmong(MemoryPool<NodeState> pool){
        super(pool);
    }

    public void init(ParametersAmong constraint){
        this.constraint = constraint;
        among = Memory.ArrayOfInt(constraint.q());
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateAmong state = Memory.StateAmong(constraint);
        for(int i = 1; i < constraint.q(); i++) state.among.set(i-1, among.get(i));
        if(constraint.contains(label)) state.among.set(constraint.q()-1, 1);
        else state.among.set(constraint.q()-1, 0);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        int cpt = 0;
        for(int i = 1; i < constraint.q(); i++) cpt += among.get(i);
        if(constraint.contains(label)) cpt++;
        int potential = constraint.q() - layer - 1;
        if(potential < 0) potential = 0;
        return constraint.min() <= cpt + potential && cpt <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if(among.length > 0) {
            for (int i = 1; i < among.length; i++) {
                builder.append(among.get(i));
                builder.append(", ");
            }
            builder.append(constraint.contains(label) ? 1 : 0);
        }
        builder.append("]");
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(among);
        super.free();
    }
}
