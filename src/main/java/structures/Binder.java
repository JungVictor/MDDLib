package structures;

import mdd.components.Node;
import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;

public class Binder implements MemoryObject {

    private final MemoryPool<Binder> pool;
    private int ID = -1;

    private int depth;
    private MapOf<Node, Binder> next;
    private Node leaf;

    public Binder(MemoryPool<Binder> pool){
        this.pool = pool;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Binder path(ArrayOf<Node> nodes){
        if(this.depth >= nodes.length) return this;
        Binder next = this.next.get(nodes.get(depth));
        if(next == null) {
            next = Memory.Binder();
            next.setDepth(depth+1);
            this.next.put(nodes.get(depth), next);
        }
        return next.path(nodes);
    }

    public Node getLeaf(){
        return leaf;
    }

    public void setLeaf(Node leaf){
        this.leaf = leaf;
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
}
