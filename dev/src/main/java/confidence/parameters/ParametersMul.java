package confidence.parameters;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.ArrayOf;

import java.math.BigInteger;

public class ParametersMul implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ParametersMul> pool;
    private int ID = -1;
    //

    // References, must not be free or cleaned by the object
    private BigInteger min, max;
    private ArrayOf<BigInteger> vMin, vMax;

    public ParametersMul(MemoryPool<ParametersMul> pool){
        this.pool = pool;
    }

    public void init(BigInteger min, BigInteger max, ArrayOf<BigInteger> vMin, ArrayOf<BigInteger> vMax){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public BigInteger min(){return min;}
    public BigInteger max(){return max;}
    public BigInteger vMin(int i){return vMin.get(i);}
    public BigInteger vMax(int i){return vMax.get(i);}

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
