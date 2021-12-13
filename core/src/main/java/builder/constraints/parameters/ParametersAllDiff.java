package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.generics.SetOf;

public class ParametersAllDiff implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    // References, must not be free or cleaned by the object
    private SetOf<Integer> V;
    // Variables affected by the constraint
    private SetOf<Integer> variables;


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

    public ParametersAllDiff(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(SetOf<Integer> V, SetOf<Integer> variables){
        this.V = V;
        this.variables = variables;
    }

    public static ParametersAllDiff create(SetOf<Integer> V, SetOf<Integer> variables){
        ParametersAllDiff object = allocator().allocate();
        object.init(V, variables);
        return object;
    }

    public static ParametersAllDiff create(SetOf<Integer> V){
        return create(V, null);
    }

    //**************************************//

    public boolean contains(int label){
        return V.contains(label);
    }
    public boolean isVariable(int layer) {
        return variables == null || variables.contains(layer);
    }
    public SetOf<Integer> set(){
        return V;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free() {
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the ParametersAllDiff type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersAllDiff> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersAllDiff[] arrayCreation(int capacity) {
            return new ParametersAllDiff[capacity];
        }

        @Override
        protected ParametersAllDiff createObject(int index) {
            return new ParametersAllDiff(index);
        }
    }

}
