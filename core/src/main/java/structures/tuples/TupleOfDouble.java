package structures.tuples;

import memory.Allocable;
import memory.AllocatorOf;

public class TupleOfDouble implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private double e1, e2;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public TupleOfDouble(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a TupleOfInt with specified values.
     * The object is managed by the allocator.
     * @param e1 Value of the first element
     * @param e2 Value of the second element
     * @return A TupleOfInt with given capacity
     */
    public static TupleOfDouble create(double e1, double e2){
        TupleOfDouble object = allocator().allocate();
        object.set(e1, e2);
        return object;
    }

    /**
     * Create a TupleOfInt by copying another TupleOfInt
     * @param tuple Tuple to copy
     * @return A TupleOfInt with same values
     */
    public static TupleOfDouble create(TupleOfDouble tuple){
        return create(tuple.getFirst(), tuple.getSecond());
    }

    /**
     * Create a TupleOfInt with values (0,0).
     * The object is managed by the allocator.
     * @return A TupleOfInt with given capacity
     */
    public static TupleOfDouble create(){
        return create(0,0);
    }

    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    // toString

    @Override
    public String toString(){
        return "["+e1+", " + e2 + "]";
    }

    private static Allocator allocator(){
        return localStorage.get();
    }

    //**************************************//
    //          TUPLE MANAGEMENT            //
    //**************************************//

    /**
     * Set both values at the same time
     * @param e1 First value
     * @param e2 Second value
     */
    public void set(double e1, double e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Set the first value to the given value
     * @param e1 First value
     */
    public void setFirst(double e1){
        this.e1 = e1;
    }

    /**
     * Set the second value to the given value
     * @param e2 Second value
     */
    public void setSecond(double e2){
        this.e2 = e2;
    }

    /**
     * Get the first value of the tuple
     * @return the first value of the tuple
     */
    public double getFirst(){
        return e1;
    }

    /**
     * Get the second value of the tuple
     * @return the second value of the tuple
     */
    public double getSecond(){
        return e2;
    }

    /**
     * Increment both values by the specified among
     * @param i1 Amount for the first value
     * @param i2 Amount for the second value
     */
    public void incr(double i1, double i2){
        this.e1 += i1;
        this.e2 += i2;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free() {
        allocator().free(this);
    }


    static final class Allocator extends AllocatorOf<TupleOfDouble> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected TupleOfDouble[] arrayCreation(int capacity) {
            return new TupleOfDouble[capacity];
        }

        @Override
        protected TupleOfDouble createObject(int index) {
            return new TupleOfDouble(index);
        }
    }
}
