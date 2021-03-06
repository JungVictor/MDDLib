package dd.mdd.components;

import memory.Allocable;
import memory.AllocatorOf;
import memory.Memory;
import representation.MDDVisitor;
import structures.generics.SetOfNode;

import java.util.Iterator;

/**
 * <b>This class is used to represent a layer of a MDD</b> <br>
 * The Layer is the structure used to stock and manage nodes.
 * As a layer is implemented using a Set, there is no notion of order. However, the notion of order in a layer is not important.
 */
public class Layer implements Allocable, Iterable<Node> {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    private SetOfNode<Node> nodes;


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
    private Layer(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a Layer.
     * The object is managed by the allocator.
     * @return A fresh Layer
     */
    public static Layer create(){
        Layer layer = allocator().allocate();
        layer.prepare();
        return layer;
    }

    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * Accept a MDDVisitor. Used to represent the MDD.
     * @param visitor The visitor
     */
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
        for(Node node : nodes) node.accept(visitor);
    }


    //**************************************//
    //           LAYER MANAGEMENT           //
    //**************************************//

    /**
     * Add a node to the layer
     * @param node The node to add
     */
    public void add(Node node){
        nodes.add(node);
    }

    /**
     * Remove the node from the layer
     * @param node The node to remove
     */
    public void remove(Node node){
        nodes.remove(node);
    }

    /**
     * Remove the node from the layer AND free it from memory
     * @param node The node to remove and free
     */
    public void removeAndFree(Node node){
        node.remove();
        nodes.remove(node);
        Memory.free(node);
    }

    /**
     * Remove all nodes from the layer
     */
    public void clear(){
        nodes.clear();
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get the number of nodes in the layer
     * @return the number of nodes in the layer
     */
    public int size(){
        return nodes.size();
    }

    /**
     * Check if the node is in the layer
     * @param node Node to check
     * @return true if the node is in the layer, false otherwise
     */
    public boolean contains(Node node){
        return nodes.contains(node);
    }

    /**
     * Get all nodes that are in the layer
     * @return all nodes that are in the layer
     */
    public SetOfNode<Node> getNodes(){
        return nodes;
    }

    /**
     * Get a node from the layer
     * @return a node from the layer
     */
    public Node getNode(){
        for(Node node : nodes) return node;
        return null;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    /**
     * Prepare the object to be used a new one
     */
    private void prepare() {
        nodes = Memory.SetOfNode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        Memory.free(nodes);
        allocator().free(this);
    }

    /**
     * Free all nodes contained in the layer
     */
    public void freeAllNodes(){
        for(Node node : nodes) {
            node.remove();
            Memory.free(node);
        }
        nodes.clear();
    }

    /**
     * <b>The allocator that is in charge of the Layer type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<Layer> {

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
        protected Layer[] arrayCreation(int capacity) {
            return new Layer[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Layer createObject(int index) {
            return new Layer(index);
        }
    }

    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Node> interface
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}
