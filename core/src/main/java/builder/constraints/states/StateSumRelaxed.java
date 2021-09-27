package builder.constraints.states;

import builder.constraints.parameters.ParametersSumRelaxed;
import memory.AllocatorOf;
import structures.Signature;

public strictfp class StateSumRelaxed extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private long sum;
    private ParametersSumRelaxed constraint;


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

    private StateSumRelaxed(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersSumRelaxed constraint){
        this.constraint = constraint;
        this.sum = 0;
    }

    public static StateSumRelaxed create(ParametersSumRelaxed constraint){
        StateSumRelaxed object = allocator().allocate();
        object.init(constraint);
        return object;
    }

    public String toString(){
        return Double.toString(sum);
    }

    //**************************************//

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateSumRelaxed state = StateSumRelaxed.create(constraint);
        state.sum = sum + constraint.map(label);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        long value = constraint.map(label);
        long minPotential = sum + value + constraint.vMin(layer-1);
        long maxPotential = sum + value + constraint.vMax(layer-1);


        if(maxPotential < constraint.min() || constraint.max() < minPotential) return false;
        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return true;
        return sum + value <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        long value = constraint.map(label);
        long minPotential = sum + value + constraint.vMin(layer-1);
        long maxPotential = sum + value + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return (long) ((sum + value) / Math.pow(10, constraint.precision() - constraint.epsilon())) + "";
    }

    @Override
    public Signature hash(int label, int layer, int size, boolean test){
        long value = constraint.map(label);
        long minPotential = sum + value + constraint.vMin(layer-1);
        long maxPotential = sum + value + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return Signature.EMPTY;
        Signature hash = Signature.create();
        hash.add(Math.floor(((sum + value) / Math.pow(10, constraint.precision() - constraint.epsilon()))));
        return hash;
    }

    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateSumRelaxed stateSumRelaxed = (StateSumRelaxed) state;
        long s = stateSumRelaxed.sum + constraint.map(label);
        if(s < sum) sum = s;
        return null;
    }

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

        @Override
        protected StateSumRelaxed[] arrayCreation(int capacity) {
            return new StateSumRelaxed[capacity];
        }

        @Override
        protected StateSumRelaxed createObject(int index) {
            return new StateSumRelaxed(index);
        }
    }

}
