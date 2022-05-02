package dd.bdd.components;

import builder.constraints.states.NodeState;
import dd.interfaces.IStateNode;
import memory.AllocatorOf;
import memory.Memory;

public class BinaryStateNode extends BinaryNode implements IStateNode {

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
    public BinaryStateNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a BinarySNode.
     * The object is managed by the allocator.
     * @return A fresh BinarySNode
     */
    public static BinaryStateNode create(){
        BinaryStateNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryStateNode Node(){
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
    static final class Allocator extends AllocatorOf<BinaryStateNode> {

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
        protected BinaryStateNode[] arrayCreation(int capacity) {
            return new BinaryStateNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected BinaryStateNode createObject(int index) {
            return new BinaryStateNode(index);
        }
    }
}
