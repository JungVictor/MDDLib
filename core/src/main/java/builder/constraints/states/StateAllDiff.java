package builder.constraints.states;

import builder.constraints.parameters.ParametersAllDiff;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.SetOf;

/**
 * <b>StateAllDiff</b><br>
 * Represent the state of an All Different constraint.
 */
public class StateAllDiff extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private SetOf<Integer> alldiff;
    private ParametersAllDiff constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateAllDiff(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersAllDiff constraint){
        this.alldiff = Memory.SetOfInteger();
        this.constraint = constraint;
    }

    /**
     * Create a StateAllDiff with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateAllDiff with given parameters
     */
    public static StateAllDiff create(ParametersAllDiff constraint){
        StateAllDiff object = allocator().allocate();
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
        StateAllDiff state = StateAllDiff.create(constraint);
        state.alldiff.add(alldiff);
        if(constraint.contains(label) && constraint.inScope(layer-1)) state.alldiff.add(label);
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        return !constraint.inScope(layer-1) || !constraint.contains(label) || !alldiff.contains(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signature(int label, int layer, int size) {
        if(!constraint.isLayerRemaining(layer)) return "";
        if(layer+1 == size) return "";
        StringBuilder builder = new StringBuilder();
        for(int v : constraint.set()) {
            if((v == label && constraint.inScope(layer-1)) || alldiff.contains(v)) builder.append("1");
            else builder.append("0");
        }
        return builder.toString();
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
        Memory.free(alldiff);
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateAllDiff type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateAllDiff> {

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
        protected StateAllDiff[] arrayCreation(int capacity) {
            return new StateAllDiff[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateAllDiff createObject(int index) {
            return new StateAllDiff(index);
        }
    }
}
