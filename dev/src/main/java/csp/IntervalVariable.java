package csp;

import csp.constraints.IntervalConstraint;
import csp.structures.lists.ListOfIntervalConstraint;
import memory.Allocable;
import memory.AllocatorOf;
import structures.tuples.TupleOfLong;

public class IntervalVariable implements Allocable {

    private TupleOfLong interval;   //The interval of value the variable can take
    private ListOfIntervalConstraint constraints; //The list of constraint involving the variable

    //**************************************//
    //       ALLOCATION AND CREATION        //
    //**************************************//

    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private final int allocatedIndex;

    /**
     * Constructor. Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator.
     */
    private IntervalVariable(int allocatedIndex){ this.allocatedIndex = allocatedIndex; }

    private static Allocator allocator(){ return localStorage.get(); }

    /**
     * Get an IntervalVariable object from the allocator.
     * @param lb The lower bound.
     * @param ub The upper bound.
     * @return An IntervalVariable.
     */
    public static IntervalVariable create(long lb, long ub){
        if (lb > ub){
            throw new IllegalArgumentException("The lower bound can not be greater than the upper bound. lb = "+lb+", ub = "+ub);
        }
        IntervalVariable var = allocator().allocate();
        var.prepare();
        var.interval.set(lb, ub);
        return var;
    }

    //**************************************//
    //               METHODS                //
    //**************************************//

    @Override
    public String toString(){
        return interval.toString();
    }

    public void setInterval(long lb, long ub){
        interval.set(lb, ub);
    }

    public void setInterval(TupleOfLong interval){
        this.setInterval(interval.getFirst(), interval.getSecond());
    }

    public void setMin(long lb){
        interval.setFirst(lb);
    }

    public void setMax(long ub){
        interval.setSecond(ub);
    }

    public long getMin(){
        return interval.getFirst();
    }

    public long getMax(){
        return interval.getSecond();
    }

    public IntervalConstraint getConstraints(int i) {
        return constraints.get(i);
    }

    /**
     * @return The number of IntervalConstraint involving the IntervalVariable.
     */
    public int numberOfConstraints(){
        return constraints.size();
    }

    /**
     * Add the IntervalConstraint to the list of IntervalConstraint involving the IntervalVariable.<br>
     * <b>/!\ Adding an IntervalConstraint that does not involve the IntervalVariable will result in
     * a loss of time computation for the CSP resolution.</b>
     * @param constraint An IntervalConstraint involving the IntervalVariable
     */
    public void addConstraint(IntervalConstraint constraint){
        constraints.add(constraint);
    }

    /**
     * Intersects the IntervalVariable with an interval.
     * @param lb The lower bound of the interval.
     * @param ub The upper bound of the interval.
     * @return A boolean indicating if the intersection changer anything.
     */
    public boolean intersect(long lb, long ub){
        boolean res = false;
        if (lb > interval.getFirst()){
            res = true;
            interval.setFirst(lb);
        }
        if (ub < interval.getSecond()){
            res = true;
            interval.setSecond(ub);
        }
        if (interval.getFirst() > interval.getSecond()){
            throw new IllegalArgumentException("The constraint can not be satisfied");
        }
        return res;
    }

    /**
     * Intersects the IntervalVariable with an interval.
     * @param interval An interval.
     * @return A boolean indicating if the intersection changer anything.
     */
    public boolean intersect(TupleOfLong interval){
        return this.intersect(interval.getFirst(), interval.getSecond());
    }

    /**
     * Intersects the IntervalVariable with an interval [<b>threshold</b>, +INFINITY].
     * @param threshold A value.
     * @return A boolean indicating if the intersection changer anything.
     */
    public boolean intersectGeq(long threshold){
        return this.intersect(threshold, this.getMax());
    }

    /**
     * Intersects the IntervalVariable with an interval [-INFINITY, <b>threshold</b>].
     * @param threshold A value.
     * @return A boolean indicating if the intersection changer anything.
     */
    public boolean intersectLeq(long threshold){
        return this.intersect(this.getMin(), threshold);
    }
    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    /**
     * Prepare the object by allocating its attributes.
     */
    private void prepare(){
        interval = TupleOfLong.create();
        constraints = ListOfIntervalConstraint.create();
    }

    @Override
    public int allocatedIndex(){ return allocatedIndex; }

    @Override
    public void free(){
        interval.free();
        constraints.free();
        allocator().free(this); // Free the object
    }

    static final class Allocator extends AllocatorOf<IntervalVariable> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected IntervalVariable[] arrayCreation(int capacity) {
            return new IntervalVariable[capacity];
        }

        @Override
        protected IntervalVariable createObject(int index) {
            return new IntervalVariable(index);
        }
    }


}
