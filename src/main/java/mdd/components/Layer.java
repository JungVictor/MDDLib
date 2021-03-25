package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.SetOf;

import java.util.Iterator;

public class Layer implements MemoryObject, Iterable<Node> {

    private final SetOf<Node> nodes = Memory.SetOfNode();
    private final MemoryPool<Layer> pool;
    private int ID = -1;

    public Layer(MemoryPool<Layer> pool){
        this.pool = pool;
    }

    public void add(Node node){
        nodes.add(node);
    }

    public boolean contains(Node node){
        return nodes.contains(node);
    }

    public void remove(Node node){
        nodes.remove(node);
    }

    public void removeFull(Node node){
        node.remove();
        nodes.remove(node);
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
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}
