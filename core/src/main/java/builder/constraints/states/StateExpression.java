package builder.constraints.states;

import builder.constraints.parameters.ParametersExpression;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.MapOf;
import utils.expressions.Expression;

public class StateExpression extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private MapOf<Integer, Integer> values;
    private MapOf<Integer, Integer> nExpr; // optimisation
    private ParametersExpression constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateExpression(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersExpression constraint){
        this.values = Memory.MapOfIntegerInteger();
        this.nExpr = Memory.MapOfIntegerInteger();
        for(int layer : constraint.expressions().keySet()) {
            this.nExpr.put(layer, constraint.expressions(layer).size());
        }
        this.constraint = constraint;
    }

    /**
     * Create a StateAllDiff with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateAllDiff with given parameters
     */
    public static StateExpression create(ParametersExpression constraint){
        StateExpression object = allocator().allocate();
        object.init(constraint);
        return object;
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }


    //**************************************//
    //           STATE FUNCTIONS            //
    //**************************************//
    // Implementation of NodeState functions

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeState createState(int label, int layer, int size) {
        StateExpression state = StateExpression.create(constraint);
        state.nExpr.clear();
        for(int key : values) state.values.put(key, values.get(key));
        for(int key : nExpr) state.nExpr.put(key, nExpr.get(key));
        if(constraint.inScope(layer-1)) {
            state.values.put(layer - 1, label);
            for(Expression expr : constraint.expressions(layer - 1))
                if(expr.lastVariable() <= layer - 1) state.removeExpression(state.values, state.nExpr, expr);
        }

        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        if(!constraint.inScope(layer-1)) return true;

        MapOf<Integer, Integer> tmp = Memory.MapOfIntegerInteger();
        tmp.copy(values);

        tmp.put(layer-1, label);

        for(Expression expr : constraint.expressions(layer - 1)) {
            // Must complete the values for evaluation
            if(expr.lastVariable() > layer - 1) {
                // TODO : Maximise / minimise to see if the solution is always TRUE
            } else {
                if(!expr.eval(tmp)) {
                    Memory.free(tmp);
                    return false;
                }
            }
        }
        Memory.free(tmp);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signature(int label, int layer, int size) {
        if(!constraint.inScope(layer - 1)) return values.toString();
        StringBuilder builder = new StringBuilder();

        MapOf<Integer, Integer> tmp = Memory.MapOfIntegerInteger(), tmp_exp = Memory.MapOfIntegerInteger();
        tmp.copy(values); tmp_exp.copy(nExpr);

        for(Expression expr : constraint.expressions(layer - 1))
            if(expr.lastVariable() <= layer - 1) removeExpression(tmp, tmp_exp, expr);

        for(int value : tmp){
            builder.append(value);
            builder.append("=");
            builder.append(tmp.get(value));
            builder.append(" ");
        }
        if(tmp_exp.contains(layer - 1)) {
            builder.append(layer - 1);
            builder.append("=");
            builder.append(label);
        }
        Memory.free(tmp);
        Memory.free(tmp_exp);
        return builder.toString();
    }

    /**
     * Remove an expression from the set of expressions.
     * If there are no expressions bound to a layer, remove the layer from the state
     * @param expr The expression to remove
     */
    private void removeExpression(MapOf<Integer, Integer> values, MapOf<Integer, Integer> nExpr, Expression expr){
        for(int layer : expr.getBinding().values()) {
            int size = nExpr.get(layer) - 1;
            if(size == 0) {
                values.remove(layer);
                nExpr.remove(layer);
            } else nExpr.put(layer, size);
        }
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        Memory.free(values);
        Memory.free(nExpr);
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateExpression type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateExpression> {

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
        protected StateExpression[] arrayCreation(int capacity) {
            return new StateExpression[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateExpression createObject(int index) {
            return new StateExpression(index);
        }
    }
}
