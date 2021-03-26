package structures;

import mdd.components.Node;
import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.ArrayOf;
import structures.generics.MapOf;

public class Binder implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<Binder> pool;
    private int ID;
    //

    private final MapOf<Node, Binder> reduction = new MapOf<>(null);
    private Node leaf;
    private int depth;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    // init

    public Binder(MemoryPool<Binder> pool){
        this.pool = pool;
    }


    //**************************************//
    //             OPERATIONS               //
    //**************************************//
    // add              || setLeaf
    // hasLeaf          || getLeaf
    // clear            || -isLeaf

    /**
     * Add an array of nodes to the structure
     * @param nodes Nodes to add to the structure
     * @return The last Binder added to the general structure.
     */
    public Binder path(ArrayOf<Node> nodes){
        if(isLeaf(nodes.length)) return this;
        else if(reduction.get(nodes.get(depth)) != null) return reduction.get(nodes.get(depth)).path(nodes);
        else {
            Binder next = Memory.Binder();
            next.depth = depth + 1;
            reduction.put(nodes.get(depth), next);
            return next.path(nodes);
        }
    }

    /**
     * Set the leaf of the LayerReduction
     * @param leaf Node that will be the leaf
     */
    public void setLeaf(Node leaf){
        this.leaf = leaf;
    }

    /**
     * Get the leaf
     * @return (Node) The leaf
     */
    public Node getLeaf(){
        return leaf;
    }

    /**
     * Clear all information in the LayerReduction, recursively.
     */
    public void clear(){
        for(Binder l : reduction.values()) Memory.free(l);
        prepare();
    }

    /**
     * Check if the LayerReduction is the last one in the structure
     * @param size Size of the structure
     * @return true if this is the final level, false otherwise
     */
    private boolean isLeaf(int size){
        return depth == size;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        for(Binder l : reduction.values()) Memory.free(l);
        prepare();
        pool.free(this, ID);
    }

    @Override
    public void prepare(){
        this.reduction.clear();
        this.leaf = null;
        this.depth = 0;
    }
}
