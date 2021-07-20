package confidence.parameters;

import confidence.structures.PrimeFactorization;
import confidence.structures.arrays.ArrayOfPrimeFactorization;
import memory.Allocable;
import memory.AllocatorOf;
import structures.generics.MapOf;


public class ParametersMulPF implements Allocable {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    // References, must not be free or cleaned by the object
    private double min, max;
    private ArrayOfPrimeFactorization vMin, vMax;
    private MapOf<Integer, PrimeFactorization> mapPrimeFact;

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

    private ParametersMulPF(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(double min, double max, ArrayOfPrimeFactorization vMin, ArrayOfPrimeFactorization vMax, MapOf<Integer, PrimeFactorization> mapPrimeFact){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.mapPrimeFact = mapPrimeFact;
    }

    public static ParametersMulPF create(double min, double max, ArrayOfPrimeFactorization vMin, ArrayOfPrimeFactorization vMax, MapOf<Integer, PrimeFactorization> mapPrimeFact){
        ParametersMulPF object = allocator().allocate();
        object.init(min, max, vMin, vMax, mapPrimeFact);
        return object;
    }

    //**************************************//

    public double min(){return min;}
    public double max(){return max;}
    public PrimeFactorization vMin(int i){return vMin.get(i);}
    public PrimeFactorization vMax(int i){return vMax.get(i);}
    public PrimeFactorization mapPrimeFact(int i){return mapPrimeFact.get(i);}

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
     * <b>The allocator that is in charge of the ParametersMul type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersMulPF> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersMulPF[] arrayCreation(int capacity) {
            return new ParametersMulPF[capacity];
        }

        @Override
        protected ParametersMulPF createObject(int index) {
            return new ParametersMulPF(index);
        }
    }
}
