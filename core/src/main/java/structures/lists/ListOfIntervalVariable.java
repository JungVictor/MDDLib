package structures.lists;

import csp.IntervalVariable;
import memory.AllocatorOf;

public class ListOfIntervalVariable extends ListOf<IntervalVariable> {

    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private IntervalVariable[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private ListOfIntervalVariable(int allocatedIndex){
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
     * Create a ListOfIntervalVariable with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return A ListOfIntervalVariable with given initial capacity
     */
    public static ListOfIntervalVariable create(int capacity){
        ListOfIntervalVariable object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new IntervalVariable[capacity];
        return object;
    }

    /**
     * Create a ListOfIntervalVariable with initial capacity of 16.
     * The object is managed by the allocator.
     * @return A ListOfIntervalVariable with initial capacity of 16.
     */
    public static ListOfIntervalVariable create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public IntervalVariable[] list() {
        return list;
    }

    @Override
    protected IntervalVariable[] createList(int capacity) {
        return new IntervalVariable[capacity];
    }

    @Override
    protected void setList(IntervalVariable[] list) {
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

    static final class Allocator extends AllocatorOf<ListOfIntervalVariable> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ListOfIntervalVariable[] arrayCreation(int capacity) {
            return new ListOfIntervalVariable[capacity];
        }

        @Override
        protected ListOfIntervalVariable createObject(int index) {
            return new ListOfIntervalVariable(index);
        }
    }
}
