package dd.mdd.pmdd.components.properties;

import memory.AllocatorOf;
import memory.Memory;
import structures.generics.MapOf;
import structures.tuples.TupleOfDouble;

/**
 * <b>SUM CONSTRAINT (DOUBLE)</b><br>
 * We use an interval to represents the [min, max] value of the sum.<br>
 */
public strictfp class PropertySumDouble extends NodeProperty {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    private TupleOfDouble value;
    private MapOf<Integer, Double> bindings;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    public PropertySumDouble(int allocatedIndex){
        super(allocatedIndex);
        super.setName(SUM);
    }

    /**
     * Create a new property corresponding to given parameters.
     * @param v1 The minimum value of the property's usm
     * @param v2 The maximum value of the property's sum
     * @return a new property corresponding to given parameters.
     */
    public static PropertySumDouble create(double v1, double v2){
        PropertySumDouble property = allocator().allocate();
        property.prepare();
        property.setValue(v1, v2);
        return property;
    }

    /**
     * Create a new property corresponding to given parameters.
     * @param v1 The minimum value of the property's usm
     * @param v2 The maximum value of the property's sum
     * @param bindings The map associating labels with values
     * @return a new property corresponding to given parameters.
     */
    public static PropertySumDouble create(double v1, double v2, MapOf<Integer, Double> bindings){
        PropertySumDouble property = create(v1, v2);
        property.setBindings(bindings);
        return property;
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

    /**
     * Set the bindings
     * @param bindings The map associating labels with values
     */
    public void setBindings(MapOf<Integer, Double> bindings){
        this.bindings = bindings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return value.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object){
        if(!(object instanceof PropertySumDouble)) return false;
        PropertySumDouble prop = (PropertySumDouble) object;
        return prop.value.getFirst() == value.getFirst() && prop.value.getSecond() == value.getSecond();
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getArray

    /**
     * {@inheritDoc}
     */
    @Override
    public MapOf getData(){
        MapOf<Integer, Double> data = Memory.MapOfIntegerDouble();
        data.put(0, value.getFirst());
        data.put(1, value.getSecond());
        return data;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeProperty createProperty(int val) {
        double v = val;
        if(bindings != null) v = bindings.get(val);
        return PropertySumDouble.create(value.getFirst()+v, value.getSecond()+v, bindings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeWithProperty(int label, NodeProperty nodeProperty){
        double val = label;
        if(bindings != null) val = bindings.get(label);
        PropertySumDouble property = (PropertySumDouble) nodeProperty;
        property.value.setFirst(Math.min(value.getFirst()+val, property.value.getFirst()));
        property.value.setSecond(Math.max(value.getSecond()+val, property.value.getSecond()));
        System.out.print("");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        Memory.free(value);
        this.bindings = null;
        super.free();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(){
        super.prepare();
        this.value = TupleOfDouble.create();
    }


    /**
     * <b>The allocator that is in charge of the PropertySumDouble type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PropertySumDouble> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertySumDouble[] arrayCreation(int capacity) {
            return new PropertySumDouble[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertySumDouble createObject(int index) {
            return new PropertySumDouble(index);
        }
    }
}
