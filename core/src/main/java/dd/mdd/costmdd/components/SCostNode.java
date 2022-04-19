package dd.mdd.costmdd.components;

import dd.mdd.components.Node;
import dd.mdd.components.SNode;
import memory.AllocatorOf;

public class SCostNode extends SNode {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


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
    public SCostNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a SCostNode.
     * The object is managed by the allocator.
     * @return A fresh CostNode
     */
    public static SCostNode create(){
        SCostNode node = allocator().allocate();
        node.prepare();
        return node;
    }


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * Set the cost to the arc corresponding to the given label
     * @param label Label of the arc
     * @param cost Cost of the arc
     */
    public void setArcCost(int label, int cost){
        ((OutCostArcs) getChildren()).setCost(label, cost);
        ((InCostArcs) getParents()).setCost(label, this, cost);
    }

    /**
     * Add a child to this node with the given label
     * @param label Label of the arc
     * @param child Node to add as a child
     * @param cost Cost of the arc
     */
    public void addChild(int label, Node child, int cost){
        ((OutCostArcs) getChildren()).add(label, child, cost);
    }

    /**
     * Add a parent to this node with the given label
     * @param label Label of the ingoing arc
     * @param parent Node to add as a parent
     * @param cost Cost of the arc
     */
    public void addParent(int label, Node parent, int cost){
        ((InCostArcs) getParents()).add(label, parent, cost);
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
    protected void allocateArcs(){
        setChildren(OutCostArcs.create());
        setParents(InCostArcs.create());
    }

    /**
     * <b>The allocator that is in charge of the SCostNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<SCostNode> {

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
        protected SCostNode[] arrayCreation(int capacity) {
            return new SCostNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SCostNode createObject(int index) {
            return new SCostNode(index);
        }
    }

}
