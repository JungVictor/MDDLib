package dd.mdd.nondeterministic.components;

import dd.interfaces.INode;
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
public class NonDeterministicArcs implements Allocable, Iterable<Integer> {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private final HashMap<Integer, SetOfNode<INode>> arcs = new HashMap<>();

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
    protected NonDeterministicArcs(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a InArcs.
     * The object is managed by the allocator.
     * @return A fresh InArcs
     */
    public static NonDeterministicArcs create(){
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
    public void add(int label, NDNode node){
        if(!this.arcs.containsKey(label)) this.arcs.put(label, Memory.SetOfINode());
        this.arcs.get(label).add(node);
    }

    /**
     * Remove the node from the structure corresponding to the given label
     * @param label Label of the arc's value
     * @param node Node to remove
     * @return true if a node was removed, false otherwise
     */
    public boolean remove(int label, NDNode node){
        SetOfNode<INode> nodes = this.arcs.get(label);
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
     * Remove all nodes corresponding to the given label
     * @param label Label to remove
     */
    public void remove(int label){
        if(this.arcs.get(label) != null) this.arcs.get(label).clear();
    }

    /**
     * Get the set of nodes associated with the given label
     * @param label Label of the arcs' value
     * @return the set of nodes associated with the given label
     */
    public SetOfNode<INode> get(int label){
        return arcs.get(label);
    }

    /**
     * Return the first Node corresponding to the given label.
     * @param label Label of the node
     * @return The first node corresponding to the given label.
     */
    public INode getFirst(int label){
        for(INode node : arcs.get(label)) return node;
        return null;
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
    static final class Allocator extends AllocatorOf<NonDeterministicArcs> {

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
        protected NonDeterministicArcs[] arrayCreation(int capacity) {
            return new NonDeterministicArcs[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected NonDeterministicArcs createObject(int index) {
            return new NonDeterministicArcs(index);
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
