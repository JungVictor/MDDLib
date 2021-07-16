package costmdd.components;

import mdd.components.Node;
import mdd.components.OutArcs;
import memory.AllocatorOf;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.MapOf;

public class OutCostArcs extends OutArcs {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private final MapOf<Integer, Integer> costs = Memory.MapOfIntegerInteger();

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static OutCostArcs create(){
        return allocator().allocate();
    }

    protected OutCostArcs(int allocatedIndex) {
        super(allocatedIndex);
    }


    /**
     * Associate a node with the given label
     * @param label Label of the arc
     * @param node Node to associate with the given label
     * @param cost Cost of the arc
     */
    public void add(int label, Node node, int cost){
        add(label, node);
        costs.put(label, cost);
    }

    /**
     * Get the cost associated with the given arc
     * @param label Label of the arc
     * @return the cost associated with the given arc
     */
    public int getCost(int label){
        return costs.get(label);
    }

    @Override
    public void dealloc(){
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the OutCostArcs type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<OutCostArcs> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected OutCostArcs[] arrayCreation(int capacity) {
            return new OutCostArcs[capacity];
        }

        @Override
        protected OutCostArcs createObject(int index) {
            return new OutCostArcs(index);
        }
    }

}
