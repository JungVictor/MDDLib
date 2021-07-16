package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.arrays.ArrayOfInt;

public class ParametersSum implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    // References, must not be free or cleaned by the object
    private int min, max;
    private ArrayOfInt vMin, vMax;

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

    public ParametersSum(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public static ParametersSum create(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax){
        ParametersSum object = allocator().allocate();
        object.init(min, max, vMin, vMax);
        return object;
    }

    //**************************************//


    public int min(){return min;}
    public int max(){return max;}
    public int vMin(int i){return vMin.get(i);}
    public int vMax(int i){return vMax.get(i);}


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
     * <b>The allocator that is in charge of the ParametersSum type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersSum> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersSum[] arrayCreation(int capacity) {
            return new ParametersSum[capacity];
        }

        @Override
        protected ParametersSum createObject(int index) {
            return new ParametersSum(index);
        }
    }
}
