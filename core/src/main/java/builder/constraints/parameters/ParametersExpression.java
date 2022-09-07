package builder.constraints.parameters;

import memory.AllocatorOf;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOf;
import utils.expressions.Expression;

import java.util.HashMap;
import java.util.HashSet;

public class ParametersExpression extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private HashMap<Integer, HashSet<Expression>> expressions;
    private ArrayOfInt min, max;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Private constructor of the parameters.
     * Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator
     */
    protected ParametersExpression(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Initialisation of the parameters.
     * @param scope The set of constrained variables
     */
    protected void init(SetOf<Integer> scope, HashMap<Integer, HashSet<Expression>> expressions, ArrayOfInt min, ArrayOfInt max){
        super.setScope(scope);
        this.expressions = expressions;
        this.min = min;
        this.max = max;
    }

    /**
     * Get a ParametersAllDiff object from the allocator.
     * @param scope The set of constrained variables
     * @return a fresh ParametersAllDiff object
     */
    public static ParametersExpression create(SetOf<Integer> scope, HashMap<Integer, HashSet<Expression>> expressions, ArrayOfInt min, ArrayOfInt max){
        ParametersExpression object = allocator().allocate();
        object.init(scope, expressions, min, max);
        return object;
    }

    //**************************************//

    /**
     * Get the set of all expressions containing the ith variable
     * @param i The index of the variable
     * @return The set of all expressions containing the ith variable
     */
    public HashSet<Expression> expressions(int i){
        return expressions.get(i);
    }

    public HashMap<Integer, HashSet<Expression>> expressions(){
        return expressions;
    }

    /**
     * Get the minimal value of the ith variable
     * @param i The index of the variable
     * @return The minimal value of the ith variable
     */
    public int minValue(int i){
        return min.get(i);
    }

    /**
     * Get the maximal value of the ith variable
     * @param i The index of the variable
     * @return The maximal value of the ith variable
     */
    public int maxValue(int i){
        return max.get(i);
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the ParametersAllDiff type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersExpression> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersExpression[] arrayCreation(int capacity) {
            return new ParametersExpression[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ParametersExpression createObject(int index) {
            return new ParametersExpression(index);
        }
    }

}
