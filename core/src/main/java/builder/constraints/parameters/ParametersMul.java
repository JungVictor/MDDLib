package builder.constraints.parameters;

import structures.arrays.ArrayOfBigInteger;
import memory.Allocable;
import memory.AllocatorOf;
import structures.generics.SetOf;

import java.math.BigInteger;

public class ParametersMul extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


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
        super(allocatedIndex);
    }

    public void init(BigInteger min, BigInteger max, ArrayOfBigInteger vMin, ArrayOfBigInteger vMax, SetOf<Integer> variables){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        super.setVariables(variables);
    }

    public static ParametersMul create(BigInteger min, BigInteger max, ArrayOfBigInteger vMin, ArrayOfBigInteger vMax, SetOf<Integer> variables){
        ParametersMul object = allocator().allocate();
        object.init(min, max, vMin, vMax, variables);
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
    public void free() {
        super.free();
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
            super.init();
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
