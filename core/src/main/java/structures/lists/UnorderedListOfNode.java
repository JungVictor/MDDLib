package structures.lists;

import mdd.components.Node;
import memory.AllocatorOf;

public class UnorderedListOfNode extends UnorderedListOf<Node> {

    // Thread safe allocator
    private final static ThreadLocal<UnorderedListOfNode.Allocator> localStorage = ThreadLocal.withInitial(UnorderedListOfNode.Allocator::new);

    private Node[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private UnorderedListOfNode(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static UnorderedListOfNode.Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an UnorderedListOfNode with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return An UnorderedListOfNode with given initial capacity
     */
    public static UnorderedListOfNode create(int capacity){
        UnorderedListOfNode object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new Node[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfNode with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfNode with initial capacity of 16.
     */
    public static UnorderedListOfNode create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public Node[] list() {
        return list;
    }

    @Override
    protected Node[] createList(int capacity) {
        return new Node[capacity];
    }

    @Override
    protected void setList(Node[] list) {
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

    static final class Allocator extends AllocatorOf<UnorderedListOfNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected UnorderedListOfNode[] arrayCreation(int capacity) {
            return new UnorderedListOfNode[capacity];
        }

        @Override
        protected UnorderedListOfNode createObject(int index) {
            return new UnorderedListOfNode(index);
        }
    }
}
