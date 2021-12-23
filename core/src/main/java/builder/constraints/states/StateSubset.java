package builder.constraints.states;

import builder.constraints.parameters.ParametersSubset;
import memory.AllocatorOf;

public class StateSubset extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private int setID;
    private ParametersSubset constraint;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public StateSubset(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersSubset constraint, int setID){
        this.constraint = constraint;
        this.setID = setID;
    }

    /**
     * Create a StateSubset with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @param setID The ID of the set
     * @return A StateSubset with given parameters
     */
    public static StateSubset create(ParametersSubset constraint, int setID){
        StateSubset object = allocator().allocate();
        object.init(constraint, setID);
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

    @Override
    public NodeState createState(int label, int layer, int size) {
        return StateSubset.create(constraint, constraint.getNext(setID, label));
    }

    @Override
    public boolean isValid(int label, int layer, int size) {
        if(constraint.isEmpty(setID)) return label == -1;
        return constraint.isIn(setID, label);
    }

    @Override
    public String hash(int label, int layer, int size) {
        return Integer.toString(constraint.getNext(setID, label));
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    @Override
    public void free(){
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateSubset type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateSubset> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateSubset[] arrayCreation(int capacity) {
            return new StateSubset[capacity];
        }

        @Override
        protected StateSubset createObject(int index) {
            return new StateSubset(index);
        }
    }
}
