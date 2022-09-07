package dd;

import dd.interfaces.INode;
import memory.Allocable;
import structures.arrays.ArrayOfNodeInterface;
import structures.successions.SuccessionOfNodeInterface;

public abstract class AbstractNode implements Allocable, INode {

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get a new node corresponding to the same type as this node
     * @return A new node corresponding to the same type as this node
     */
    public abstract INode Node();

    /**
     * Set the ith associated node to node
     * @param node The node to associate
     * @param i The index of the association
     */
    public abstract void setX(INode node, int i);

    /**
     * Set the first associated node to x1
     * @param x1 The node to associate
     */
    public void setX1(INode x1){ setX(x1,0); }

    /**
     * Set the second associated node to x2
     * @param x2 The node to associate
     */
    public void setX2(INode x2){ setX(x2,1); }

    /**
     * Associate the node to all given nodes
     * @param nodes The associations
     */
    public abstract void associate(ArrayOfNodeInterface nodes);

    /**
     * Set the first and second associated nodes to x1 and x2
     * @param x1 The first node associated
     * @param x2 The second node associated
     */
    public abstract void associate(INode x1, INode x2);

    /**
     * Get the set of all associated nodes
     * @return The set of all associated nodes
     */
    public abstract SuccessionOfNodeInterface getAssociations();


    //**************************************//
    //               GETTERS                //
    //**************************************//

    // ---------------------
    //      ABSTRACT

    /**
     * Get the child corresponding to the given label
     * @param label The value of the label
     * @return The node associated with the given label
     */
    public abstract INode getChild(int label);

    /**
     * The number of outgoing arcs
     * @return the number of outgoing arcs
     */
    public abstract int numberOfChildren();

    /**
     * The number of ingoing arcs labels
     * @return the number of ingoing arcs labels
     */
    public abstract int numberOfParentsLabel();


    /**
     * The number of ingoing arcs with given label
     * @return the number of ingoing with given label
     */
    public abstract int numberOfParents(int label);


    /**
     * {@inheritDoc}
     */
    public int numberOfParents(){
        int cpt = 0;
        for(int v : iterateOnParentLabels()) cpt += numberOfParents(v);
        return cpt;
    }

    /**
     * Get the out-going labels
     * @return All out-going labels
     */
    public abstract Iterable<Integer> iterateOnChildLabels();

    /**
     * Get the in-going labels
     * @return All in-going labels
     */
    public abstract Iterable<Integer> iterateOnParentLabels();

    /**
     * Get all parents corresponding to the given label
     * @param label The label of the in-going arcs
     * @return The UnorderedListOfBinaryNode of parents
     */
    public abstract Iterable<INode> iterateOnParents(int label);

    /**
     * Get the ith node associated with this node.
     * @param i The index of the associated node
     * @return The ith node associated with this node, null if there is none
     */
    public abstract INode getX(int i);

    // ---------------------
    //      DEFAULT

    /**
     * Check if the node has a child corresponding to the given label
     * @param label Label of the arc to check
     * @return True if there is an arc associated to the given label, false otherwise.
     */
    public boolean containsLabel(int label){
        return getChild(label) != null;
    }

    /**
     * Get the first node associated
     * @return the first node associated
     */
    public INode getX1() {
        return getX(0);
    }

    /**
     * Get the second node associated
     * @return the second node associated
     */
    public INode getX2(){
        return getX(1);
    }


    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    // ---------------------
    //      ABSTRACT

    /**
     * Add a child to this node with the given label
     * @param label Label of the arc
     * @param child Node to add as a child
     */
    public abstract void addChild(int label, INode child);

    /**
     * Remove the child corresponding to the given label
     * @param label Label of the outgoing arc
     */
    public abstract void removeChild(int label);

    /**
     * Add a parent to this node with the given label
     * @param label Label of the ingoing arc
     * @param parent Node to add as a parent
     */
    public abstract void addParent(int label, INode parent);

    /**
     * Remove the given node from the parents' list corresponding to the given arc's label
     * @param label Label of the ingoing arc
     * @param parent The parent node to remove
     */
    public abstract void removeParent(int label, INode parent);

    /**
     * Clear all parents references
     */
    public abstract void clearParents();

    /**
     * Clear all children references
     */
    public abstract void clearChildren();

    // ---------------------
    //      DEFAULT

    /**
     * Replace all parents' references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceParentsReferencesBy(INode node){
        for(int value : iterateOnParentLabels()){
            for(INode parent : iterateOnParents(value)) {
                parent.addChild(value, node);
                node.addParent(value, parent);
            }
        }
        clearParents();
    }

    /**
     * Replace all children's references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceChildrenReferencesBy(INode node){
        for(int value : iterateOnChildLabels()){
            INode child = getChild(value);
            child.removeParent(value, this);
            child.addParent(value, node);
            node.addChild(value, child);
        }
        clearChildren();
    }

    /**
     * Replace all references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceReferencesBy(INode node){
        replaceChildrenReferencesBy(node);
        replaceParentsReferencesBy(node);
    }


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * Clear all associations of the node
     */
    public abstract void clearAssociations();

    /**
     * Remove all references of this node from nodes referenced by this node
     */
    public void remove(){
        for(int value : iterateOnChildLabels()) getChild(value).removeParent(value, this);
        for(int value : iterateOnParentLabels()) for(INode node : iterateOnParents(value)) node.removeChild(value);
    }

    /**
     * Remove all information of this node
     */
    public void clear(){
        clearParents();
        clearChildren();
        clearAssociations();
    }

}
