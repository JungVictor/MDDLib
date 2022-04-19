package structures.arrays;

import dd.interfaces.NodeInterface;
import memory.AllocatorOf;

public class ArrayOfNodeInterface extends ArrayOf<NodeInterface> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // The array
    private NodeInterface[] array;

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an ArrayOfAbstractNode with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An ArrayOfAbstractNode with given capacity
     */
    public static ArrayOfNodeInterface create(int capacity){
        ArrayOfNodeInterface object = allocator().allocate();
        object.init(capacity);
        return object;
    }

    /**
     * Create an ArrayOfAbstractNode.
     * It will be put at the specified index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    private ArrayOfNodeInterface(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    protected NodeInterface[] array() {
        return array;
    }

    @Override
    protected void arrayAllocation(int capacity) {
        array = new NodeInterface[capacity];
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
     * <b>The allocator that is in charge of the ArrayOfAbstractNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ArrayOfNodeInterface> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ArrayOfNodeInterface[] arrayCreation(int capacity) {
            return new ArrayOfNodeInterface[capacity];
        }

        @Override
        protected ArrayOfNodeInterface createObject(int index) {
            return new ArrayOfNodeInterface(index);
        }
    }

}

