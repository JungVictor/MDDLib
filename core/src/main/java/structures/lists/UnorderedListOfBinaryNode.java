package structures.lists;

import bdd.components.BinaryNode;
import mdd.components.Node;
import memory.AllocatorOf;

public class UnorderedListOfBinaryNode extends UnorderedListOf<BinaryNode> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private BinaryNode[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private UnorderedListOfBinaryNode(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an UnorderedListOfNode with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return An UnorderedListOfNode with given initial capacity
     */
    public static UnorderedListOfBinaryNode create(int capacity){
        UnorderedListOfBinaryNode object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new BinaryNode[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfNode with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfNode with initial capacity of 16.
     */
    public static UnorderedListOfBinaryNode create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public BinaryNode[] list() {
        return list;
    }

    @Override
    protected BinaryNode[] createList(int capacity) {
        return new BinaryNode[capacity];
    }

    @Override
    protected void setList(BinaryNode[] list) {
        this.list = list;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    public void free() {
        clear();
        allocator().free(this);
    }

    static final class Allocator extends AllocatorOf<UnorderedListOfBinaryNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected UnorderedListOfBinaryNode[] arrayCreation(int capacity) {
            return new UnorderedListOfBinaryNode[capacity];
        }

        @Override
        protected UnorderedListOfBinaryNode createObject(int index) {
            return new UnorderedListOfBinaryNode(index);
        }
    }
}
