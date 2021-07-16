package builder.constraints.states;

import builder.constraints.parameters.ParametersAllDiff;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.SetOf;

public class StateAllDiff extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private SetOf<Integer> alldiff;
    private ParametersAllDiff constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public StateAllDiff(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersAllDiff constraint){
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

    @Override
    public NodeState createState(int label, int layer, int size) {
        StateAllDiff state = StateAllDiff.create(constraint);
        state.alldiff.add(alldiff);
        if(constraint.contains(label)) alldiff.add(label);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        return !constraint.contains(label) || !alldiff.contains(label);
    }

    @Override
    public String hash(int label, int layer, int size) {
        StringBuilder builder = new StringBuilder();
        for(int v : alldiff) {
            if(v == label || constraint.contains(v)) builder.append("1");
            else builder.append("0");
        }
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

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
            this(16);
        }

        @Override
        protected StateAllDiff[] arrayCreation(int capacity) {
            return new StateAllDiff[capacity];
        }

        @Override
        protected StateAllDiff createObject(int index) {
            return new StateAllDiff(index);
        }
    }
}
