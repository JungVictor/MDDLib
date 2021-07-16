package builder.constraints.states;

import builder.constraints.parameters.ParametersGCC;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.MapOf;
import structures.lists.ListOfInt;

public class StateGCC extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // Private reference
    private MapOf<Integer, Integer> count;
    private int minimum;

    // Shared references : constraint
    private ParametersGCC constraint;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public StateGCC(int allocatedIndex) {
        super(allocatedIndex);
    }

    public void init(ParametersGCC constraint){
        this.constraint = constraint;
        this.minimum = constraint.minimum();
        this.count = Memory.MapOfIntegerInteger();
    }

    public void initV(){
        for(int v : constraint.V()) count.put(v,0);
    }

    /**
     * Create a StateGCC with specified parameters.
     * The object is managed by the allocator.
     * @param constraint Parameters of the constraint
     * @return A StateGCC with given parameters
     */
    public static StateGCC create(ParametersGCC constraint){
        StateGCC object = allocator().allocate();
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
        StateGCC state = StateGCC.create(constraint);
        state.minimum = minimum;
        int potential = size - layer - 1;
        for(int v : count) {
            if(count.get(v) < constraint.min(v) || count.get(v) + potential > constraint.max(v)) state.count.put(v, count.get(v));
        }
        if(state.count.contains(label)){
            if (count.get(label) < constraint.min(label)) state.minimum--;
            // If we are sure that, whatever the value, we satisfy the gcc, we remove the value
            // So we only add the value when we are not sure
            if(count.get(label) + 1 >= constraint.min(label) && count.get(label) + potential + 1 <= constraint.max(label)) state.count.remove(label);
            else state.count.put(label, state.count.get(label) + 1);
            //state.count.put(label, state.count.get(label) + 1);
        }
        return state;
    }

    @Override
    public boolean isValid(int label, int layer, int size) {
        int potential = size - layer - 1;
        int minimum = this.minimum;

        if(!count.contains(label)) return minimum <= potential;
        int value = count.get(label);
        if(value < constraint.min(label)) minimum--;
        return minimum <= potential && value+1 <= constraint.max(label);
    }

    @Override
    public String hash(int label, int layer, int size){
        ListOfInt integers = ListOfInt.create();
        integers.add(count.keySet());
        integers.sort();
        StringBuilder builder = new StringBuilder();
        for (int v : integers) {
            if(v == label && count.get(label) + size - layer <= constraint.max(label)) continue;
            else if(count.get(v) >= constraint.min(v) && count.get(v) + (size-1) - layer <= constraint.max(v)) continue;
            builder.append(v);
            builder.append(" -> ");
            if(v != label) builder.append(count.get(v));
            else builder.append(count.get(v)+1);
            builder.append("; ");
        }
        Memory.free(integers);
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(count);
        this.constraint = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the StateGCC type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StateGCC> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateGCC[] arrayCreation(int capacity) {
            return new StateGCC[capacity];
        }

        @Override
        protected StateGCC createObject(int index) {
            return new StateGCC(index);
        }
    }
}
