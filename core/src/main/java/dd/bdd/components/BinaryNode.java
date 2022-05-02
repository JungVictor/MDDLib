package dd.bdd.components;

import dd.AbstractNode;
import dd.interfaces.INode;
import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfNodeInterface;
import structures.arrays.ArrayOfInt;
import structures.lists.AbstractUnorderedListOfNodes;
import structures.lists.UnorderedListOfBinaryNode;
import structures.successions.SuccessionOfBinaryNode;

public class BinaryNode extends AbstractNode {

    public final static ArrayOfInt NONE = ArrayOfInt.create(0),
            ZERO = ArrayOfInt.create(true, 0),
            ONE = ArrayOfInt.create(true, 1),
            BOTH = ArrayOfInt.create(true, 0, 1);

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    // Associations
    private SuccessionOfBinaryNode associations;

    // Children
    private BinaryNode child0, child1;

    // Parents
    private UnorderedListOfBinaryNode parent0, parent1;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){ return localStorage.get(); }

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    public BinaryNode(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void prepare(){
        parent0 = UnorderedListOfBinaryNode.create();
        parent1 = UnorderedListOfBinaryNode.create();
        associations = SuccessionOfBinaryNode.create(2);
    }

    /**
     * Create a BinaryNode.
     * The object is managed by the allocator.
     * @return A fresh BinaryNode
     */
    public static BinaryNode create(){
        BinaryNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    /**
     * Create a BinaryNode of the same type as this node.
     * The object is managed by the allocator.
     * @return A fresh BinaryNode
     */
    public BinaryNode Node(){
        return create();
    }

    /**
     * Associate nodes to this node
     * @param x1 The first node
     * @param x2 The second node
     */
    public void associate(BinaryNode x1, BinaryNode x2){
        this.associations.set(0, x1);
        this.associations.set(1, x2);
    }

    /**
     * Set the ith associated node to node
     * @param node The node to associate
     * @param i The index of the association
     */
    public void setX(BinaryNode node, int i){
        this.associations.set(i, node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associate(INode x1, INode x2){
        associate((BinaryNode) x1, (BinaryNode) x2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associate(ArrayOfNodeInterface associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length(); i++) this.associations.set(i, (BinaryNode) associations.get(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessionOfBinaryNode getAssociations() {
        return associations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setX(INode node, int i){
        setX((BinaryNode) node, i);
    }

    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get the out-going labels
     * @return All out-going labels
     */
    public ArrayOfInt getChildren(){
        if (child0 != null) {
            if(child1 != null) return BOTH;
            else return ZERO;
        } else {
            if(child1 != null) return ONE;
            else return NONE;
        }
    }

    /**
     * Get the in-going labels
     * @return All in-going labels
     */
    public ArrayOfInt getParents(){
        if (parent0.size() > 0) {
            if(parent1.size() > 0) return BOTH;
            else return ZERO;
        } else {
            if(parent1.size() > 0) return ONE;
            else return NONE;
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
     * {@inheritDoc}
     */
    @Override
    public BinaryNode getChild(int label){
        if(label == 0) return child0;
        return child1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numberOfChildren(){
        int children = 0;
        if (child0 != null) children++;
        if (child1 != null) children++;
        return children;
    }

    @Override
    public int numberOfParentsLabel() {
        int parents = 0;
        if (parent0 != null) parents++;
        if (parent1 != null) parents++;
        return parents;
    }

    @Override
    public int numberOfParents(int label) {
        if(label == 0 && parent0 != null) return parent0.size();
        if(label == 1 && parent1 != null) return parent1.size();
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayOfInt iterateOnChildLabels(){
        return getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayOfInt iterateOnParentLabels(){
        return getParents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractUnorderedListOfNodes iterateOnParents(int label){
        if(label == 0) return parent0;
        return parent1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryNode getX(int i){
        return this.associations.get(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryNode getX1(){
        return getX(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryNode getX2(){
        return getX(1);
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
            if(child == null) continue;
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
     * {@inheritDoc}
     */
    @Override
    public void addChild(int label, INode child){
        addChild(label, (BinaryNode) child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addParent(int label, INode parent){
        addParent(label, (BinaryNode) parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChild(int label){
        if(label == 0) child0 = null;
        else child1 = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeParent(int label, INode parent){
        removeParent(label, (BinaryNode) parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearParents(){
        parent0.clear();
        parent1.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearChildren(){
        child0 = null;
        child1 = null;
    }


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAssociations(){
        this.associations.clear();
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    /**
     * {@inheritDoc}
     */
    @Override
    public int allocatedIndex(){ return allocatedIndex; }


    /**
     * Call the allocator to free this object
     */
    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        clearChildren();
        clearParents();
        Memory.free(parent0);
        Memory.free(parent1);
        dealloc();
    }


    static final class Allocator extends AllocatorOf<BinaryNode> {

        // You can specify the initial capacity. Default : 10.
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
        protected BinaryNode[] arrayCreation(int capacity) {
            return new BinaryNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected BinaryNode createObject(int index) {
            return new BinaryNode(index);
        }
    }

}
