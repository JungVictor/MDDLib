package pmdd;

import mdd.MDD;
import mdd.components.Node;
import memory.AllocatorOf;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import structures.generics.MapOf;

import java.util.InputMismatchException;

/**
 * <b>The MDD with added properties.</b> <br>
 * It extends the basic MDD, override the MDD() and Node() functions, and add functions specific to properties.
 * The root node must at least be of type PNode !
 */
public class PMDD extends MDD {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static PMDD create(Node root){
        PMDD mdd = allocator().allocate();
        mdd.setRoot(root);
        return mdd;
    }

    public static PMDD create(){
        PMDD mdd = allocator().allocate();
        mdd.setRoot(mdd.Node());
        return mdd;
    }

    protected PMDD(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public void setRoot(Node node){
        if(node instanceof PNode) super.setRoot(node);
        else throw new InputMismatchException("Expected the root to be at least a PNode !");
    }

    @Override
    public Node Node(){
        if(getRoot() != null) return getRoot().Node();
        return PNode.create();
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
    //              PROPERTIES              //
    //**************************************//

    /**
     * Propagate all properties from the root node through the MDD
     * @return The map of name -> property of the tt node after the propagation
     */
    public MapOf<String, NodeProperty> propagateProperties(){
        return propagateProperties(true);
    }

    public MapOf<String, NodeProperty> propagateProperties(boolean clean) {
        for(int i = 0; i < size() - 1; i++){
            for(Node node : getLayer(i)) {
                ((PNode) node).transferProperties();
                if(clean) ((PNode) node).clearProperties();
            }
        }
        return ((PNode) getTt()).getProperties();
    }

    /**
     * Add a property to the root node.
     * @param propertyName The name of the property
     * @param property The property to add
     */
    public void addRootProperty(String propertyName, NodeProperty property){
        ((PNode) getRoot()).addProperty(propertyName, property);
    }

    /**
     * Remove a property from the root node
     * @param propertyName The name of the property to remove
     * @return the property removed (null if none)
     */
    public NodeProperty removeRootProperty(String propertyName){
        return ((PNode) getRoot()).removeProperty(propertyName);
    }

    /**
     * Get the specified property from the tt node.
     * Perform this after the propogateProperties operation
     * (otherwise the tt node won't have any property)
     * @param propertyName The name of the property to get
     * @return the specified property from the tt node.
     */
    public NodeProperty getTTProperty(String propertyName){
        return ((PNode) getTt()).getProperty(propertyName);
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the PMDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PMDD> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected PMDD[] arrayCreation(int capacity) {
            return new PMDD[capacity];
        }

        @Override
        protected PMDD createObject(int index) {
            return new PMDD(index);
        }
    }
}
