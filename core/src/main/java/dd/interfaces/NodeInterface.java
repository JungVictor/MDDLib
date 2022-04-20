package dd.interfaces;

import memory.Freeable;
import structures.arrays.ArrayOfNodeInterface;
import structures.successions.SuccessionOfNodeInterface;

public interface NodeInterface extends Freeable {

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get a new node corresponding to the same type as this node
     *
     * @return A new node corresponding to the same type as this node
     */
    NodeInterface Node();

    /**
     * Set the ith associated node to node
     *
     * @param node The node to associate
     * @param i    The index of the association
     */
    void setX(NodeInterface node, int i);

    /**
     * Set the first associated node to x1
     *
     * @param x1 The node to associate
     */
    void setX1(NodeInterface x1);

    /**
     * Set the second associated node to x2
     *
     * @param x2 The node to associate
     */
    void setX2(NodeInterface x2);

    /**
     * Associate the node to all given nodes
     *
     * @param nodes The associations
     */
    void associate(ArrayOfNodeInterface nodes);

    /**
     * Set the first and second associated nodes to x1 and x2
     *
     * @param x1 The first node associated
     * @param x2 The second node associated
     */
    void associate(NodeInterface x1, NodeInterface x2);

    /**
     * Get the set of all associated nodes
     *
     * @return The set of all associated nodes
     */
    SuccessionOfNodeInterface getAssociations();


    //**************************************//
    //               GETTERS                //
    //**************************************//

    // ---------------------
    //      ABSTRACT

    /**
     * Get the child corresponding to the given label
     *
     * @param label The value of the label
     * @return The node associated with the given label
     */
    NodeInterface getChild(int label);

    /**
     * The number of outgoing arcs
     *
     * @return the number of outgoing arcs
     */
    int numberOfChildren();

    /**
     * The number of ingoing arcs labels
     *
     * @return the number of ingoing arcs labels
     */
    int numberOfParentsLabel();


    /**
     * The number of ingoing arcs with given label
     *
     * @return the number of ingoing with given label
     */
    int numberOfParents(int label);

    /**
     * Get the out-going labels
     *
     * @return All out-going labels
     */
    Iterable<Integer> iterateOnChildLabels();

    /**
     * Get the in-going labels
     *
     * @return All in-going labels
     */
    Iterable<Integer> iterateOnParentLabels();

    /**
     * Get all parents corresponding to the given label
     *
     * @param label The label of the in-going arcs
     * @return The UnorderedListOfBinaryNode of parents
     */
    Iterable<NodeInterface> iterateOnParents(int label);

    /**
     * Get the ith node associated with this node.
     *
     * @param i The index of the associated node
     * @return The ith node associated with this node, null if there is none
     */
    NodeInterface getX(int i);

    // ---------------------
    //      DEFAULT

    /**
     * Check if the node has a child corresponding to the given label
     *
     * @param label Label of the arc to check
     * @return True if there is an arc associated to the given label, false otherwise.
     */
    boolean containsLabel(int label);

    /**
     * Get the first node associated
     *
     * @return the first node associated
     */
    NodeInterface getX1();

    /**
     * Get the second node associated
     *
     * @return the second node associated
     */
    NodeInterface getX2();


    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    // ---------------------
    //      ABSTRACT

    /**
     * Add a child to this node with the given label
     *
     * @param label Label of the arc
     * @param child Node to add as a child
     */
    void addChild(int label, NodeInterface child);

    /**
     * Remove the child corresponding to the given label
     *
     * @param label Label of the outgoing arc
     */
    void removeChild(int label);

    /**
     * Add a parent to this node with the given label
     *
     * @param label  Label of the ingoing arc
     * @param parent Node to add as a parent
     */
    void addParent(int label, NodeInterface parent);

    /**
     * Remove the given node from the parents' list corresponding to the given arc's label
     *
     * @param label  Label of the ingoing arc
     * @param parent The parent node to remove
     */
    void removeParent(int label, NodeInterface parent);

    /**
     * Clear all parents references
     */
    void clearParents();

    /**
     * Clear all children references
     */
    void clearChildren();

    // ---------------------
    //      DEFAULT

    /**
     * Replace all parents' references of this node by the given node
     *
     * @param node Node to replace this node
     */
    void replaceParentsReferencesBy(NodeInterface node);

    /**
     * Replace all children's references of this node by the given node
     *
     * @param node Node to replace this node
     */
    void replaceChildrenReferencesBy(NodeInterface node);

    /**
     * Replace all references of this node by the given node
     *
     * @param node Node to replace this node
     */
    void replaceReferencesBy(NodeInterface node);


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * Clear all associations of the node
     */
    void clearAssociations();

    /**
     * Remove all references of this node from nodes referenced by this node
     */
    void remove();

    /**
     * Remove all information of this node
     */
    void clear();
}