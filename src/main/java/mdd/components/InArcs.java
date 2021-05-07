package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.SetOf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * <b>This class is used to represent the binding between a node and its parents.</b> <br>
 * The in-going arcs (InArcs) is represented using a Map, binding the labels of the arcs to the parents node.
 * As a node can have multiple parents having the same label, the map is actually binding an integer to a set of nodes.
 */
public class InArcs implements MemoryObject, Iterable<Integer> {

    // MemoryObject variables
    private final MemoryPool<InArcs> pool;
    private int ID = -1;
    //

    private final HashMap<Integer, SetOf<Node>> arcs = new HashMap<>();

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public InArcs(MemoryPool<InArcs> pool){
        this.pool = pool;
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
        SetOf<Node> nodes = this.arcs.get(label);
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
    public SetOf<Node> get(int label){
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
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        arcs.clear();
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        for(int value : arcs.keySet()) Memory.free(arcs.get(value));
        arcs.clear();
        this.pool.free(this, ID);
    }



    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Integer> interface

    @Override
    public Iterator<Integer> iterator() {
        return this.arcs.keySet().iterator();
    }
}
