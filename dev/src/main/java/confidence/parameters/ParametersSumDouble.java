package confidence.parameters;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.ArrayOf;
import structures.generics.MapOf;

public class ParametersSumDouble implements MemoryObject {
    // MemoryObject variables
    private final MemoryPool<ParametersSumDouble> pool;
    private int ID = -1;
    //

    // References, must not be free or cleaned by the object
    private double min, max;
    private ArrayOf<Double> vMin, vMax;
    private MapOf<Integer, Double> mapDouble;
    private int precision;

    public ParametersSumDouble(MemoryPool<ParametersSumDouble> pool){
        this.pool = pool;
    }

    public void init(double min, double max, ArrayOf<Double> vMin, ArrayOf<Double> vMax, MapOf<Integer, Double> mapDouble, int precision){
        this.min = min;
        this.max = max;
        this.vMin = vMin;
        this.vMax = vMax;
        this.mapDouble = mapDouble;
        this.precision = precision;
    }

    public double min(){return min;}
    public double max(){return max;}
    public double vMin(int i){return vMin.get(i);}
    public double vMax(int i){return vMax.get(i);}
    public double mapDouble(int i){return mapDouble.get(i);}
    public int precision(){return precision;}

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
