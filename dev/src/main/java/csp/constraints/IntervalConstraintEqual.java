package csp.constraints;

import csp.IntervalVariable;
import csp.structures.lists.ListOfIntervalVariable;
import memory.AllocatorOf;

public class IntervalConstraintEqual extends IntervalConstraint {

    private IntervalVariable a;
    private IntervalVariable b;

    //**************************************//
    //       ALLOCATION AND CREATION        //
    //**************************************//

    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    /**
     * Constructor. Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator.
     */
    private IntervalConstraintEqual(int allocatedIndex){ super(allocatedIndex); }

    private static Allocator allocator(){ return localStorage.get(); }

    /**
     * Get an IntervalConstraintEqual object from the allocator.
     * Constraint equal : a = b.
     * @param a An IntervalVariable.
     * @param b An IntervalVariable.
     * @return An IntervalConstraintEqual
     */
    public static IntervalConstraintEqual create(IntervalVariable a, IntervalVariable b){
        IntervalConstraintEqual constraint = allocator().allocate();
        constraint.prepare(2);
        constraint.init(a, b);
        return constraint;
    }

    /**
     * Initialisation of the IntervalConstraintEqual.
     * @param a An IntervalVariable.
     * @param b An IntervalVariable.
     */
    protected void init(IntervalVariable a, IntervalVariable b){
        super.init();
        this.a = a;
        this.addVariable(a);
        this.b = b;
        this.addVariable(b);
    }

    //**************************************//
    //               METHODS                //
    //**************************************//

    /**
     * Apply the IntervalConstraintEqual to filter the intervals of the concerned IntervalVariable objects.
     * @return An array of boolean indicating which IntervalVariable objects get their interval changed by the filtering.
     */
    public ListOfIntervalVariable apply(){
        ListOfIntervalVariable changedVariables = ListOfIntervalVariable.create();
        boolean change;
        change = a.intersect(b.getMin(), b.getMax());
        if (change) changedVariables.add(a);
        change = b.intersect(a.getMin(), a.getMax());
        if (change) changedVariables.add(b);
        return changedVariables;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    public void free(){
        super.free();
        allocator().free(this);
    }

    static final class Allocator extends AllocatorOf<IntervalConstraintEqual> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected IntervalConstraintEqual[] arrayCreation(int capacity) {
            return new IntervalConstraintEqual[capacity];
        }

        @Override
        protected IntervalConstraintEqual createObject(int index) {
            return new IntervalConstraintEqual(index);
        }
    }
}
