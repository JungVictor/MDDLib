package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.generics.SetOf;

public class ParametersAmong extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // References, must not be free or cleaned by the object
    private int q, min, max;
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
    public ParametersAmong(int allocatedIndex){
        super(allocatedIndex);
    }

    public void init(int q, int min, int max, SetOf<Integer> V, SetOf<Integer> variables){
        this.q = q;
        this.min = min;
        this.max = max;
        this.V = V;
        super.setVariables(variables);
    }

    public static ParametersAmong create(int q, int min, int max, SetOf<Integer> V, SetOf<Integer> variables){
        ParametersAmong object = allocator().allocate();
        object.init(q, min, max, V, variables);
        return object;
    }

    //**************************************//

    public int q(){return q;}
    public int min(){return min;}
    public int max(){return max;}
    public SetOf<Integer> V(){return V;}
    public boolean contains(int label){
        return V.contains(label);
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the ParametersAmong type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersAmong> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersAmong[] arrayCreation(int capacity) {
            return new ParametersAmong[capacity];
        }

        @Override
        protected ParametersAmong createObject(int index) {
            return new ParametersAmong(index);
        }
    }

}
