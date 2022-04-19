package dd.mdd.costmdd.components;

import dd.mdd.components.Node;
import dd.interfaces.CostNodeInterface;
import memory.AllocatorOf;

public class CostNode extends Node implements CostNodeInterface {
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
    public CostNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a CostNode.
     * The object is managed by the allocator.
     * @return A fresh CostNode
     */
    public static CostNode create(){
        CostNode node = allocator().allocate();
        node.prepare();
        return node;
    }


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    public void setArcCost(int label, int cost){
        ((OutCostArcs) getChildren()).setCost(label, cost);
        ((InCostArcs) getParents()).setCost(label, this, cost);
    }

    /**
     * {@inheritDoc}
     */
    public void addChild(int label, Node child, int cost){
        ((OutCostArcs) getChildren()).add(label, child, cost);
    }

    /**
     * {@inheritDoc}
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
     * <b>The allocator that is in charge of the CostNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<CostNode> {

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
        protected CostNode[] arrayCreation(int capacity) {
            return new CostNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CostNode createObject(int index) {
            return new CostNode(index);
        }
    }
}
