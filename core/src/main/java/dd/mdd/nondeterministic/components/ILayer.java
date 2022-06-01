package dd.mdd.nondeterministic.components;

import dd.interfaces.INode;
import memory.Allocable;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.SetOfNode;

import java.util.Iterator;

/**
 * <b>This class is used to represent a layer of a NDMDD</b> <br>
 * The Layer is the structure used to stock and manage nodes.
 * As a layer is implemented using a Set, there is no notion of order. However, the notion of order in a layer is not important.
 */
public class ILayer implements Allocable, Iterable<INode> {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    private SetOfNode<INode> nodes;


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
    private ILayer(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a Layer.
     * The object is managed by the allocator.
     * @return A fresh Layer
     */
    public static ILayer create(){
        ILayer layer = allocator().allocate();
        layer.prepare();
        return layer;
    }


    //**************************************//
    //           LAYER MANAGEMENT           //
    //**************************************//

    /**
     * Add a node to the layer
     * @param node The node to add
     */
    public void add(INode node){
        nodes.add(node);
    }

    /**
     * Remove the node from the layer
     * @param node The node to remove
     */
    public void remove(INode node){
        nodes.remove(node);
    }

    /**
     * Remove the node from the layer AND free it from memory
     * @param node The node to remove and free
     */
    public void removeAndFree(INode node){
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
    public boolean contains(INode node){
        return nodes.contains(node);
    }

    /**
     * Get all nodes that are in the layer
     * @return all nodes that are in the layer
     */
    public SetOfNode<INode> getNodes(){
        return nodes;
    }

    /**
     * Get a node from the layer
     * @return a node from the layer
     */
    public INode getNode(){
        for(INode node : nodes) return node;
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
        nodes = Memory.SetOfINode();
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
        for(INode node : nodes) {
            node.remove();
            Memory.free(node);
        }
        nodes.clear();
    }

    /**
     * <b>The allocator that is in charge of the NDLayer type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ILayer> {

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
        protected ILayer[] arrayCreation(int capacity) {
            return new ILayer[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ILayer createObject(int index) {
            return new ILayer(index);
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
    public Iterator<INode> iterator() {
        return nodes.iterator();
    }
}
