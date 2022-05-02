package structures.lists;

import dd.interfaces.INode;
import memory.AllocatorOf;

public class ListOfNodeInterface extends ListOf<INode> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private INode[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private ListOfNodeInterface(int allocatedIndex){
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
    public static ListOfNodeInterface create(int capacity){
        ListOfNodeInterface object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new INode[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfLayer with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfLayer with initial capacity of 16.
     */
    public static ListOfNodeInterface create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public INode[] list() {
        return list;
    }

    @Override
    protected INode[] createList(int capacity) {
        return new INode[capacity];
    }

    @Override
    protected void setList(INode[] list) {
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

    static final class Allocator extends AllocatorOf<ListOfNodeInterface> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ListOfNodeInterface[] arrayCreation(int capacity) {
            return new ListOfNodeInterface[capacity];
        }

        @Override
        protected ListOfNodeInterface createObject(int index) {
            return new ListOfNodeInterface(index);
        }
    }
}
