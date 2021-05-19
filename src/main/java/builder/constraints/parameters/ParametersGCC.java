package builder.constraints.parameters;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.MapOf;
import structures.integers.TupleOfInt;

public class ParametersGCC implements MemoryObject {

    // Not to free
    private MapOf<Integer, TupleOfInt> gcc;

    // MemoryObject variables
    private final MemoryPool<ParametersGCC> pool;
    private int ID = -1;
    //

    public ParametersGCC(MemoryPool<ParametersGCC> pool){
        this.pool = pool;
    }

    public void init(MapOf<Integer, TupleOfInt> gcc){
        this.gcc = gcc;
    }

    public boolean contains(int label){
        return gcc.contains(label);
    }

    public int min(int label){
        return gcc.get(label).getFirst();
    }

    public int max(int label){
        return gcc.get(label).getSecond();
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
