package builder.constraints.parameters;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.SetOf;

public class ParametersAllDiff implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ParametersAllDiff> pool;
    private int ID = -1;
    //


    // References, must not be free or cleaned by the object
    private SetOf<Integer> V;


    public ParametersAllDiff(MemoryPool<ParametersAllDiff> pool){
        this.pool = pool;
    }

    public void init(SetOf<Integer> V){
        this.V = V;
    }

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
        this.pool.free(this, this.ID);
    }

}
