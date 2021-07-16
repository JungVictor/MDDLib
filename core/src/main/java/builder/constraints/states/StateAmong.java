package builder.constraints.states;

import builder.constraints.parameters.ParametersAmong;
import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfInt;

public class StateAmong extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private ArrayOfInt among;

    // Must not be free
    private ParametersAmong constraint;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public StateAmong(int allocatedIndex){
        super(allocatedIndex);
    }

    public void init(ParametersAmong constraint){
        this.constraint = constraint;
        among = ArrayOfInt.create(constraint.q());
    }

    /**
     * Create a StateAmong with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateAmong with given parameters
     */
    public static StateAmong create(ParametersAmong constraint){
        StateAmong object = allocator().allocate();
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
        StateAmong state = StateAmong.create(constraint);
        for(int i = 1; i < constraint.q(); i++) state.among.set(i-1, among.get(i));
        if(constraint.contains(label)) state.among.set(constraint.q()-1, 1);
        else state.among.set(constraint.q()-1, 0);
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size){
        int cpt = 0;
        for(int i = 1; i < constraint.q(); i++) cpt += among.get(i);
        if(constraint.contains(label)) cpt++;
        int potential = constraint.q() - layer;
        if(potential < 0) potential = 0;
        return constraint.min() <= cpt + potential && cpt <= constraint.max();
    }

    @Override
    public String hash(int label, int layer, int size){
        int r = size - layer;
        int cpt = 0;
        for(int i = r; i < constraint.q(); i++) cpt += among.get(i);
        if(constraint.min() <= cpt && cpt + r <= constraint.max()) return "";

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if(among.length > 0) {
            for (int i = 1; i < among.length; i++) {
                builder.append(among.get(i));
                builder.append(", ");
            }
            builder.append(constraint.contains(label) ? 1 : 0);
        }
        builder.append("]");
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(among);
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateAmong type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateAmong> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected StateAmong[] arrayCreation(int capacity) {
            return new StateAmong[capacity];
        }

        @Override
        protected StateAmong createObject(int index) {
            return new StateAmong(index);
        }
    }

}
