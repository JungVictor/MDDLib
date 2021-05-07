package structures.integers;

import memory.MemoryObject;
import memory.MemoryPool;

/**
 * <b>The class representing a tuple of int.</b>
 */
public class TupleOfInt implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<TupleOfInt> pool;
    private int ID = -1;
    //

    private int e1, e2;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public TupleOfInt(MemoryPool<TupleOfInt> pool){
        this.pool = pool;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    // toString

    @Override
    public String toString(){
        return "["+e1+", " + e2 + "]";
    }


    //**************************************//
    //          TUPLE MANAGEMENT            //
    //**************************************//

    /**
     * Set both values at the same time
     * @param e1 First value
     * @param e2 Second value
     */
    public void set(int e1, int e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Set the first value to the given value
     * @param e1 First value
     */
    public void setFirst(int e1){
        this.e1 = e1;
    }

    /**
     * Set the second value to the given value
     * @param e2 Second value
     */
    public void setSecond(int e2){
        this.e2 = e2;
    }

    /**
     * Get the first value of the tuple
     * @return the first value of the tuple
     */
    public int getFirst(){
        return e1;
    }

    /**
     * Get the second value of the tuple
     * @return the second value of the tuple
     */
    public int getSecond(){
        return e2;
    }

    /**
     * Increment both values by the specified among
     * @param i1 Amount for the first value
     * @param i2 Amount for the second value
     */
    public void incr(int i1, int i2){
        this.e1 += i1;
        this.e2 += i2;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        e1 = 0;
        e2 = 0;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        this.pool.free(this, ID);
    }
}
