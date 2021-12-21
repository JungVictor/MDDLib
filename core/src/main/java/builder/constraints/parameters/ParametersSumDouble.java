package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.generics.MapOf;
import structures.arrays.ArrayOfDouble;
import structures.generics.SetOf;

public class ParametersSumDouble extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // References, must not be free or cleaned by the object
    private double min, max;
    private ArrayOfDouble vMin, vMax;
    private MapOf<Integer, Double> mapDouble;
    private int epsilon;


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
        super(allocatedIndex);
    }

    public void init(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, MapOf<Integer, Double> mapDouble, int epsilon, SetOf<Integer> variables){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.mapDouble = mapDouble;
        this.epsilon = epsilon;
        super.setVariables(variables);
    }

    public static ParametersSumDouble create(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, MapOf<Integer, Double> mapDouble, int precision, SetOf<Integer> variables){
        ParametersSumDouble object = allocator().allocate();
        object.init(min, max, vMin, vMax, mapDouble, precision, variables);
        return object;
    }

    //**************************************//

    public double min(){return min;}
    public double max(){return max;}
    public double vMin(int i){return vMin.get(i);}
    public double vMax(int i){return vMax.get(i);}
    public double mapDouble(int i){return mapDouble.get(i);}
    public int epsilon(){return epsilon;}

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
