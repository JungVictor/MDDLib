package structures.arrays;

import mdd.MDD;
import memory.AllocatorOf;

public class ArrayOfMDD extends ArrayOf<MDD> {

    // Thread safe allocator
    private final static ThreadLocal<ArrayOfMDD.Allocator> localStorage = ThreadLocal.withInitial(ArrayOfMDD.Allocator::new);

    // The array
    private MDD[] array;

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static ArrayOfMDD.Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an SuccessionOfMDD with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An SuccessionOfMDD with given capacity
     */
    public static ArrayOfMDD create(int capacity){
        ArrayOfMDD object = allocator().allocate();
        object.init(capacity);
        return object;
    }

    /**
     * Create an SuccessionOfMDD.
     * It will be put at the specified index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    private ArrayOfMDD(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public void free() {
        for(int i = 0; i < length(); i++) array[i] = null;
        allocator().free(this);
    }

    @Override
    protected MDD[] array() {
        return array;
    }

    @Override
    protected void arrayAllocation(int capacity) {
        array = new MDD[capacity];
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
    static final class Allocator extends AllocatorOf<ArrayOfMDD> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ArrayOfMDD[] arrayCreation(int capacity) {
            return new ArrayOfMDD[capacity];
        }

        @Override
        protected ArrayOfMDD createObject(int index) {
            return new ArrayOfMDD(index);
        }
    }

}
