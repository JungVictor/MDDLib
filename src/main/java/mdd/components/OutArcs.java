package mdd.components;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.HashMap;
import java.util.Iterator;

public class OutArcs implements MemoryObject, Iterable<Integer> {

    private final HashMap<Integer, Node> arcs = new HashMap<>();
    private final MemoryPool<OutArcs> pool;
    private int ID = -1;

    public OutArcs(MemoryPool<OutArcs> pool){
        this.pool = pool;
    }

    public void add(int value, Node node){
        this.arcs.put(value, node);
    }

    public boolean remove(int value){
        return this.arcs.remove(value) != null;
    }

    public Node get(int value){
        return arcs.get(value);
    }

    public void merge(OutArcs outArcs){
        for(int value : outArcs) if(!this.arcs.containsKey(value)) this.arcs.put(value, outArcs.get(value));
    }

    public void mergeWithOverride(OutArcs outArcs){
        for(int value : outArcs) this.arcs.put(value, outArcs.get(value));
    }

    public void clear(){
        this.arcs.clear();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        this.pool.free(ID);
    }

    @Override
    public boolean isComposed() {
        return false;
    }

    @Override
    public Iterator<Integer> iterator() {
        return arcs.keySet().iterator();
    }
}
