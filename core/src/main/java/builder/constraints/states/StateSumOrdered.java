package builder.constraints.states;

import builder.constraints.parameters.ParametersSumOrdered;
import memory.AllocatorOf;

/**
 * <b>StateSumOrdered</b><br>
 * Represent the state of a Sum constraint with ordering.
 */
public class StateSumOrdered extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    protected int sum;
    protected int lowestValue;
    protected ParametersSumOrdered constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateSumOrdered(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersSumOrdered constraint){
        this.constraint = constraint;
        this.sum = 0;
        this.lowestValue = Integer.MAX_VALUE;
    }

    /**
     * Create a StateSum with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateSum with given parameters
     */
    public static StateSumOrdered create(ParametersSumOrdered constraint){
        StateSumOrdered object = allocator().allocate();
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
    public String toString(){
        return Integer.toString(sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeState createState(int label, int layer, int size) {
        label = constraint.value(label);
        StateSumOrdered state = StateSumOrdered.create(constraint);
        state.sum = sum;
        if(constraint.inScope(layer-1)) state.sum += label;
        if(!constraint.isJump(layer)) state.lowestValue = label;
        else state.lowestValue = Integer.MAX_VALUE;
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        if(!constraint.inScope(layer-1)) return true;
        label = constraint.value(label);
        int minPotential = sum + label + constraint.vMin(layer-1);
        int maxPotential = sum + label + constraint.vMax(layer-1);

        boolean order = constraint.isJump(layer) || lowestValue >= label;
        return maxPotential >= constraint.min() && constraint.max() >= minPotential && order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signature(int label, int layer, int size){
        if(!constraint.inScope(layer-1)) label = 0;
        label = constraint.value(label);
        int minPotential = sum + label + constraint.vMin(layer-1);
        int maxPotential = sum + label + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) {
            if(constraint.isJump(layer)) return "";
            return Integer.toString(label);
        }
        if(constraint.isJump(layer)) return Integer.toString(sum + label);
        return (sum + label) + " " + label;
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
        this.constraint = null;
        this.lowestValue = Integer.MAX_VALUE;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateSumOrdered type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateSumOrdered> {

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
        protected StateSumOrdered[] arrayCreation(int capacity) {
            return new StateSumOrdered[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateSumOrdered createObject(int index) {
            return new StateSumOrdered(index);
        }
    }

}
