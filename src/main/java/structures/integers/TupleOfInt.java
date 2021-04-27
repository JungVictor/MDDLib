package structures.integers;

import memory.MemoryObject;
import memory.MemoryPool;

public class TupleOfInt implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<TupleOfInt> pool;
    private int ID = -1;
    //

    private int e1, e2;

    public TupleOfInt(MemoryPool<TupleOfInt> pool){
        this.pool = pool;
    }

    public String toString(){
        return "["+e1+", " + e2 + "]";
    }

    public void set(int e1, int e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public void setFirst(int e1){
        this.e1 = e1;
    }

    public void setSecond(int e2){
        this.e2 = e2;
    }

    public int getFirst(){
        return e1;
    }

    public int getSecond(){
        return e2;
    }

    public void incr(int i1, int i2){
        this.e1 += i1;
        this.e2 += i2;
    }


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
