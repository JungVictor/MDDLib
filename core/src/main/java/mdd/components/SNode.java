package mdd.components;

import builder.constraints.states.NodeState;
import memory.Memory;
import memory.MemoryPool;

public class SNode extends Node {

    private NodeState state;

    public SNode(MemoryPool<Node> pool) {
        super(pool);
    }

    public void setState(NodeState state){
        this.state = state;
    }

    public NodeState getState(){
        return state;
    }


    @Override
    public Node Node(){
        return Memory.SNode();
    }

    @Override
    public void free(){
        if(state != null) Memory.free(state);
        state = null;
        super.free();
    }

}
