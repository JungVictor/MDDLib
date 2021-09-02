package builder.constraints.states;

import memory.Allocable;
import structures.Signature;

public abstract class NodeState implements Allocable {

    // Index in Memory
    private final int allocatedIndex;

    public NodeState(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public abstract NodeState createState(int label, int layer, int size);

    public abstract boolean isValid(int label, int layer, int size);

    public NodeState merge(NodeState state, int label, int layer, int size) {
        return null;
    }

    public abstract String hash(int label, int layer, int size);
    public Signature hash(int label, int layer, int size, boolean test){
        return null;
    }

    public NodeState copy(){
        return null;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

}
