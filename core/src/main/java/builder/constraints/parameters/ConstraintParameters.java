package builder.constraints.parameters;

import memory.Allocable;
import structures.generics.SetOf;

/**
 * <b>ConstraintParameters</b> <br>
 * Contains the set of constrained variables.
 */
public abstract class ConstraintParameters implements Allocable {

    // Allocated index in the allocator
    private final int allocatedIndex;

    // Set of constrained variables
    private SetOf<Integer> scope;
    private int lastVariable;

    /**
     * Constructor. Initialise the allocated index in the allocator
     * @param allocatedIndex Allocated index in the allocator
     */
    protected ConstraintParameters(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Initialise the set of constrained variables
     * @param scope Set of constrained variables
     */
    protected void setScope(SetOf<Integer> scope){
        this.scope = scope;
        lastVariable = -1;
        if(scope != null) for(int v : scope) if(v > lastVariable) lastVariable = v;
    }

    /**
     * Check if the ith variable is constrained
     * @param i The index of the variable
     * @return True if the variable is constrained, false otherwise.
     */
    public boolean inScope(int i) {
        return scope == null || scope.contains(i);
    }

    /**
     * Return the layer of the last variable
     * @return The layer of the last variables
     */
    public int getLastVariable(){
        return lastVariable;
    }

    /**
     * Return true if there is a layer remaining in the constraint after the given one.
     * @param layer The layer considered
     * @return True if there is a layer remaining in the constraint after the given one, false otherwise.
     */
    public boolean isLayerRemaining(int layer){
        if(lastVariable == -1) return true;
        return layer >= lastVariable;
    }

    /**
     * Get the number of constrained variables
     * @return The number of constrained variables
     */
    public int numberOfVariables(){
        if(scope == null) return -1;
        return scope.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        this.scope = null;
        lastVariable = -1;
    }

}
