package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.SetOf;

import java.util.HashMap;
import java.util.Iterator;

public class InArcs implements MemoryObject, Iterable<Integer> {

    private final HashMap<Integer, SetOf<Node>> arcs = new HashMap<>();
    private final MemoryPool<InArcs> pool;
    private int ID;

    public InArcs(MemoryPool<InArcs> pool){
        this.pool = pool;
    }

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
                nodes.free();
            }
            return result;
        }
        return false;
    }

    public SetOf<Node> get(int value){
        return arcs.get(value);
    }

    public void clear(){
        for(int value : arcs.keySet()) arcs.get(value).free();
        arcs.clear();
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
        return true;
    }

    @Override
    public Iterator<Integer> iterator() {
        return this.arcs.keySet().iterator();
    }
}
