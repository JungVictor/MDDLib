package costmdd;

import costmdd.components.CostNode;
import mdd.MDD;
import mdd.components.Node;
import memory.AllocatorOf;

import java.util.InputMismatchException;

public class CostMDD extends MDD {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static MDD create(Node root){
        MDD mdd = allocator().allocate();
        mdd.setRoot(root);
        return mdd;
    }

    public static MDD create(){
        MDD mdd = allocator().allocate();
        mdd.setRoot(mdd.Node());
        return mdd;
    }

    protected CostMDD(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public void setRoot(Node node){
        if(node instanceof CostNode) super.setRoot(node);
        else throw new InputMismatchException("Expected the root to be at least a CostNode !");
    }

    @Override
    public Node Node(){
        if(getRoot() != null) return getRoot().Node();
        return CostNode.create();
    }

    @Override
    public MDD MDD(){
        return MDD(Node());
    }

    @Override
    public MDD MDD(Node root){
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

        @Override
        protected CostMDD[] arrayCreation(int capacity) {
            return new CostMDD[capacity];
        }

        @Override
        protected CostMDD createObject(int index) {
            return new CostMDD(index);
        }
    }

}
