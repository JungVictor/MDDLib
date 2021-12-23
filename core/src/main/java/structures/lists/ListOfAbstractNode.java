package structures.lists;

import dd.AbstractNode;
import memory.AllocatorOf;

public class ListOfAbstractNode extends ListOf<AbstractNode> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private AbstractNode[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private ListOfAbstractNode(int allocatedIndex){
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
     * Create an UnorderedListOfLayer with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return An UnorderedListOfLayer with given initial capacity
     */
    public static ListOfAbstractNode create(int capacity){
        ListOfAbstractNode object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new AbstractNode[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfLayer with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfLayer with initial capacity of 16.
     */
    public static ListOfAbstractNode create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public AbstractNode[] list() {
        return list;
    }

    @Override
    protected AbstractNode[] createList(int capacity) {
        return new AbstractNode[capacity];
    }

    @Override
    protected void setList(AbstractNode[] list) {
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

    static final class Allocator extends AllocatorOf<ListOfAbstractNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ListOfAbstractNode[] arrayCreation(int capacity) {
            return new ListOfAbstractNode[capacity];
        }

        @Override
        protected ListOfAbstractNode createObject(int index) {
            return new ListOfAbstractNode(index);
        }
    }
}
