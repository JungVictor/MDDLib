package builder.rules;

import builder.rules.operations.SuccessionRuleIntersection;
import builder.rules.operations.SuccessionRuleUnion;
import mdd.components.Node;
import memory.Allocable;
import structures.lists.ListOfInt;

/**
 * Define a succession rule when building the MDD. <br/>
 * That is to say, what are the possible values for my arcs given a node and a layer.
 */
public  abstract class SuccessionRule implements Allocable {

    public static final SuccessionRule
            INTERSECTION = new SuccessionRuleIntersection(-1),
            UNION = new SuccessionRuleUnion(-1);


    // Index in Memory
    private final int allocatedIndex;

    public SuccessionRule(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public abstract Iterable<Integer> successors(ListOfInt successors, int layer, Node x);

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

}
