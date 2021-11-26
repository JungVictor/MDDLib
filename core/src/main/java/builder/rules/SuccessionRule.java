package builder.rules;

import mdd.components.Node;
import memory.Allocable;

/**
 * Define a succession rule when building the MDD. <br/>
 * That is to say, what are the possible values for my arcs given a node and a layer.
 */
public  abstract class SuccessionRule implements Allocable {

    // Index in Memory
    private final int allocatedIndex;

    public SuccessionRule(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public abstract Iterable<Integer> successors(int layer, Node x);

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

}
