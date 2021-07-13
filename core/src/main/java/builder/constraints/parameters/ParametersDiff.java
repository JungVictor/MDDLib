package builder.constraints.parameters;

import memory.MemoryObject;
import memory.MemoryPool;

public class ParametersDiff implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ParametersDiff> pool;
    private int ID = -1;
    //

    private int length;

    public ParametersDiff(MemoryPool<ParametersDiff> pool) {
        this.pool = pool;
    }

    public void init(int length){
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void prepare() {}

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        this.pool.free(this, ID);
    }
}
