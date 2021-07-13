package builder.constraints.parameters;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.SetOf;

public class ParametersAmong implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ParametersAmong> pool;
    private int ID = -1;
    //

    // References, must not be free or cleaned by the object
    private int q, min, max;
    private SetOf<Integer> V;

    public ParametersAmong(MemoryPool<ParametersAmong> pool){
        this.pool = pool;
    }

    public void init(int q, int min, int max, SetOf<Integer> V){
        this.q = q;
        this.min = min;
        this.max = max;
        this.V = V;
    }

    public int q(){return q;}
    public int min(){return min;}
    public int max(){return max;}
    public SetOf<Integer> V(){return V;}
    public boolean contains(int label){
        return V.contains(label);
    }

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
        this.V = null;
        this.pool.free(this, this.ID);
    }

}
