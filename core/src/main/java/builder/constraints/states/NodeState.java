package builder.constraints.states;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.ArrayOf;

public abstract class NodeState implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<NodeState> pool;
    private int ID = -1;
    //

    public NodeState(MemoryPool<NodeState> pool){
        this.pool = pool;
    }

    public abstract NodeState createState(int label, int layer, int size);

    public abstract boolean isValid(int label, int layer, int size);

    public boolean isValid(int label, int layer, int size, NodeState state){
        return isValid(label, layer, size);
    }
    public boolean isValid(int label, int layer, int size, ArrayOf<NodeState> states){
        return false;
    }

    public NodeState merge(NodeState state, int label, int layer, int size) {
        return null;
    }

    public abstract String hash(int label, int layer, int size);

    public NodeState copy(){
        return null;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void setID(int id){
        this.ID = id;
    }

    @Override
    public void free(){
        pool.free(this, this.ID);
    }

    @Override
    public void prepare(){}

}
