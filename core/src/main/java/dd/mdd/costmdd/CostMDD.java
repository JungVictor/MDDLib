package dd.mdd.costmdd;

import dd.AbstractNode;
import dd.mdd.costmdd.components.CostNode;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import memory.AllocatorOf;

import java.util.InputMismatchException;

/**
 * <b>The class representing the CostMDD.</b> <br>
 * Extends the <b>MDD</b> class, adding a value to the arcs (cost).
 */
public class CostMDD extends MDD {

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
    protected CostMDD(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a CostMDD with given node as root.
     * The object is managed by the allocator.
     * @param root Node to use as a root
     * @return A fresh CostMDD
     */
    public static CostMDD create(Node root){
        CostMDD mdd = allocator().allocate();
        mdd.setRoot(root);
        return mdd;
    }

    /**
     * Create a CostMDD.
     * The object is managed by the allocator.
     * @return A fresh CostMDD
     */
    public static CostMDD create(){
        CostMDD mdd = allocator().allocate();
        mdd.setRoot(mdd.Node());
        return mdd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(Node node){
        if(node instanceof CostNode) super.setRoot(node);
        else throw new InputMismatchException("Expected the root to be at least a CostNode !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node Node(){
        if(getRoot() != null) return getRoot().Node();
        return CostNode.create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CostMDD DD(){
        return MDD(Node());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CostMDD DD(AbstractNode root){
        return MDD((Node) root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CostMDD MDD(){
        return create(Node());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CostMDD MDD(Node root){
        return create(root);
    }


    //**************************************//
    //             MANAGE MDD               //
    //**************************************//

    /**
     * Add an arc between the source node and the destination node with the given value as label.
     * Ensures the connection between the two nodes
     * @param source The source node (parent)
     * @param value The value of the arc's label
     * @param destination The destination node (child)
     * @param cost Cost of the arc
     * @param layer The layer of the PARENT node (source)
     */
    public void addArc(Node source, int value, Node destination, int cost, int layer){
        ((CostNode) source).addChild(value, destination, cost);
        ((CostNode) destination).addParent(value, source, cost);
        addValue(value, layer);
    }

    /**
     * Add the node destination to the given layer, and add an arc between the source node
     * and the destination node with the given value as label.
     * @param source The source node
     * @param value The value of the arc's label
     * @param destination The destination node - node to add in the MDD
     * @param cost Cost of the arc
     * @param layer The layer of the MDD where the node destination will be added
     */
    public void addArcAndNode(Node source, int value, Node destination, int cost, int layer){
        addArc(source, value, destination, cost, layer);
        addNode(destination, layer);
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
     * <b>The allocator that is in charge of the CostMDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<CostMDD> {

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
        protected CostMDD[] arrayCreation(int capacity) {
            return new CostMDD[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CostMDD createObject(int index) {
            return new CostMDD(index);
        }
    }

}
