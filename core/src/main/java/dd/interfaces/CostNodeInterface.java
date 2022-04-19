package dd.interfaces;

import dd.mdd.components.Node;

public interface CostNodeInterface extends NodeInterface {

    /**
     * Set the cost to the arc corresponding to the given label
     * @param label Label of the arc
     * @param cost Cost of the arc
     */
    void setArcCost(int label, int cost);

    /**
     * Add a child to this node with the given label
     * @param label Label of the arc
     * @param child Node to add as a child
     * @param cost Cost of the arc
     */
    void addChild(int label, Node child, int cost);

    /**
     * Add a parent to this node with the given label
     * @param label Label of the ingoing arc
     * @param parent Node to add as a parent
     * @param cost Cost of the arc
     */
    void addParent(int label, Node parent, int cost);


}
