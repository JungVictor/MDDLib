package confidence.structures;

import memory.AllocatorOf;
import structures.arrays.ArrayOf;

import java.math.BigInteger;

public class ArrayOfBigInteger extends ArrayOf<BigInteger> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // The array
    private BigInteger[] array;

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an SuccessionOfMDD with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An SuccessionOfMDD with given capacity
     */
    public static ArrayOfBigInteger create(int capacity){
        ArrayOfBigInteger object = allocator().allocate();
        object.init(capacity);
        return object;
    }

    /**
     * Create an SuccessionOfMDD.
     * It will be put at the specified index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    private ArrayOfBigInteger(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public void free() {
        for(int i = 0; i < length(); i++) array[i] = null;
        allocator().free(this);
    }

    @Override
    protected BigInteger[] array() {
        return array;
    }

    @Override
    protected void arrayAllocation(int capacity) {
        array = new BigInteger[capacity];
    }

    @Override
    protected int size(){
        return array.length;
    }

    /**
     * <b>The allocator that is in charge of the SuccessionOfMDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ArrayOfBigInteger> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ArrayOfBigInteger[] arrayCreation(int capacity) {
            return new ArrayOfBigInteger[capacity];
        }

        @Override
        protected ArrayOfBigInteger createObject(int index) {
            return new ArrayOfBigInteger(index);
        }
    }

}
