package builder.constraints.parameters;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.integers.ArrayOfInt;

public class ParametersSum implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ParametersSum> pool;
    private int ID = -1;
    //

    // References, must not be free or cleaned by the object
    private int min, max;
    private ArrayOfInt vMin, vMax;

    public ParametersSum(MemoryPool<ParametersSum> pool){
        this.pool = pool;
    }

    public void init(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public int min(){return min;}
    public int max(){return max;}
    public int vMin(int i){return vMin.get(i);}
    public int vMax(int i){return vMax.get(i);}

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
