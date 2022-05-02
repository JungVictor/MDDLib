package structures.successions;

import dd.interfaces.INode;
import memory.AllocatorOf;

public class SuccessionOfAbstractNode extends SuccessionOfNodeInterface<INode> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // The array
    private INode[] array;

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an SuccessionOfNode with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An SuccessionOfNode with given capacity
     */
    public static SuccessionOfAbstractNode create(int capacity){
        SuccessionOfAbstractNode object = allocator().allocate();
        object.init(capacity);
        return object;
    }

    /**
     * Create an SuccessionOfNode.
     * It will be put at the specified index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    private SuccessionOfAbstractNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    protected INode[] array() {
        return array;
    }

    @Override
    protected void arrayAllocation(int capacity) {
        array = new INode[capacity];
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
     * <b>The allocator that is in charge of the SuccessionOfNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<SuccessionOfAbstractNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected SuccessionOfAbstractNode[] arrayCreation(int capacity) {
            return new SuccessionOfAbstractNode[capacity];
        }

        @Override
        protected SuccessionOfAbstractNode createObject(int index) {
            return new SuccessionOfAbstractNode(index);
        }
    }
}
