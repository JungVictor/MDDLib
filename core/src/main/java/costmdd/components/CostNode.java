package costmdd.components;

import mdd.components.Node;
import memory.Memory;
import memory.MemoryPool;

public class CostNode extends Node {

    public CostNode(MemoryPool<Node> pool) {
        super(pool);
    }

    /**
     * Add a child to this node with the given label
     * @param label Label of the arc
     * @param child Node to add as a child
     * @param cost Cost of the arc
     */
    public void addChild(int label, Node child, int cost){
        ((OutCostArcs) getChildren()).add(label, child, cost);
    }

    /**
     * Add a parent to this node with the given label
     * @param label Label of the ingoing arc
     * @param parent Node to add as a parent
     * @param cost Cost of the arc
     */
    public void addParent(int label, Node parent, int cost){
        ((InCostArcs) getParents()).add(label, parent, cost);
    }

    @Override
    protected void allocateArcs(){
        setChildren(Memory.OutCostArcs());
        setParents(Memory.InCostArcs());
    }

}
