package confidence.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.generics.MapOf;
import structures.arrays.ArrayOfDouble;

public class ParametersSumDouble implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    // References, must not be free or cleaned by the object
    private double min, max;
    private ArrayOfDouble vMin, vMax;
    private MapOf<Integer, Double> mapDouble;
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

    private ParametersSumDouble(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, MapOf<Integer, Double> mapDouble, int precision){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.mapDouble = mapDouble;
        this.precision = precision;
    }

    public static ParametersSumDouble create(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, MapOf<Integer, Double> mapDouble, int precision){
        ParametersSumDouble object = allocator().allocate();
        object.init(min, max, vMin, vMax, mapDouble, precision);
        return object;
    }

    //**************************************//

    public double min(){return min;}
    public double max(){return max;}
    public double vMin(int i){return vMin.get(i);}
    public double vMax(int i){return vMax.get(i);}
    public double mapDouble(int i){return mapDouble.get(i);}
    public int precision(){return precision;}

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free() {
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the ParametersSumDouble type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersSumDouble> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersSumDouble[] arrayCreation(int capacity) {
            return new ParametersSumDouble[capacity];
        }

        @Override
        protected ParametersSumDouble createObject(int index) {
            return new ParametersSumDouble(index);
        }
    }
}
