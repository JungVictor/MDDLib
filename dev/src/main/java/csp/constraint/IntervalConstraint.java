package csp.constraint;

import csp.structures.arrays.ArrayOfIntervalVariable;
import csp.IntervalVariable;
import csp.structures.lists.ListOfIntervalVariable;
import memory.Allocable;

public abstract class IntervalConstraint implements Allocable {

    private ArrayOfIntervalVariable variables; //The array of IntervalVariable concerned by the IntervalConstraint
    private int index; //The current number of IntervalVariable added to the array variables.

    //**************************************//
    //       ALLOCATION AND CREATION        //
    //**************************************//

    private final int allocatedIndex;

    /**
     * Constructor. Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator.
     */
    protected IntervalConstraint(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Initialise the IntervalConstraint.
     */
    protected void init(){
        this.index = 0;
    }

    //**************************************//
    //               METHODS                //
    //**************************************//

    public IntervalVariable getVariable(int i){
        return variables.get(i);
    }

    /**
     * @return The number of IntervalVariable concerned by the IntervalConstraint.
     */
    public int numberOfVariables(){
        return variables.length();
    }

    /**
     * Apply the IntervalConstraint to filter the intervals of the concerned IntervalVariable objects.
     * @return An array of boolean indicating which IntervalVariable objects get their interval changed by the filtering.
     */
    public abstract ListOfIntervalVariable apply();

    /**
     * Add properly an IntervalVariable to the array variables.<br>
     * <b>/!\ When an IntervalVariable is added, it automatically adds
     * the IntervalConstraint in its list of IntervalConstraint.</b><br>
     * <b>/!\ A same IntervalVariable might not be added more than one time !</b>
     * @param variable
     */
    protected void addVariable(IntervalVariable variable){
        variables.set(index, variable);
        index ++;
        variable.addConstraint(this);
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    /**
     * Prepare the object by allocating its attributes.
     */
    protected void prepare(int n){
        variables = ArrayOfIntervalVariable.create(n);
    }

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free(){
        this.variables = null;
        this.index = 0;
    }
}
