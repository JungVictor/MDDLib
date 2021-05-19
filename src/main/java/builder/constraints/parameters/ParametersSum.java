package builder.constraints.parameters;

import memory.MemoryObject;
import memory.MemoryPool;

public class ParametersSum implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ParametersSum> pool;
    private int ID = -1;
    //

    // References, must not be free or cleaned by the object
    private int min, max;
    private int vMin, vMax;

    public ParametersSum(MemoryPool<ParametersSum> pool){
        this.pool = pool;
    }

    public void init(int min, int max, int vMin, int vMax){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public int min(){return min;}
    public int max(){return max;}
    public int vMin(){return vMin;}
    public int vMax(){return vMax;}

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {

    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        this.pool.free(this, this.ID);
    }
}
