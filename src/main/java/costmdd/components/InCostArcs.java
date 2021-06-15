package costmdd.components;

import mdd.components.InArcs;
import mdd.components.Node;
import memory.Memory;
import memory.MemoryPool;
import structures.generics.MapOf;

import java.util.HashMap;

public class InCostArcs extends InArcs {

    private HashMap<Integer, HashMap<Node, Integer>> costs = new HashMap<>();

    public InCostArcs(MemoryPool<InArcs> pool) {
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

}
