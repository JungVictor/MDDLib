package mdd.components;

import builder.constraints.states.NodeState;
import memory.AllocatorOf;
import memory.Memory;

public class SNode extends Node {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // State of the node
    private NodeState state;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static SNode create(){
        SNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    public SNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public Node Node(){
        return create();
    }


    //**************************************//
    //          STATES MANAGEMENT           //
    //**************************************//

    public void setState(NodeState state){
        this.state = state;
    }

    public NodeState getState(){
        return state;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    protected void dealloc(){
        allocator().free(this);
    }

    @Override
    public void free(){
        if(state != null) Memory.free(state);
        state = null;
        super.free();
    }

    /**
     * <b>The allocator that is in charge of the SNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<SNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected SNode[] arrayCreation(int capacity) {
            return new SNode[capacity];
        }

        @Override
        protected SNode createObject(int index) {
            return new SNode(index);
        }
    }

}
