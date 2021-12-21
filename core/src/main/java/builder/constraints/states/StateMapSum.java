package builder.constraints.states;

import builder.constraints.parameters.ParametersMapSum;
import memory.AllocatorOf;

public class StateMapSum extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private int sum;
    private ParametersMapSum constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public StateMapSum(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersMapSum constraint){
        this.constraint = constraint;
        this.sum = 0;
    }

    /**
     * Create a StateSum with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateSum with given parameters
     */
    public static StateMapSum create(ParametersMapSum constraint){
        StateMapSum object = allocator().allocate();
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


    public String toString(){
        return Integer.toString(sum);
    }

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateMapSum state = StateMapSum.create(constraint);
        state.sum = sum;
        if(constraint.isVariable(layer-1)) state.sum += constraint.map(label);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        if(!constraint.isVariable(layer-1)) return true;
        label = constraint.map(label);
        int minPotential = sum + label + constraint.vMin(layer-1);
        int maxPotential = sum + label + constraint.vMax(layer-1);


        if(maxPotential < constraint.min() || constraint.max() < minPotential) return false;
        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return true;
        return sum + label <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        if(constraint.isVariable(layer-1)) label = constraint.map(label);
        else label = 0;
        int minPotential = sum + label + constraint.vMin(layer-1);
        int maxPotential = sum + label + constraint.vMax(layer-1);

        if(constraint.min() <= minPotential && maxPotential <= constraint.max()) return "";
        return Integer.toString(sum + label);
    }

    @Override
    public void free(){
        this.constraint = null;
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the StateMapSum type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateMapSum> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateMapSum[] arrayCreation(int capacity) {
            return new StateMapSum[capacity];
        }

        @Override
        protected StateMapSum createObject(int index) {
            return new StateMapSum(index);
        }
    }

}
