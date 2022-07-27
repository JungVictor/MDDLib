package dd.mdd.costmdd.components;

import dd.mdd.components.Node;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.MapOf;

import java.util.HashMap;

public class InArcs extends dd.mdd.components.InArcs {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private final HashMap<Integer, MapOf<Node, Integer>> costs = new HashMap<>();


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
    protected InArcs(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a InCostArcs.
     * The object is managed by the allocator.
     * @return A fresh InCostArcs
     */
    public static InArcs create(){
        return allocator().allocate();
    }


    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    /**
     * Associate a node with the given label
     * @param label Label of the arc
     * @param node Node to associate with the given label
     * @param cost Cost of the arc
     */
    public void add(int label, Node node, int cost){
        add(label, node);
        MapOf<Node, Integer> map = costs.get(label);
        if(map == null) {
            map = Memory.MapOfNodeInteger();
            costs.put(label, map);
        }
        costs.get(label).put(node, cost);
    }

    /**
     * Set the cost corresponding to the given arc
     * @param label Label of the arc
     * @param parent Parent node
     * @param cost The value of the cost
     */
    public void setCost(int label, Node parent, int cost){
        MapOf<Node, Integer> map = costs.get(label);
        if(map == null) {
            map = Memory.MapOfNodeInteger();
            costs.put(label, map);
        }
        map.put(parent, cost);
    }

    /**
     * Get the cost associated with the given arc
     * @param node The parent
     * @param label Label of the arc
     * @return the cost associated with the given arc
     */
    public int getCost(Node node, int label){
        return costs.get(label).get(node);
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    public void dealloc() {
        allocator().free(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        // Free all maps
        for(MapOf<Node, Integer> map : costs.values()) Memory.free(map);
        super.free();
    }

    /**
     * <b>The allocator that is in charge of the InCostArcs type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<InArcs> {

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
        protected InArcs[] arrayCreation(int capacity) {
            return new InArcs[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected InArcs createObject(int index) {
            return new InArcs(index);
        }
    }

}
