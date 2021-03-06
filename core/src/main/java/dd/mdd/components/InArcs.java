package dd.mdd.components;

import memory.*;
import structures.generics.SetOfNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * <b>This class is used to represent the binding between a node and its parents.</b> <br>
 * The in-going arcs (InArcs) is represented using a Map, binding the labels of the arcs to the parents node.
 * As a node can have multiple parents having the same label, the map is actually binding an integer to a set of nodes.
 */
public class InArcs implements Allocable, Iterable<Integer> {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private final HashMap<Integer, SetOfNode<Node>> arcs = new HashMap<>();

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
    protected InArcs(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a InArcs.
     * The object is managed by the allocator.
     * @return A fresh InArcs
     */
    public static InArcs create(){
        return allocator().allocate();
    }

    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    /**
     * Add a node to the structure corresponding to the given label
     * @param label Label of the arc's value
     * @param node Node to add
     */
    public void add(int label, Node node){
        if(!this.arcs.containsKey(label)) this.arcs.put(label, Memory.SetOfNode());
        this.arcs.get(label).add(node);
    }

    /**
     * Remove the node from the structure corresponding to the given label
     * @param label Label of the arc's value
     * @param node Node to remove
     * @return true if a node was removed, false otherwise
     */
    public boolean remove(int label, Node node){
        SetOfNode<Node> nodes = this.arcs.get(label);
        if(nodes != null){
            boolean result = nodes.remove(node);
            if(result && nodes.size() == 0) {
                arcs.remove(label);
                Memory.free(nodes);
            }
            return result;
        }
        return false;
    }

    /**
     * Get the set of nodes associated with the given label
     * @param label Label of the arcs' value
     * @return the set of nodes associated with the given label
     */
    public SetOfNode<Node> get(int label){
        return arcs.get(label);
    }

    /**
     * Get all the ingoing arcs' values
     * @return all the ingoing arcs' values
     */
    public Collection<Integer> values(){
        return arcs.keySet();
    }

    /**
     * Clear all the information
     */
    public void clear(){
        for(int value : arcs.keySet()) Memory.free(arcs.get(value));
        arcs.clear();
    }

    /**
     * Get the number of different values
     * @return the number of different values
     */
    public int size(){
        return arcs.size();
    }


    /**
     * Get a random ingoing arc's value
     * @param random The random object
     * @return a random ingoing arc's value
     */
    public int randomValue(Random random){
        Object[] arr = arcs.keySet().toArray();
        return (int) arr[random.nextInt(arr.length)];
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    /**
     * Call the allocator to free this object
     */
    public void dealloc() {
        allocator().free(this);
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
        for(int value : arcs.keySet()) Memory.free(arcs.get(value));
        arcs.clear();
        dealloc();
    }

    /**
     * <b>The allocator that is in charge of the InArcs type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<InArcs> {

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
        protected InArcs[] arrayCreation(int capacity) {
            return new InArcs[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected InArcs createObject(int index) {
            return new InArcs(index);
        }
    }


    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Integer> interface

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Integer> iterator() {
        return this.arcs.keySet().iterator();
    }
}
