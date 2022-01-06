package builder.constraints.states;

import memory.Allocable;
import structures.Signature;

/**
 * <b>NodeState</b><br>
 * Abstract class representing the state of a constraint.
 */
public abstract class NodeState implements Allocable {

    // Index in Memory
    private final int allocatedIndex;

    /**
     * Constructor of NodeState.
     * Initialise the allocated index.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected NodeState(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }


    //**************************************//
    //           STATE FUNCTIONS            //
    //**************************************//

    /**
     * Transition function.<br>
     * Create a new state according to the label of the arc, the layer of the <b>PARENT</b> node
     * and the size of the DD.
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @return The new state according to the transition
     */
    public abstract NodeState createState(int label, int layer, int size);

    /**
     * Validity check.<br>
     * Check if the new state according to the transition function is a valid state of the constraint
     * <b>prior to its creation</b>.
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @return True if the new state is valid, false otherwise
     */
    public abstract boolean isValid(int label, int layer, int size);

    /**
     * Merge function.<br>
     * This function is called when the constraint is relaxed.<br>
     * When two nodes have the same signature (hash), they are merged : however, when the constraint
     * is relaxed, the states might not be the same. Therefore, we need to define a merge.
     * @param state The state to merge this state with
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @return The result of the merging of the states
     */
    public NodeState merge(NodeState state, int label, int layer, int size) {
        return null;
    }

    /**
     * Signature function.<br>
     * Two nodes are merged when their signature are the same.
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @return The signature of the state
     */
    public abstract String signature(int label, int layer, int size);


    /**
     * Signature function <b>(TESTING)</b>.<br>
     * Two nodes are merged when their signature are the same.
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @param test ---
     * @return The signature of the state
     */
    public Signature signature(int label, int layer, int size, boolean test){
        return null;
    }

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
