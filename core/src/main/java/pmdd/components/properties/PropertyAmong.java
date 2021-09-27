package pmdd.components.properties;

import memory.AllocatorOf;
import memory.Memory;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;

public class PropertyAmong extends NodeProperty {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    private ArrayOfInt values;
    private int min, max, n, depth;
    boolean first;
    private SetOf<Integer> V;

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

    public PropertyAmong(int allocatedIndex) {
        super(allocatedIndex);
        setName(AMONG);
    }

    public static PropertyAmong create(int q, int min, int max, SetOf<Integer> V){
        PropertyAmong property = allocator().allocate();
        property.prepare();
        property.setParameters(q, min, max, V);
        return property;
    }

    /**
     * Set the parameters of the among constraint.
     * @param q The size of the among constraint
     * @param min The minimum number of time a value in V is assigned
     * @param max The maximum number of time a value in V is assigned
     * @param V The set of values constrained
     */
    public void setParameters(int q, int min, int max, SetOf<Integer> V){
        this.min = min;
        this.max = max;
        this.n = 0;
        first = false;
        this.values = ArrayOfInt.create(q);
        this.V = V;
        this.depth = 0;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty

    @Override
    public NodeProperty createProperty(int value){
        PropertyAmong nextProperty = create(values.length, min, max, V);
        nextProperty.depth = depth + 1;
        nextProperty.n = first ? n-1 : n;
        for(int i = values.length - 1; i > 0; i--) nextProperty.values.set(i-1, values.get(i));
        nextProperty.values.set(values.length - 1, V.contains(value) ? 1 : 0);
        if(depth+1 >= values.length) nextProperty.first = nextProperty.values.get(0) == 1;
        nextProperty.n+=nextProperty.values.get(values.length - 1);
        return nextProperty;
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isValid(){
        if(depth < values.length) return n <= max;
        return min <= n && n <= max;
    }

    @Override
    public boolean isValid(int v, int layer, int size){
        int next = n;
        if(first) next--;
        if(V.contains(v)) next++;
        if(depth+1 < values.length) return next <= max;
        return min <= next && next <= max;
    }


    //**************************************//
    //           HASH FUNCTIONS             //
    //**************************************//
    // hash

    @Override
    public int hash(){
        return n + values.length * (first ? 1 : 0);
    }

    @Override
    public int hash(int value){
        int hash = n;
        if(first) hash--;
        if(V.contains(value)) hash++;
        if(V.contains(values.get(1))) hash += values.length;
        return hash;
    }

    @Override
    public String hashstr(int value){
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if(values.length > 0) {
            for (int i = 1; i < values.length; i++) {
                builder.append(values.get(i));
                builder.append(", ");
            }
            builder.append(V.contains(value) ? 1 : 0);
        }
        builder.append("]");
        return builder.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free() {
        Memory.free(values);
        this.V = null;
        super.free();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the PropertyAmong type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PropertyAmong> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected PropertyAmong[] arrayCreation(int capacity) {
            return new PropertyAmong[capacity];
        }

        @Override
        protected PropertyAmong createObject(int index) {
            return new PropertyAmong(index);
        }
    }


}
