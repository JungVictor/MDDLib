package builder.constraints.states;

import builder.constraints.parameters.ParametersAllDiffMem;
import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfInt;

/**
 * <b>StateAllDiffMem</b><br>
 * Represent the state of an All Different constraint.
 */
public class StateAllDiffMem extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private ArrayOfInt alldiff;
    private ParametersAllDiffMem constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateAllDiffMem(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Initialisation of the state
     * @param constraint Parameters of the constraint
     */
    protected void init(ParametersAllDiffMem constraint){
        this.alldiff = ArrayOfInt.create(constraint.getCapacity());
        for(int i = 0; i < alldiff.length; i++) alldiff.set(i, -1);
        this.constraint = constraint;
    }

    /**
     * Create a StateAllDiffMem with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateAllDiffMem with given parameters
     */
    public static StateAllDiffMem create(ParametersAllDiffMem constraint){
        StateAllDiffMem object = allocator().allocate();
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
        StateAllDiffMem state = StateAllDiffMem.create(constraint);
        if(constraint.contains(label) && constraint.isVariable(layer-1)) {
            for (int i = 0; i < alldiff.length() - 1; i++) state.alldiff.set(i+1, alldiff.get(i));
            state.alldiff.set(0, label);
        } else for (int i = 0; i < alldiff.length(); i++) state.alldiff.set(i, alldiff.get(i));
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int label, int layer, int size){
        return !constraint.isVariable(layer-1) || !constraint.contains(label) || !alldiff.contains(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signature(int label, int layer, int size) {
        if(layer + 1 == size) return "tt";
        StringBuilder builder = new StringBuilder();
        if(constraint.contains(label) && constraint.isVariable(layer-1)) {
            builder.append(label); builder.append(" ");
            for (int i = 0; i < Math.min(alldiff.length()-1, layer); i++) {
                builder.append(alldiff.get(i));
                builder.append(" ");
            }
        } else for (int i = 0; i < Math.min(alldiff.length()-1, layer); i++) {
            builder.append(alldiff.get(i));
            builder.append(" ");
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
        this.alldiff = null;
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateAllDiffMem type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateAllDiffMem> {

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
        protected StateAllDiffMem[] arrayCreation(int capacity) {
            return new StateAllDiffMem[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected StateAllDiffMem createObject(int index) {
            return new StateAllDiffMem(index);
        }
    }
}
