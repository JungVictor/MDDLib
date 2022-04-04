package builder.constraints.states;

import builder.constraints.parameters.ParametersSumRelaxed;
import memory.AllocatorOf;
import structures.Signature;

/**
 * <b>StateSumRelaxed</b><br>
 * Represent the state of a relaxed Sum constraint using long.
 */
public strictfp class StateSumRelaxed extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private long sum;
    private ParametersSumRelaxed constraint;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateSumRelaxed(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersSumRelaxed constraint){
        this.constraint = constraint;
        this.sum = 0;
    }

    /**
     * Create a StateSumDouble with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateSumDouble with given parameters
     */
    public static StateSumRelaxed create(ParametersSumRelaxed constraint){
        StateSumRelaxed object = allocator().allocate();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return Double.toString(sum);
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
        StateSumRelaxed state = StateSumRelaxed.create(constraint);
        state.sum = sum;
        if(constraint.isVariable(layer-1)) state.sum += constraint.map(label);
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        if(!constraint.isVariable(layer-1)) return true;

        long value = constraint.map(label);
        long minPotential = sum + value + constraint.vMin(layer-1);
        long maxPotential = sum + value + constraint.vMax(layer-1);


        return maxPotential >= constraint.min() && constraint.max() >= minPotential;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signature(int label, int layer, int size){
        long value = 0;
        if(constraint.isVariable(layer-1)) value = constraint.map(label);
        long minPotential = sum + value + constraint.vMin(layer-1);
        long maxPotential = sum + value + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return (long) ((sum + value) / Math.pow(10, constraint.precision() - constraint.epsilon())) + "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signature signature(int label, int layer, int size, boolean test){
        long value = constraint.map(label);
        long minPotential = sum + value + constraint.vMin(layer-1);
        long maxPotential = sum + value + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return Signature.EMPTY;
        Signature hash = Signature.create();
        hash.add(Math.floor(((sum + value) / Math.pow(10, constraint.precision() - constraint.epsilon()))));
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateSumRelaxed stateSumRelaxed = (StateSumRelaxed) state;
        long s = stateSumRelaxed.sum + constraint.map(label);
        if(s < sum) sum = s;
        return null;
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
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateSumDouble type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateSumRelaxed> {

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
        protected StateSumRelaxed[] arrayCreation(int capacity) {
            return new StateSumRelaxed[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateSumRelaxed createObject(int index) {
            return new StateSumRelaxed(index);
        }
    }

}
