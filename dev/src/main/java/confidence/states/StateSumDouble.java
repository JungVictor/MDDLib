package confidence.states;

import builder.constraints.states.NodeState;
import memory.AllocatorOf;
import memory.MemoryPool;
import confidence.MyMemory;
import confidence.parameters.ParametersSumDouble;
import structures.Signature;

public strictfp class StateSumDouble extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private double sum;
    private ParametersSumDouble constraint;


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

    private StateSumDouble(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersSumDouble constraint){
        this.constraint = constraint;
        this.sum = 0;
    }

    public static StateSumDouble create(ParametersSumDouble constraint){
        StateSumDouble object = allocator().allocate();
        object.init(constraint);
        return object;
    }

    public String toString(){
        return Double.toString(sum);
    }

    //**************************************//

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateSumDouble state = StateSumDouble.create(constraint);
        state.sum = sum + constraint.mapDouble(label);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        double doubleLabel = constraint.mapDouble(label);
        double minPotential = sum + doubleLabel + constraint.vMin(layer-1);
        double maxPotential = sum + doubleLabel + constraint.vMax(layer-1);


        if(maxPotential < constraint.min() || constraint.max() < minPotential) return false;
        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return true;
        return sum + doubleLabel <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        double doubleLabel = constraint.mapDouble(label);
        double minPotential = sum + doubleLabel + constraint.vMin(layer-1);
        double maxPotential = sum + doubleLabel + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Math.floor((sum + doubleLabel) * Math.pow(10, constraint.epsilon())) + "";
    }

    @Override
    public Signature hash(int label, int layer, int size, boolean test){
        double doubleLabel = constraint.mapDouble(label);
        double minPotential = sum + doubleLabel + constraint.vMin(layer-1);
        double maxPotential = sum + doubleLabel + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return Signature.EMPTY;
        Signature hash = Signature.create();
        hash.add(Math.floor((sum + doubleLabel) * Math.pow(10, constraint.epsilon())));
        return hash;
    }

    @Override
    public NodeState merge(NodeState state, int label, int layer, int size){
        StateSumDouble stateDouble = (StateSumDouble) state;
        double s = stateDouble.sum + constraint.mapDouble(label);
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
    static final class Allocator extends AllocatorOf<StateSumDouble> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateSumDouble[] arrayCreation(int capacity) {
            return new StateSumDouble[capacity];
        }

        @Override
        protected StateSumDouble createObject(int index) {
            return new StateSumDouble(index);
        }
    }

}
