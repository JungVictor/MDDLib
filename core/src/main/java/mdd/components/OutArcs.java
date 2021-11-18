package mdd.components;

import memory.Allocable;
import memory.AllocatorOf;
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
public class OutArcs implements Allocable, Iterable<Integer> {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private boolean isSorted = false;
    private final HashMap<Integer, Node> arcs = new HashMap<>();
    private final ArrayList<Integer> values = new ArrayList<>();

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static OutArcs create(){
        return allocator().allocate();
    }

    protected OutArcs(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
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
        if(!this.values.contains(label)) addValue(label);
        this.arcs.put(label, node);
    }

    public boolean containsLabel(int label){
        return this.values.contains(label);
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
        isSorted = false;
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
        int bottom = 0;
        int up = values.size();
        int pos = 0;
        while (bottom <= up) {
            pos = bottom + (up - bottom) / 2;
            if(values.get(pos) > value) up = pos - 1;
            else if(values.get(pos) < value) bottom = pos + 1;
            else break;
        }
        for(int i = pos; i < values.size() - 1; i++) values.set(i+1, values.get(i));
        values.set(pos, value);
    }

    public void addValue(int label){
        if(isSorted) addValueAndSort(label);
        else values.add(label);
    }

    public void sort(){
        if(isSorted) return;
        Collections.sort(values);
        isSorted = true;
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

    protected void dealloc(){
        allocator().free(this);
    }

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free() {
        arcs.clear();
        values.clear();
        isSorted = false;
        dealloc();
    }

    /**
     * <b>The allocator that is in charge of the OutArcs type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<OutArcs> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected OutArcs[] arrayCreation(int capacity) {
            return new OutArcs[capacity];
        }

        @Override
        protected OutArcs createObject(int index) {
            return new OutArcs(index);
        }
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
