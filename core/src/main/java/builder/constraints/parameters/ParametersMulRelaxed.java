package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.arrays.ArrayOfDouble;

public class ParametersMulRelaxed implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    // References, must not be free or cleaned by the object
    private double min, max;
    private ArrayOfDouble vMin, vMax;
    private double maxProbaDomains;
    private double maxProbaEpsilon;

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

    private ParametersMulRelaxed(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, double maxProbaDomains, double maxProbaEpsilon){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.maxProbaDomains = maxProbaDomains;
        this.maxProbaEpsilon = maxProbaEpsilon;
    }

    public static ParametersMulRelaxed create(double min, double max, ArrayOfDouble vMin, ArrayOfDouble vMax, double maxProbaDomains, double maxProbaEpsilon){
        ParametersMulRelaxed object = allocator().allocate();
        object.init(min, max, vMin, vMax, maxProbaDomains, maxProbaEpsilon);
        return object;
    }

    //**************************************//

    public double min(){return min;}
    public double max(){return max;}
    public double vMin(int i){return vMin.get(i);}
    public double vMax(int i){return vMax.get(i);}
    public double maxProbaDomains(){return maxProbaDomains;}
    public double maxProbaEpsilon(){return maxProbaEpsilon;}

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
     * <b>The allocator that is in charge of the ParametersMulRelaxed type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersMulRelaxed> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersMulRelaxed[] arrayCreation(int capacity) {
            return new ParametersMulRelaxed[capacity];
        }

        @Override
        protected ParametersMulRelaxed createObject(int index) {
            return new ParametersMulRelaxed(index);
        }
    }
}
