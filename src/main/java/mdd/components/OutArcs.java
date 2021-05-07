package mdd.components;

import memory.MemoryObject;
import memory.MemoryPool;
import representation.MDDVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <b>This class is used to represent the binding between a node and its children.</b> <br>
 * The out-going arcs (OutArcs) is represented using a simple map binding integer to node,
 * as the MDD is determinist by default (i.e. can only have one children with a specific label).
 */
public class OutArcs implements MemoryObject, Iterable<Integer> {


    // MemoryObject variables
    private final MemoryPool<OutArcs> pool;
    private int ID = -1;
    //


    private final HashMap<Integer, Node> arcs = new HashMap<>();
    private final ArrayList<Integer> values = new ArrayList<>();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public OutArcs(MemoryPool<OutArcs> pool){
        this.pool = pool;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * Accept a MDDVisitor.
     * @param visitor
     */
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
    }


    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    /**
     * Associate a node with the given label
     * @param label Label of the arc
     * @param node Node to associate with the given label
     */
    public void add(int label, Node node){
        if(!this.values.contains(label)) addValueAndSort(label);
        this.arcs.put(label, node);
    }

    /**
     * Get the node associated with the given label
     * @param label Label of the arc
     * @return the node associated with the given label
     */
    public Node get(int label){
        return arcs.get(label);
    }

    /**
     * Get the node associated with the label corresponding to the given index
     * @param index Index of the label of the arc
     * @return the node associated with the label corresponding to the given index
     */
    public Node getByIndex(int index){
        return arcs.get(values.get(index));
    }

    /**
     * Get the value corresponding to the given index
     * @param index index of the value
     * @return the value corresponding to the given index
     */
    public int getValue(int index){
        return values.get(index);
    }

    /**
     * Get all the values that are associated with a node
     * @return Array containing all the values that are associated with a node
     */
    public ArrayList<Integer> getValues(){
        return values;
    }

    /**
     * Remove the node associated with the given label
     * @param label Label of the arc
     * @return true if an association is removed, false otherwise
     */
    public boolean remove(int label){
        values.remove(Integer.valueOf(label));
        return this.arcs.remove(label) != null;
    }

    /**
     * Check if the given value is associated with a node
     * @param value The value of the label
     * @return true if the given value is associated with a node, false otherwise
     */
    public boolean contains(int value){
        return arcs.containsKey(value);
    }

    /**
     * Merge the two set of associations.
     * That is to say, copy all associations that DO NOT exist in this set.
     * @param outArcs The sets of outgoing arcs.
     */
    public void merge(OutArcs outArcs){
        for(int value : outArcs) if(!this.arcs.containsKey(value)) this.arcs.put(value, outArcs.get(value));
    }


    /**
     * Merge the two set of associations with override.
     * That is to say, copy all associations and override in case of conflicts.
     * @param outArcs The sets of outgoing arcs.
     */
    public void mergeWithOverride(OutArcs outArcs){
        for(int value : outArcs) this.arcs.put(value, outArcs.get(value));
    }

    /**
     * Clear all informations
     */
    public void clear(){
        this.values.clear();
        this.arcs.clear();
    }

    /**
     * Get the number of arcs in the set
     * @return the number of arcs in the set
     */
    public int size(){
        return arcs.size();
    }

    /**
     * Add a value to the set of values and sort the set in increasing order.
     * @param value Value to add
     */
    private void addValueAndSort(int value){
        values.add(value);
        Collections.sort(values);
    }

    /**
     * Check if the given node is associated with a value
     * @param child Node to check
     * @return true if the given node is associated with a value, false otherwise
     */
    public boolean contains(Node child){
        for(Node node : arcs.values()) if(node == child) return true;
        return false;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        arcs.clear();
        values.clear();
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        arcs.clear();
        values.clear();
        this.pool.free(this, ID);
    }


    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Integer> interface
    @Override
    public Iterator<Integer> iterator() {
        return values.iterator();
    }
}
