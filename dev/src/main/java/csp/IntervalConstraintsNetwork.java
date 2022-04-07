package csp;

import csp.constraint.IntervalConstraint;
import csp.structures.lists.ListOfIntervalVariable;
import memory.Allocable;
import memory.AllocatorOf;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class IntervalConstraintsNetwork implements Allocable {

    Queue<IntervalConstraint> constraintsToApply = new LinkedList<>(); //The queue of constraint to apply
    HashSet<IntervalConstraint> isInQueue = new HashSet<>(); //The set of constraint that are in the queue

    //**************************************//
    //       ALLOCATION AND CREATION        //
    //**************************************//

    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private final int allocatedIndex;

    /**
     * Constructor. Initialise the allocated index in the allocator.
     * @param allocatedIndex Allocated index in the allocator.
     */
    private IntervalConstraintsNetwork(int allocatedIndex){ this.allocatedIndex = allocatedIndex; }

    private static Allocator allocator(){ return localStorage.get(); }

    /**
     * Get an IntervalConstraintNetwork object from the allocator.
     * @return An IntervalConstraintNetwork
     */
    public static IntervalConstraintsNetwork create(){
        IntervalConstraintsNetwork constraintsNetwork = allocator().allocate();
        constraintsNetwork.prepare();
        return constraintsNetwork;
    }

    //**************************************//
    //               METHODS                //
    //**************************************//

    public void addConstraint(IntervalConstraint constraint){
        constraintsToApply.add(constraint);
        isInQueue.add(constraint);
    }

    private void propagate(ListOfIntervalVariable changedVariables){
        int numberOfVariables = changedVariables.size();
        int numberOfConstraints;
        IntervalVariable currentVariable; //The current variable for the loop with i
        IntervalConstraint currentConstraint; //The current constraint for the loop with j
        for (int i = 0; i < numberOfVariables; i++) {
            currentVariable = changedVariables.get(i);
            numberOfConstraints = currentVariable.numberOfConstraints();
            for (int j = 0; j < numberOfConstraints; j++) {
                currentConstraint = currentVariable.getConstraints(j);
                if (!isInQueue.contains(currentConstraint)){
                    addConstraint(currentConstraint);
                }
            }
        }
    }

    public void resolve(){
        IntervalConstraint constraint = constraintsToApply.poll();
        isInQueue.remove(constraint);
        ListOfIntervalVariable changedVariables;
        while (constraint != null){
            changedVariables = constraint.apply();
            propagate(changedVariables);
            changedVariables.free();
            constraint = constraintsToApply.poll();
            isInQueue.remove(constraint);
        }
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    private void prepare(){
        constraintsToApply.clear();
        isInQueue.clear();
    }

    @Override
    public int allocatedIndex(){ return allocatedIndex; }

    @Override
    public void free(){
        constraintsToApply.clear();
        isInQueue.clear();

        allocator().free(this); // Free the object
    }

    static final class Allocator extends AllocatorOf<IntervalConstraintsNetwork> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected IntervalConstraintsNetwork[] arrayCreation(int capacity) {
            return new IntervalConstraintsNetwork[capacity];
        }

        @Override
        protected IntervalConstraintsNetwork createObject(int index) {
            return new IntervalConstraintsNetwork(index);
        }
    }

}
