package dd.bdd.components;

import builder.constraints.states.NodeState;
import dd.interfaces.StateNodeInterface;
import memory.AllocatorOf;
import memory.Memory;

public class BinarySNode extends BinaryNode implements StateNodeInterface {

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
    public BinarySNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a BinarySNode.
     * The object is managed by the allocator.
     * @return A fresh BinarySNode
     */
    public static BinarySNode create(){
        BinarySNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinarySNode Node(){
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
     * <b>The allocator that is in charge of the BinarySNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<BinarySNode> {

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
        protected BinarySNode[] arrayCreation(int capacity) {
            return new BinarySNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected BinarySNode createObject(int index) {
            return new BinarySNode(index);
        }
    }
}
