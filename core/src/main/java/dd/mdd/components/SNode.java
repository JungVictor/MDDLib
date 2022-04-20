package dd.mdd.components;

import builder.constraints.states.NodeState;
import dd.interfaces.StateNodeInterface;
import memory.AllocatorOf;
import memory.Memory;

public class SNode extends Node implements StateNodeInterface {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // State of the node
    private NodeState state;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    public SNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a SNode.
     * The object is managed by the allocator.
     * @return A fresh SNode
     */
    public static SNode create(){
        SNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StateNodeInterface Node(){
        return create();
    }


    //**************************************//
    //          STATES MANAGEMENT           //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    public void setState(NodeState state){
        this.state = state;
    }

    /**
     * {@inheritDoc}
     */
    public NodeState getState(){
        return state;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * {@inheritDoc}
     */
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
            super.init();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SNode[] arrayCreation(int capacity) {
            return new SNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SNode createObject(int index) {
            return new SNode(index);
        }
    }

}
