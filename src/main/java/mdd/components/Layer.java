package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import representation.MDDVisitor;
import structures.generics.SetOf;

import java.util.Iterator;

public class Layer implements MemoryObject, Iterable<Node> {

    // MemoryObject variables
    private final MemoryPool<Layer> pool;
    private int ID = -1;
    //

    private SetOf<Node> nodes;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public Layer(MemoryPool<Layer> pool){
        this.pool = pool;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
        for(Node node : nodes) node.accept(visitor);
    }


    //**************************************//
    //           LAYER MANAGEMENT           //
    //**************************************//

    public void add(Node node){
        nodes.add(node);
    }

    public void remove(Node node){
        nodes.remove(node);
    }

    public void removeAndFree(Node node){
        node.remove();
        nodes.remove(node);
        Memory.free(node);
    }

    public void clear(){
        nodes.clear();
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    public int size(){
        return nodes.size();
    }
    public boolean contains(Node node){
        return nodes.contains(node);
    }
    public SetOf<Node> getNodes(){
        return nodes;
    }
    public Node getNode(){
        for(Node node : nodes) return node;
        return null;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        nodes = Memory.SetOfNode();
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        Memory.free(nodes);
        this.pool.free(this, ID);
    }

    public void freeAllNodes(){
        for(Node node : nodes) {
            node.remove();
            Memory.free(node);
        }
        nodes.clear();
    }

    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Node> interface
    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}
