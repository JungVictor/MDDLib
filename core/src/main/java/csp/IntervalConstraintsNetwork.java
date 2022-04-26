package csp;

import csp.constraints.IntervalConstraint;
import structures.lists.ListOfIntervalConstraint;
import structures.lists.ListOfIntervalVariable;
import memory.Allocable;
import memory.AllocatorOf;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class IntervalConstraintsNetwork implements Allocable {


    ListOfIntervalConstraint constraints = ListOfIntervalConstraint.create(); //The constraint concerned by the constraints network
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

    /**
     * Add a constraint to the constraint network.
     * @param constraint The IntervalConstraint to add.
     */
    public void addConstraint(IntervalConstraint constraint){
        constraints.add(constraint);
    }

    private void addToQueue(IntervalConstraint constraint){
        constraintsToApply.add(constraint);
        isInQueue.add(constraint);
    }

    /**
     * Propagate the changes in the constraint network.
     * @param changedVariables The variables that changed during the resolution
     */
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
                    addToQueue(currentConstraint);
                }
            }
        }
    }

    /**
     * Run the resolution of the constraint network
     */
    public boolean resolve(){
        boolean res = false;

        for (int i = 0; i < constraints.size(); i++) {
            addToQueue(constraints.get(i));
        }

        IntervalConstraint constraint = constraintsToApply.poll();
        isInQueue.remove(constraint);
        ListOfIntervalVariable changedVariables;
        //int count = 0;
        while (constraint != null){
            //count++;
            //System.out.println("Count : "+count);
            changedVariables = constraint.apply();
            if (changedVariables.size() > 0){
                res = true;
            }
            propagate(changedVariables);
            changedVariables.free();
            constraint = constraintsToApply.poll();
            isInQueue.remove(constraint);
        }
        return res;
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
