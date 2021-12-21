package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.arrays.ArrayOfLong;
import structures.generics.MapOf;
import structures.generics.SetOf;

public class ParametersSumRelaxed extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // References, must not be free or cleaned by the object
    private long min, max;
    private ArrayOfLong vMin, vMax;
    private MapOf<Integer, Long> map;
    private int epsilon;
    private int precision;


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

    private ParametersSumRelaxed(int allocatedIndex){
        super(allocatedIndex);
    }

    public void init(long min, long max, ArrayOfLong vMin, ArrayOfLong vMax, MapOf<Integer, Long> map, int epsilon, int precision, SetOf<Integer> variables){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.map = map;
        this.epsilon = epsilon;
        this.precision = precision;
        super.setVariables(variables);
    }

    public static ParametersSumRelaxed create(long min, long max, ArrayOfLong vMin, ArrayOfLong vMax, MapOf<Integer, Long> map, int epsilon, int precision, SetOf<Integer> variables){
        ParametersSumRelaxed object = allocator().allocate();
        object.init(min, max, vMin, vMax, map, epsilon, precision, variables);
        return object;
    }

    //**************************************//

    public long min(){return min;}
    public long max(){return max;}
    public long vMin(int i){return vMin.get(i);}
    public long vMax(int i){return vMax.get(i);}
    public long map(int i){return map.get(i);}
    public int epsilon(){return epsilon;}
    public int precision(){return precision;}

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the ParametersSumDouble type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersSumRelaxed> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersSumRelaxed[] arrayCreation(int capacity) {
            return new ParametersSumRelaxed[capacity];
        }

        @Override
        protected ParametersSumRelaxed createObject(int index) {
            return new ParametersSumRelaxed(index);
        }
    }
}
