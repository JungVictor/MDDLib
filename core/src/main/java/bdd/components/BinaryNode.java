package bdd.components;

import memory.Allocable;
import memory.AllocatorOf;
import memory.Memory;
import structures.lists.UnorderedListOfBinaryNode;

public class BinaryNode implements Allocable {

    private final static byte[] none = {},
                                zero = {0},
                                one = {1},
                                both = {0, 1};

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    // Associations
    private BinaryNode x1, x2;

    // Children
    private BinaryNode child0, child1;

    // Parents
    private UnorderedListOfBinaryNode parent0, parent1;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){ return localStorage.get(); }

    private BinaryNode(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public static BinaryNode create(){
        BinaryNode node = allocator().allocate();
        node.parent0 = UnorderedListOfBinaryNode.create();
        node.parent1 = UnorderedListOfBinaryNode.create();
        return node;
    }

    public BinaryNode Node(){
        return create();
    }

    public void associate(BinaryNode x1, BinaryNode x2){
        this.x1 = x1;
        this.x2 = x2;
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get the child corresponding to the given label
     * @param label The value of the label
     * @return The node associated with the given label
     */
    public BinaryNode getChild(int label){
        if(label == 0) return child0;
        return child1;
    }

    /**
     * Check if the node has a child corresponding to the given label
     * @param label Label of the arc to check
     * @return True if there is an arc associated to the given label, false otherwise.
     */
    public boolean containsLabel(int label){
        if(label == 0) return child0 != null;
        return child1 != null;
    }

    /**
     * The number of outgoing arcs
     * @return the number of outgoing arcs
     */
    public int numberOfChildren(){
        int children = 0;
        if (child0 != null) children++;
        if (child1 != null) children++;
        return children;
    }

    /**
     * Get the out-going labels
     * @return All out-going labels
     */
    public byte[] getChildren(){
        if (child0 != null) {
            if(child1 != null) return both;
            else return zero;
        } else {
            if(child1 != null) return one;
            else return none;
        }
    }

    /**
     * Get all parents corresponding to the given label
     * @param label The label of the in-going arcs
     * @return The UnorderedListOfBinaryNode of parents
     */
    public UnorderedListOfBinaryNode getParent(int label){
        if(label == 0) return parent0;
        return parent1;
    }

    /**
     * Get all parents corresponding to the label 0
     * @return The UnorderedListOfBinaryNode of parents with label = 0
     */
    public UnorderedListOfBinaryNode getParent0(){
        return parent0;
    }

    /**
     * Get all parents corresponding to the label 1
     * @return The UnorderedListOfBinaryNode of parents with label = 1
     */
    public UnorderedListOfBinaryNode getParent1(){
        return parent1;
    }

    /**
     * Get the first node associated
     * @return the first node associated
     */
    public BinaryNode getX1(){
        return x1;
    }

    /**
     * Get the second node associated
     * @return the second node associated
     */
    public BinaryNode getX2(){
        return x2;
    }


    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    /**
     * Add a child to this node with the given label
     * @param label Label of the arc
     * @param child Node to add as a child
     */
    public void addChild(int label, BinaryNode child){
        if(label == 0) child0 = child;
        else child1 = child;
    }

    /**
     * Remove the child corresponding to the given label
     * @param label Label of the outgoing arc
     */
    public void removeChild(int label){
        if(label == 0) child0 = null;
        else child1 = null;
    }

    /**
     * Add a parent to this node with the given label
     * @param label Label of the ingoing arc
     * @param parent Node to add as a parent
     */
    public void addParent(int label, BinaryNode parent){
        if(label == 0) parent0.add(parent);
        else parent1.add(parent);
    }

    /**
     * Remove the given node from the parents' list corresponding to the given arc's label
     * @param label Label of the ingoing arc
     * @param parent The parent node to remove
     */
    public void removeParent(int label, BinaryNode parent){
        if(label == 0) parent0.removeElement(parent);
        else parent1.removeElement(parent);
    }

    /**
     * Replace all parents' references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceParentsReferencesBy(BinaryNode node){
        for(int value = 0; value < 2; value++){
            for(BinaryNode parent : getParent(value)) {
                parent.addChild(value, node);
                node.addParent(value, parent);
            }
        }
        parent0.clear();
        parent1.clear();
    }

    /**
     * Replace all children's references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceChildrenReferencesBy(BinaryNode node){
        for(int value = 0; value < 2; value++){
            BinaryNode child = getChild(value);
            child.removeParent(value, this);
            child.addParent(value, node);
            node.addChild(value, child);
        }
        child0 = null;
        child1 = null;
    }

    /**
     * Replace all references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceReferencesBy(BinaryNode node){
        replaceChildrenReferencesBy(node);
        replaceParentsReferencesBy(node);
    }

    /**
     * Clear all parents references
     */
    public void clearParents(){
        parent0.clear();
        parent1.clear();
    }

    /**
     * Clear all children references
     */
    public void clearChildren(){
        child0 = null;
        child1 = null;
    }


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * Remove all references of this node from nodes referenced by this node
     */
    public void remove(){
        if(child0 != null) child0.removeParent(0, this);
        if(child1 != null) child1.removeParent(1, this);

        if(parent0.size() != 0) for (BinaryNode node : parent0) node.removeChild(0);
        if(parent1.size() != 1) for (BinaryNode node : parent1) node.removeChild(1);
    }

    /**
     * Remove all information of this node
     */
    public void clear(){
        clearParents();
        clearChildren();
        clearAssociations();
    }

    /**
     * Clear all associations of the node
     */
    public void clearAssociations(){
        x1 = null;
        x2 = null;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    @Override
    public int allocatedIndex(){ return allocatedIndex; }

    @Override
    public void free(){
        clearChildren();
        clearParents();
        Memory.free(parent0);
        Memory.free(parent1);
        allocator().free(this); // Free the object
    }


    static final class Allocator extends AllocatorOf<BinaryNode> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected BinaryNode[] arrayCreation(int capacity) {
            return new BinaryNode[capacity];
        }

        @Override
        protected BinaryNode createObject(int index) {
            return new BinaryNode(index);
        }
    }

}
