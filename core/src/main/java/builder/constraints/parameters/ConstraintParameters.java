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
    private SetOf<Integer> variables;

    /**
     * Constructor. Initialise the allocated index in the allocator
     * @param allocatedIndex Allocated index in the allocator
     */
    protected ConstraintParameters(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Initialise the set of constrained variables
     * @param variables Set of constrained variables
     */
    protected void setVariables(SetOf<Integer> variables){
        this.variables = variables;
    }

    /**
     * Check if the ith variable is constrained
     * @param i The index of the variable
     * @return True if the variable is constrained, false otherwise.
     */
    public boolean isVariable(int i) {
        return variables == null || variables.contains(i);
    }

    /**
     * Get the number of constrained variables
     * @return The number of constrained variables
     */
    public int numberOfVariables(){
        if(variables == null) return -1;
        return variables.size();
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
        this.variables = null;
    }

}
