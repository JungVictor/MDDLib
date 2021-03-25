package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.ArrayOf;

public class Node implements MemoryObject {

    private final OutArcs children = Memory.OutArcs();
    private final InArcs parents = Memory.InArcs();
    private final ArrayOf<Node> associations = Memory.ArrayOfNode(2);
    private final MemoryPool<Node> pool;
    private int ID = -1;

    public Node(MemoryPool<Node> pool) {
        this.pool = pool;
    }

    public void associates(ArrayOf<Node> associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length(); i++) this.associations.set(i, associations.get(i));
    }

    public void remove(){
        for(int value : children) children.get(value).removeParent(value, this);
        for(int value : parents) for(Node node : parents.get(value)) node.removeChild(value);
    }

    ////////////////////////////////////////////////

    public void addChild(int value, Node child){
        this.children.add(value, child);
    }

    public void removeChild(int value){
        this.children.remove(value);
    }

    public void addParent(int value, Node parent){
        this.parents.add(value, parent);
    }

    public void removeParent(int value, Node parent){
        this.parents.remove(value, parent);
    }


    ////////////////////////////////////////////////
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
