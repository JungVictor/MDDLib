package csp.structures.lists;

import csp.constraint.IntervalConstraint;
import memory.AllocatorOf;
import structures.lists.ListOf;

public class ListOfIntervalConstraint extends ListOf<IntervalConstraint> {

    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private IntervalConstraint[] list;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private ListOfIntervalConstraint(int allocatedIndex){
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
     * Create a ListOfIntervalConstraint with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return A ListOfIntervalConstraint with given initial capacity
     */
    public static ListOfIntervalConstraint create(int capacity){
        ListOfIntervalConstraint object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new IntervalConstraint[capacity];
        return object;
    }

    /**
     * Create a ListOfIntervalConstraint with initial capacity of 16.
     * The object is managed by the allocator.
     * @return A ListOfIntervalConstraint with initial capacity of 16.
     */
    public static ListOfIntervalConstraint create(){
        return create(16);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    @Override
    public IntervalConstraint[] list() {
        return list;
    }

    @Override
    protected IntervalConstraint[] createList(int capacity) {
        return new IntervalConstraint[capacity];
    }

    @Override
    protected void setList(IntervalConstraint[] list) {
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

    static final class Allocator extends AllocatorOf<ListOfIntervalConstraint> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ListOfIntervalConstraint[] arrayCreation(int capacity) {
            return new ListOfIntervalConstraint[capacity];
        }

        @Override
        protected ListOfIntervalConstraint createObject(int index) {
            return new ListOfIntervalConstraint(index);
        }
    }
}
