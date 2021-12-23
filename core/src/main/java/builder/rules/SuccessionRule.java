package builder.rules;

import builder.rules.operations.SuccessionRuleIntersection;
import builder.rules.operations.SuccessionRuleUnion;
import dd.AbstractNode;
import memory.Allocable;
import structures.generics.CollectionOf;
import structures.lists.ListOfInt;

/**
 * Define a succession rule when building the MDD. <br>
 * That is to say, what are the possible values for my arcs given a node and a layer.
 */
public abstract class SuccessionRule implements Allocable {

    public static final SuccessionRule
            INTERSECTION = SuccessionRuleIntersection.RULE,
            UNION = SuccessionRuleUnion.RULE;


    // Index in Memory
    private final int allocatedIndex;

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    public SuccessionRule(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Get the collection adapted to the SuccessionRule.
     * ListOfInt by default.
     * @return The collection adapted to the SuccessionRule
     */
    public CollectionOf<Integer> getCollection(){
        return ListOfInt.create();
    }

    /**
     * Get the successors of a node x according to its layer.
     * @param successors The collection adapted to stock the successors
     * @param layer The layer of the node x
     * @param x The node
     * @return The Iterable of all possible successor values
     */
    public abstract Iterable<Integer> successors(CollectionOf<Integer> successors, int layer, AbstractNode x);

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    /**
     * {@inheritDoc}
     */
    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

}
