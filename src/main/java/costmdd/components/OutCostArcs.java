package costmdd.components;

import mdd.components.Node;
import mdd.components.OutArcs;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.MapOf;

import java.util.HashMap;

public class OutCostArcs extends OutArcs {

    private MapOf<Integer, Integer> costs = Memory.MapOfIntegerInteger();

    public OutCostArcs(MemoryPool<OutArcs> pool) {
        super(pool);
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

}
