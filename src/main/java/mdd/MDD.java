package mdd;

import mdd.components.Layer;
import mdd.components.Node;
import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;

public class MDD implements MemoryObject {

    private final MemoryPool<MDD> pool;
    private int ID = -1;

    // Root node
    private final Node root = Memory.Node();

    // Layers
    private Layer[] L;
    private int size;


    public MDD(MemoryPool<MDD> pool){
        this.pool = pool;
    }

    /////////////////////////////////////////////////////

    public void addNode(Node node, int layer){
        L[layer].add(node);
    }

    public void addEdge(Node source, int value, Node destination){
        source.addChild(value, destination);
    }

    public void addNodeAndEdge(Node source, int value, Node destination, int layer){
        addEdge(source, value, destination);
        addNode(destination, layer);
    }

    ///////////////////////////////////////////////

    public int getSize(){
        return size;
    }
    public void setSize(int size){
        if(this.L.length < size) this.L = new Layer[size];
        this.size = size;
    }
    private void resize(int size){
        Layer[] L = new Layer[size];
        System.arraycopy(this.L, 0, L, 0, this.L.length);
        this.L = L;
        setSize(size);
    }

    ///////////////////////////////////////////////
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
}
