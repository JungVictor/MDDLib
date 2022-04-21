package dd.mdd.costmdd.components;

import dd.interfaces.NodeInterface;
import dd.mdd.components.Node;
import dd.mdd.components.SNode;
import dd.interfaces.CostNodeInterface;
import memory.AllocatorOf;

public class SCostNode extends SNode implements CostNodeInterface {

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
     * @return A fresh SCostNode
     */
    public static SCostNode create(){
        SCostNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SCostNode Node(){
        return create();
    }

    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArcCost(int label, int cost){
        ((OutCostArcs) getChildren()).setCost(label, cost);
        ((InCostArcs) getChild(label).getParents()).setCost(label, this, cost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getArcCost(int label){
        return ((OutCostArcs) getChildren()).getCost(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getArcCost(NodeInterface parent, int label) {
        return ((InCostArcs) getParents()).getCost((Node) parent, label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(int label, Node child, int cost){
        ((OutCostArcs) getChildren()).add(label, child, cost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
