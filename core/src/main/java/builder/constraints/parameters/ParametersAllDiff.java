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

    public void init(SetOf<Integer> V){
        this.V = V;
    }

    public static ParametersAllDiff create(SetOf<Integer> V){
        ParametersAllDiff object = allocator().allocate();
        object.init(V);
        return object;
    }

    //**************************************//

    public boolean contains(int label){
        return V.contains(label);
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
            this(16);
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
