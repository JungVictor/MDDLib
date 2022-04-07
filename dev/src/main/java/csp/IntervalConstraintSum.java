package csp;

import memory.AllocatorOf;
import structures.arrays.ArrayOfBoolean;
import structures.tuples.TupleOfLong;

public class IntervalConstraintSum  extends IntervalConstraint {

    private IntervalVariable a;
    private IntervalVariable x;
    private IntervalVariable y;

    //**************************************//
    //       ALLOCATION AND CREATION        //
    //**************************************//

    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    /**
     * Constructor. Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator.
     */
    private IntervalConstraintSum(int allocatedIndex){ super(allocatedIndex); }

    private static Allocator allocator(){ return localStorage.get(); }

    /**
     * Get an IntervalConstraintSum object from the allocator.
     * Constraint sum : a = x + y.
     * @param id The id of the constraint.
     * @param a An IntervalVariable.
     * @param x An IntervalVariable.
     * @param y An IntervalVariable.
     * @return An IntervalConstraintSum
     */
    public static IntervalConstraintSum create(int id, IntervalVariable a, IntervalVariable x, IntervalVariable y){
        IntervalConstraintSum constraint = allocator().allocate();
        constraint.prepare(3);
        constraint.init(id, a, x, y);
        return constraint;
    }

    /**
     * Initialisation of the IntervalConstraintSum.
     * @param id The id of the constraint.
     * @param a An IntervalVariable.
     * @param x An IntervalVariable.
     * @param y An IntervalVariable.
     */
    private void init(int id, IntervalVariable a, IntervalVariable x, IntervalVariable y){
        super.init(id);
        this.a = a;
        this.addVariable(a);
        this.x = x;
        this.addVariable(x);
        this.y = y;
        this.addVariable(y);
    }

    //**************************************//
    //               METHODS                //
    //**************************************//

    /**
     * Apply the IntervalConstraintSum to filter the intervals of the concerned IntervalVariable objects.
     * @return An array of boolean indicating which IntervalVariable objects get their interval changed by the filtering.
     */
    public ArrayOfBoolean apply(){
        ArrayOfBoolean change = ArrayOfBoolean.create(this.numberOfVariables());
        change.set(0, a.intersect(x.getMin() + y.getMin(), x.getMax() + y.getMax()));
        change.set(1, x.intersect(a.getMin() - y.getMax(), a.getMax() - y.getMin()));
        change.set(2, y.intersect(a.getMin() - x.getMax(), a.getMax() - x.getMin()));
        return change;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    public void free(){
        super.free();
        a = null;
        x = null;
        y = null;
        allocator().free(this);
    }

    static final class Allocator extends AllocatorOf<IntervalConstraintSum> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected IntervalConstraintSum[] arrayCreation(int capacity) {
            return new IntervalConstraintSum[capacity];
        }

        @Override
        protected IntervalConstraintSum createObject(int index) {
            return new IntervalConstraintSum(index);
        }
    }

}
