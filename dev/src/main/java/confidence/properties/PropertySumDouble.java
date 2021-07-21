package confidence.properties;

import confidence.MyMemory;
import memory.Memory;
import memory.MemoryPool;
import pmdd.components.properties.NodeProperty;
import structures.generics.MapOf;
import structures.integers.TupleOfDouble;

public class PropertySumDouble extends NodeProperty {

    private TupleOfDouble value;
    private MapOf<Integer, Double> bindings;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public PropertySumDouble(MemoryPool<NodeProperty> pool){
        super(pool);
        super.setName(SUM);
    }

    /**
     * Set the current values of the property and the minimum and maximum values
     * @param v1 current min value
     * @param v2 current max value
     */
    public void setValue(double v1, double v2){
        this.value = TupleOfDouble.create();
        this.value.set(v1, v2);
    }

    public void setBindings(MapOf<Integer, Double> bindings){
        this.bindings = bindings;
    }

    @Override
    public String toString(){
        return value.toString();
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray

    @Override
    public MapOf getData(){
        MapOf<Integer, Double> data = MyMemory.MapOfIntegerDouble();
        data.put(0, value.getFirst());
        data.put(1, value.getSecond());
        return data;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    @Override
    public NodeProperty createProperty(int val) {
        double v = val;
        if(bindings != null) v = bindings.get(val);
        return MyMemory.PropertySumDouble(value.getFirst()+v, value.getSecond()+v, bindings);
    }

    @Override
    public void mergeWithProperty(int label, NodeProperty nodeProperty){
        double val = label;
        if(bindings != null) val = bindings.get(label);
        PropertySumDouble property = (PropertySumDouble) nodeProperty;
        property.value.setFirst(Math.min(value.getFirst()+val, property.value.getFirst()));
        property.value.setSecond(Math.max(value.getSecond()+val, property.value.getSecond()));
        System.out.print("");
    }

    @Override
    public void mergeWithProperty(NodeProperty property){
        if(property.getClass() != PropertySumDouble.class) return;
        PropertySumDouble sum = (PropertySumDouble) property;

        value.setFirst(Math.min(value.getFirst(), sum.value.getFirst()));
        value.setSecond(Math.max(value.getSecond(), sum.value.getSecond()));
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free(){
        Memory.free(value);
        this.bindings = null;
        super.free();
    }

    @Override
    public void prepare(){
        super.prepare();
        this.value = TupleOfDouble.create();
    }
}
