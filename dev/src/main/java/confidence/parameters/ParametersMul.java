package confidence.parameters;

import confidence.structures.ArrayOfBigInteger;
import memory.Allocable;
import memory.AllocatorOf;

import java.math.BigInteger;

public class ParametersMul implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    // References, must not be free or cleaned by the object
    private BigInteger min, max;
    private ArrayOfBigInteger vMin, vMax;

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

    private ParametersMul(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(BigInteger min, BigInteger max, ArrayOfBigInteger vMin, ArrayOfBigInteger vMax){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public static ParametersMul create(BigInteger min, BigInteger max, ArrayOfBigInteger vMin, ArrayOfBigInteger vMax){
        ParametersMul object = allocator().allocate();
        object.init(min, max, vMin, vMax);
        return object;
    }

    //**************************************//

    public BigInteger min(){return min;}
    public BigInteger max(){return max;}
    public BigInteger vMin(int i){return vMin.get(i);}
    public BigInteger vMax(int i){return vMax.get(i);}

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
    static final class Allocator extends AllocatorOf<ParametersMul> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected ParametersMul[] arrayCreation(int capacity) {
            return new ParametersMul[capacity];
        }

        @Override
        protected ParametersMul createObject(int index) {
            return new ParametersMul(index);
        }
    }
}
