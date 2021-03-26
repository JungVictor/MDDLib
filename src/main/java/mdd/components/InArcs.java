package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.SetOf;

import java.util.HashMap;
import java.util.Iterator;

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

    public void add(int value, Node node){
        if(!this.arcs.containsKey(value)) this.arcs.put(value, Memory.SetOfNode());
        this.arcs.get(value).add(node);
    }

    public boolean remove(int value, Node node){
        SetOf<Node> nodes = this.arcs.get(value);
        if(nodes != null){
            boolean result = nodes.remove(node);
            if(result && nodes.size() == 0) {
                arcs.remove(nodes);
                Memory.free(nodes);
            }
            return result;
        }
        return false;
    }

    public SetOf<Node> get(int value){
        return arcs.get(value);
    }

    public void clear(){
        for(int value : arcs.keySet()) Memory.free(arcs.get(value));
        arcs.clear();
    }

    public int size(){
        return arcs.size();
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
        for(SetOf<Node> nodes : arcs.values()) Memory.free(nodes);
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
