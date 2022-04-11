package csp.constraints;

import csp.IntervalVariable;
import csp.structures.arrays.ArrayOfIntervalVariable;
import csp.structures.lists.ListOfIntervalVariable;
import dd.operations.Stochastic;
import memory.AllocatorOf;
import structures.StochasticVariable;
import structures.arrays.ArrayOfLong;

public class IntervalConstraintKnapsack extends IntervalConstraint {

    private ArrayOfIntervalVariable quantities;
    private ArrayOfIntervalVariable costs;
    private long threshold;
    private long maxQuantity;
    private int precision;

    //**************************************//
    //       ALLOCATION AND CREATION        //
    //**************************************//

    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    /**
     * Constructor. Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator.
     */
    private IntervalConstraintKnapsack(int allocatedIndex){ super(allocatedIndex); }

    private static Allocator allocator(){ return localStorage.get(); }

    /**
     * Get an IntervalConstraintKnapsack object from the allocator.<br>
     * Constraint mochila : (sum of all quantities[i] * costs[i]) >= threshold subjet to <br>
     * (sum of all quantities[i]) <= maxQuantity. <br>
     * The <b>precision</b> helps to apply this constraint on floating-point numbers<br>
     * represented by integers : an integer <b>x</b> with the precision <b>p</b> represents <br>
     * the floating-point number <b>x</b> * 10^(-<b>p</b>)
     * @param quantities An array of IntervalVariable.
     * @param costs An array of IntervalVariable.
     * @param threshold The threshold to satisfy.
     * @param maxQuantity The maximum quantity available.
     * @param precision The number of decimal digit (after the decimal separator).
     * @return An IntervalConstraintKnapsack
     */
    public static IntervalConstraintKnapsack create(ArrayOfIntervalVariable quantities, ArrayOfIntervalVariable costs, long threshold, long maxQuantity, int precision){
        IntervalConstraintKnapsack constraint = allocator().allocate();
        constraint.prepare(quantities.length() + costs.length());
        constraint.init(quantities, costs, threshold, maxQuantity, precision);
        return constraint;
    }

    /**
     * Initialisation of the IntervalConstraintKnapsack.
     * @param quantities An array of IntervalVariable.
     * @param costs An array of IntervalVariable.
     * @param threshold The threshold to satisfy.
     * @param maxQuantity The maximum quantity available.
     * @param precision The number of decimal digit (after the decimal separator).
     */
    protected void init(ArrayOfIntervalVariable quantities, ArrayOfIntervalVariable costs, long threshold, long maxQuantity, int precision){
        super.init();
        if(quantities.length() != costs.length()){
            throw new IllegalArgumentException("The array quantities and costs must have the same size. Size of quantities = "+quantities.length()+", size of costs = "+costs.length());
        }
        this.quantities = quantities;
        for (int i = 0; i < quantities.length(); i++) {
            this.addVariable(quantities.get(i));
        }
        this.costs = costs;
        for (int i = 0; i < costs.length; i++) {
            this.addVariable(costs.get(i));
        }
        this.threshold = threshold;
        this.maxQuantity = maxQuantity;
        this.precision = precision;
    }

    //**************************************//
    //               METHODS                //
    //**************************************//

    /**
     * Apply the IntervalConstraintKnapsack to filter the intervals of the concerned IntervalVariable objects.
     * @return An array of boolean indicating which IntervalVariable objects get their interval changed by the filtering.
     */
    public ListOfIntervalVariable apply(){
        ListOfIntervalVariable changedVariables = ListOfIntervalVariable.create();
        boolean change;
        int size = quantities.length();

        //Creation of the StochasticVariable
        StochasticVariable[] stochasticVariables = new StochasticVariable[size];
        StochasticVariable current;
        for (int i = 0; i < size; i++) {
            current = StochasticVariable.create(precision);
            current.setQuantity(quantities.get(i).getMin(), quantities.get(i).getMax());
            current.setValue(costs.get(i).getMin(), costs.get(i).getMax());
            stochasticVariables[i] = current;
        }

        //Filtering
        long[][] qBounds = Stochastic.computeBounds(stochasticVariables, threshold, maxQuantity, precision);
        for(int i = 0; i < stochasticVariables.length; i++) stochasticVariables[i].setQuantity(qBounds[i][0], qBounds[i][1]);

        ArrayOfLong minBounds = Stochastic.minCostFilteringPolynomialV2(stochasticVariables, threshold, maxQuantity, precision);

        //Intersection with the old intervals
        for (int i = 0; i < size; i++) {
            change = quantities.get(i).intersect(qBounds[i][0], qBounds[i][1]);
            if (change) changedVariables.add(quantities.get(i));
        }

        for (int i = 0; i < size; i++) {
            change = costs.get(i).intersectGeq(minBounds.get(i));
            if (change) changedVariables.add(costs.get(i));
        }
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

    static final class Allocator extends AllocatorOf<IntervalConstraintKnapsack> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected IntervalConstraintKnapsack[] arrayCreation(int capacity) {
            return new IntervalConstraintKnapsack[capacity];
        }

        @Override
        protected IntervalConstraintKnapsack createObject(int index) {
            return new IntervalConstraintKnapsack(index);
        }
    }
}
