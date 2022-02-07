package structures.arrays;

import memory.AllocatorOf;
import structures.tuples.TupleOfInt;

public class ArrayOfTupleOfInt extends ArrayOf<TupleOfInt> {

    // Thread safe allocator
    private final static ThreadLocal<ArrayOfTupleOfInt.Allocator> localStorage = ThreadLocal.withInitial(ArrayOfTupleOfInt.Allocator::new);

    // The array
    private TupleOfInt[] array;

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static ArrayOfTupleOfInt.Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an ArrayOfTupleOfInt with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An ArrayOfTupleOfInt with given capacity
     */
    public static ArrayOfTupleOfInt create(int capacity){
        ArrayOfTupleOfInt object = allocator().allocate();
        object.init(capacity);
        return object;
    }

    /**
     * Create an ArrayOfTupleOfInt.
     * It will be put at the specified index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    private ArrayOfTupleOfInt(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    protected TupleOfInt[] array() {
        return array;
    }

    @Override
    protected void arrayAllocation(int capacity) {
        array = new TupleOfInt[capacity];
    }

    @Override
    protected int size(){
        return array.length;
    }

    @Override
    public void free(){
        for(int i = 0; i < length(); i++) array[i] = null;
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the ArrayOfTupleOfInt type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ArrayOfTupleOfInt> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ArrayOfTupleOfInt[] arrayCreation(int capacity) {
            return new ArrayOfTupleOfInt[capacity];
        }

        @Override
        protected ArrayOfTupleOfInt createObject(int index) {
            return new ArrayOfTupleOfInt(index);
        }
    }

}
