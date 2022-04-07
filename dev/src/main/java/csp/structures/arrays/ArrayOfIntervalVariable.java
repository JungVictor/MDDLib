package csp.structures.arrays;

import csp.IntervalVariable;
import memory.AllocatorOf;
import structures.arrays.ArrayOf;

public class ArrayOfIntervalVariable extends ArrayOf<IntervalVariable> {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // The array
    private IntervalVariable[] array;

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an ArrayOfNode with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An ArrayOfNode with given capacity
     */
    public static ArrayOfIntervalVariable create(int capacity){
        ArrayOfIntervalVariable object = allocator().allocate();
        object.init(capacity);
        return object;
    }

    /**
     * Create an ArrayOfNode.
     * It will be put at the specified index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    private ArrayOfIntervalVariable(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    protected IntervalVariable[] array() {
        return array;
    }

    @Override
    protected void arrayAllocation(int capacity) {
        array = new IntervalVariable[capacity];
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
     * <b>The allocator that is in charge of the ArrayOfNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ArrayOfIntervalVariable> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ArrayOfIntervalVariable[] arrayCreation(int capacity) {
            return new ArrayOfIntervalVariable[capacity];
        }

        @Override
        protected ArrayOfIntervalVariable createObject(int index) {
            return new ArrayOfIntervalVariable(index);
        }
    }
}
