package structures.lists;

import dd.mdd.components.Layer;
import dd.mdd.nondeterministic.components.ILayer;
import memory.AllocatorOf;

public class ListOfILayer extends ListOf<ILayer> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private ILayer[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private ListOfILayer(int allocatedIndex){
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
    public static ListOfILayer create(int capacity){
        ListOfILayer object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new ILayer[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfLayer with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfLayer with initial capacity of 16.
     */
    public static ListOfILayer create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public ILayer[] list() {
        return list;
    }

    @Override
    protected ILayer[] createList(int capacity) {
        return new ILayer[capacity];
    }

    @Override
    protected void setList(ILayer[] list) {
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

    static final class Allocator extends AllocatorOf<ListOfILayer> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ListOfILayer[] arrayCreation(int capacity) {
            return new ListOfILayer[capacity];
        }

        @Override
        protected ListOfILayer createObject(int index) {
            return new ListOfILayer(index);
        }
    }
}
