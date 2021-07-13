package WIP.ndmdd;


import memory.MemoryObject;
import memory.MemoryPool;

// Non Deterministic MDD
public class NDMDD implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<NDMDD> pool;
    private int ID = -1;
    //

    public NDMDD(MemoryPool<NDMDD> pool){
        this.pool = pool;
    }


    @Override
    public void prepare() {

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
