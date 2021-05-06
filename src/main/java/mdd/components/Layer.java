package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import representation.MDDVisitor;
import structures.generics.SetOf;

import java.util.Iterator;

public class Layer implements MemoryObject, Iterable<Node> {

    // MemoryObject variables
    private final MemoryPool<Layer> pool;
    private int ID = -1;
    //

    private SetOf<Node> nodes;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public Layer(MemoryPool<Layer> pool){
        this.pool = pool;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * Accept a MDDVisitor. Used to represent the MDD.
     * @param visitor
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
    public SetOf<Node> getNodes(){
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
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        nodes = Memory.SetOfNode();
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        Memory.free(nodes);
        this.pool.free(this, ID);
    }

    public void freeAllNodes(){
        for(Node node : nodes) {
            node.remove();
            Memory.free(node);
        }
        nodes.clear();
    }

    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Node> interface
    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}
