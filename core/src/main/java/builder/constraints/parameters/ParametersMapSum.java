package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.arrays.ArrayOfInt;
import structures.generics.MapOf;
import structures.generics.SetOf;

public class ParametersMapSum extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    // References, must not be free or cleaned by the object
    private int min, max;
    private ArrayOfInt vMin, vMax;
    private MapOf<Integer, Integer> map;
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

    public ParametersMapSum(int allocatedIndex){
        super(allocatedIndex);
    }

    public void init(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax, MapOf<Integer, Integer> map, SetOf<Integer> variables){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.map = map;
        super.setVariables(variables);
    }

    public static ParametersMapSum create(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax, MapOf<Integer, Integer> map, SetOf<Integer> variables){
        ParametersMapSum object = allocator().allocate();
        object.init(min, max, vMin, vMax, map, variables);
        return object;
    }

    //**************************************//


    public int min(){return min;}
    public int max(){return max;}
    public int vMin(int i){return vMin.get(i);}
    public int vMax(int i){return vMax.get(i);}
    public int map(int i){return map.get(i);}


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
     * <b>The allocator that is in charge of the ParametersSum type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersMapSum> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersMapSum[] arrayCreation(int capacity) {
            return new ParametersMapSum[capacity];
        }

        @Override
        protected ParametersMapSum createObject(int index) {
            return new ParametersMapSum(index);
        }
    }
}
