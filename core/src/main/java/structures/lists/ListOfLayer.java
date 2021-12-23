package structures.lists;

import dd.mdd.components.Layer;
import memory.AllocatorOf;

public class ListOfLayer extends ListOf<Layer> {

    // Thread safe allocator
    private final static ThreadLocal<ListOfLayer.Allocator> localStorage = ThreadLocal.withInitial(ListOfLayer.Allocator::new);

    private Layer[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private ListOfLayer(int allocatedIndex){
        super(allocatedIndex);
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static ListOfLayer.Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Create an UnorderedListOfLayer with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return An UnorderedListOfLayer with given initial capacity
     */
    public static ListOfLayer create(int capacity){
        ListOfLayer object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new Layer[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfLayer with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfLayer with initial capacity of 16.
     */
    public static ListOfLayer create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public Layer[] list() {
        return list;
    }

    @Override
    protected Layer[] createList(int capacity) {
        return new Layer[capacity];
    }

    @Override
    protected void setList(Layer[] list) {
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

    static final class Allocator extends AllocatorOf<ListOfLayer> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ListOfLayer[] arrayCreation(int capacity) {
            return new ListOfLayer[capacity];
        }

        @Override
        protected ListOfLayer createObject(int index) {
            return new ListOfLayer(index);
        }
    }
}
