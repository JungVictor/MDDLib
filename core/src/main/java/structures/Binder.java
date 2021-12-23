package structures;

import dd.AbstractNode;
import memory.*;
import structures.arrays.ArrayOfAbstractNode;
import structures.generics.MapOf;

/**
 * <b>This class is used to represent the binding between multiple nodes and an equivalent node</b> <br>
 * This class is used when performing operations between MDD to bind multiple nodes, for instance x1 and x2 to a node x
 * that would be the result of the combination of the two nodes.<br>
 * This is useful when you do not want to create many nodes that represent the same couple.
 */
public class Binder implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Allocated index
    private final int allocatedIndex;
    //

    private final MapOf<AbstractNode, Binder> reduction = new MapOf<>(null);
    private AbstractNode leaf;
    private int depth;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private Binder(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create an Binder.
     * The object is managed by the allocator.
     * @return A Binder.
     */
    public static Binder create(){
        return allocator().allocate();
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
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
    public Binder path(ArrayOfAbstractNode nodes){
        if(isLeaf(nodes.length)) return this;
        else if(reduction.get(nodes.get(depth)) != null) return reduction.get(nodes.get(depth)).path(nodes);
        else {
            Binder next = Binder.create();
            next.depth = depth + 1;
            reduction.put(nodes.get(depth), next);
            return next.path(nodes);
        }
    }

    /**
     * Set the leaf of the LayerReduction
     * @param leaf Node that will be the leaf
     */
    public void setLeaf(AbstractNode leaf){
        this.leaf = leaf;
    }

    /**
     * Get the leaf
     * @return (Node) The leaf
     */
    public AbstractNode getLeaf(){
        return leaf;
    }

    /**
     * Clear all information in the LayerReduction, recursively.
     */
    public void clear(){
        for(Binder l : reduction.values()) Memory.free(l);
        this.reduction.clear();
        this.leaf = null;
        this.depth = 0;
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
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public void free() {
        for(Binder l : reduction.values()) Memory.free(l);
        this.reduction.clear();
        this.leaf = null;
        this.depth = 0;
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the Binder type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<Binder> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected Binder[] arrayCreation(int capacity) {
            return new Binder[capacity];
        }

        @Override
        protected Binder createObject(int index) {
            return new Binder(index);
        }
    }
}
