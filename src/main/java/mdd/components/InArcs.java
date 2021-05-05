package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.SetOf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

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

    public void add(int label, Node node){
        if(!this.arcs.containsKey(label)) this.arcs.put(label, Memory.SetOfNode());
        this.arcs.get(label).add(node);
    }

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

    public SetOf<Node> get(int label){
        return arcs.get(label);
    }

    public Collection<Integer> values(){
        return arcs.keySet();
    }

    public int randomValue(Random random){
        Object[] arr = arcs.keySet().toArray();
        return (int) arr[random.nextInt(arr.length)];
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
