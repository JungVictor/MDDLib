package builder.constraints.states;

import memory.MemoryObject;
import memory.MemoryPool;

public abstract class NodeState implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<NodeState> pool;
    private int ID = -1;
    //

    public NodeState(MemoryPool<NodeState> pool){
        this.pool = pool;
    }

    public abstract NodeState createState(int label, int layer, int size);

    public boolean isValid(int label, int layer, int size){
        return true;
    }

    public abstract String hash(int label, int layer, int size);

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
