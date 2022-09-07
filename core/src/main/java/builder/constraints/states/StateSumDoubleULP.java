package builder.constraints.states;

import memory.AllocatorOf;
import builder.constraints.parameters.ParametersSumDouble;
import structures.Signature;

// TODO : extends StateSumDouble ?
/**
 * <b>StateSumDoubleULP</b><br>
 * Represent the state of a Sum constraint using double.
 */
public strictfp class StateSumDoubleULP extends NodeState {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private double sum;
    private ParametersSumDouble constraint;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateSumDoubleULP(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersSumDouble constraint){
        this.constraint = constraint;
        this.sum = 0;
    }

    /**
     * Create a StateSumDoubleULP with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateSumDoubleULP with given parameters
     */
    public static StateSumDoubleULP create(ParametersSumDouble constraint){
        StateSumDoubleULP object = allocator().allocate();
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
        StateSumDoubleULP state = StateSumDoubleULP.create(constraint);
        if(!constraint.inScope(layer-1)) state.sum = sum;
        else state.sum = Math.nextDown(sum + constraint.mapDouble(label));
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        if(!constraint.inScope(layer-1)) return true;
        double doubleLabel = constraint.mapDouble(label);
        double minPotential = Math.nextDown(sum + doubleLabel + constraint.vMin(layer-1));
        //Revoir maxPotential
        double maxPotential = Math.nextDown(sum + doubleLabel + constraint.vMax(layer-1));


        return maxPotential >= constraint.min() && constraint.max() >= minPotential;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signature(int label, int layer, int size){
        double doubleLabel;
        if(constraint.inScope(layer-1)) doubleLabel = constraint.mapDouble(label);
        else doubleLabel = 0;

        double minPotential = Math.nextDown(sum + doubleLabel + constraint.vMin(layer-1));
        //Revoir maxPotential
        double maxPotential = Math.nextDown(sum + doubleLabel + constraint.vMax(layer-1));

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Math.floor((sum + doubleLabel) * Math.pow(10, constraint.epsilon())) + "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signature signature(int label, int layer, int size, boolean test){
        double doubleLabel = constraint.mapDouble(label);
        double minPotential = Math.nextDown(sum + doubleLabel + constraint.vMin(layer-1));
        //Revoir maxPotential
        double maxPotential = Math.nextDown(sum + doubleLabel + constraint.vMax(layer-1));

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return Signature.EMPTY;
        Signature hash = Signature.create();
        hash.add(Math.floor((sum + doubleLabel) * Math.pow(10, constraint.epsilon())));
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateSumDoubleULP stateDouble = (StateSumDoubleULP) state;
        double s = Math.nextDown(stateDouble.sum + constraint.mapDouble(label));
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
     * <b>The allocator that is in charge of the StateSumDoubleULP type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateSumDoubleULP> {

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
        protected StateSumDoubleULP[] arrayCreation(int capacity) {
            return new StateSumDoubleULP[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateSumDoubleULP createObject(int index) {
            return new StateSumDoubleULP(index);
        }
    }

}
