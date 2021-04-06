package mdd.components;

import memory.MemoryObject;
import memory.MemoryPool;
import representation.MDDVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

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

    public void add(int value, Node node){
        if(!this.values.contains(value)) addValueAndSort(value);
        this.arcs.put(value, node);
    }

    public Node get(int value){
        return arcs.get(value);
    }

    public Node getByIndex(int index){
        return arcs.get(values.get(index));
    }

    public int getValue(int index){
        return values.get(index);
    }

    public ArrayList<Integer> getValues(){
        return values;
    }

    public boolean remove(int value){
        values.remove(Integer.valueOf(value));
        return this.arcs.remove(value) != null;
    }

    public boolean contains(int value){
        return arcs.containsKey(value);
    }

    public void merge(OutArcs outArcs){
        for(int value : outArcs) if(!this.arcs.containsKey(value)) this.arcs.put(value, outArcs.get(value));
    }

    public void mergeWithOverride(OutArcs outArcs){
        for(int value : outArcs) this.arcs.put(value, outArcs.get(value));
    }

    public void clear(){
        this.values.clear();
        this.arcs.clear();
    }

    public int size(){
        return arcs.size();
    }

    private void addValueAndSort(int value){
        values.add(value);
        Collections.sort(values);
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
