package pmdd.components.properties;

import memory.Memory;
import memory.MemoryPool;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

public class PropertyAmong extends NodeProperty {

    private ArrayOfInt values;
    private int q, min, max, n;
    boolean first;
    private SetOf<Integer> V;

    public PropertyAmong(MemoryPool<NodeProperty> pool) {
        super(pool);
    }

    public void setParameters(int q, int min, int max, SetOf<Integer> V){
        this.q = q;
        this.min = min;
        this.max = max;
        this.n = 0;
        first = false;
        this.values = Memory.ArrayOfInt(q);
        this.V = V;
    }

    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty
    // getResult

    @Override
    public NodeProperty createProperty(int value){
        return null;
    }

    @Override
    public void mergeWithProperty(NodeProperty property){

    }

    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isDegenerate(){
        return n > max;
    }

    @Override
    public boolean isDegenerate(int v){
        if(!V.contains(v)) return true;
        if(first) return true;
        return n+1 > max;
    }

    @Override
    public boolean isDegenerate(int v, boolean lastLayer){
        if(lastLayer){

        }
        return isDegenerate(v);
    }

}
