package costmdd.components;

import mdd.components.InArcs;
import mdd.components.Node;
import memory.AllocatorOf;
import memory.MemoryPool;

import java.util.HashMap;

public class InCostArcs extends InArcs {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private final HashMap<Integer, HashMap<Node, Integer>> costs = new HashMap<>();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static InCostArcs create(){
        return allocator().allocate();
    }
    protected InCostArcs(int allocatedIndex) {
        super(allocatedIndex);
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
        costs.get(label).put(node, cost);
    }

    /**
     * Get the cost associated with the given arc
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
     * <b>The allocator that is in charge of the InCostArcs type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<InCostArcs> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected InCostArcs[] arrayCreation(int capacity) {
            return new InCostArcs[capacity];
        }

        @Override
        protected InCostArcs createObject(int index) {
            return new InCostArcs(index);
        }
    }

}
