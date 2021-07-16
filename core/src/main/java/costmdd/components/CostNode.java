package costmdd.components;

import mdd.components.Node;
import memory.AllocatorOf;
import memory.Memory;

public class CostNode extends Node {
    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static CostNode create(){
        CostNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    public CostNode(int allocatedIndex) {
        super(allocatedIndex);
    }


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

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

    @Override
    protected void dealloc(){
        allocator().free(this);
    }

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
            this(16);
        }

        @Override
        protected CostNode[] arrayCreation(int capacity) {
            return new CostNode[capacity];
        }

        @Override
        protected CostNode createObject(int index) {
            return new CostNode(index);
        }
    }


}
