package builder.constraints.parameters;

import memory.Allocable;
import memory.AllocatorOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.tuples.TupleOfInt;

import java.util.Set;

public class ParametersGCC extends ConstraintParameters {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    // Not to free
    private MapOf<Integer, TupleOfInt> gcc;
    private int minimum;


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

    public ParametersGCC(int allocatedIndex){
        super(allocatedIndex);
    }

    public void init(MapOf<Integer, TupleOfInt> gcc, SetOf<Integer> variables){
        this.gcc = gcc;
        this.minimum = 0;
        for(TupleOfInt tuple : gcc.values()) minimum += tuple.getFirst();
        super.setVariables(variables);
    }

    public static ParametersGCC create(MapOf<Integer, TupleOfInt> gcc, SetOf<Integer> variables){
        ParametersGCC object = allocator().allocate();
        object.init(gcc, variables);
        return object;
    }

    //**************************************//

    public boolean contains(int label){
        return gcc.contains(label);
    }

    public int min(int label){
        return gcc.get(label).getFirst();
    }

    public int max(int label){
        return gcc.get(label).getSecond();
    }

    public int minimum(){return minimum;}

    public Set<Integer> V(){return gcc.keySet();}


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the ParametersGCC type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersGCC> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersGCC[] arrayCreation(int capacity) {
            return new ParametersGCC[capacity];
        }

        @Override
        protected ParametersGCC createObject(int index) {
            return new ParametersGCC(index);
        }
    }

}
