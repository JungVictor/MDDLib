package dd.mdd.nondeterministic.components;


import dd.AbstractNode;
import dd.interfaces.INode;
import dd.mdd.components.InArcs;
import dd.mdd.components.Node;
import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfNodeInterface;
import structures.generics.SetOfNode;
import structures.successions.SuccessionOfAbstractNode;
import structures.successions.SuccessionOfNode;
import structures.successions.SuccessionOfNodeInterface;

public class NDNode extends AbstractNode {

    /**
     * Only thing changing between NonDeterministicNode and Node is that multiple children can have the same label
     * Can implement the out going arc by using the in going arc structure of the classical Node
      */

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private NonDeterministicArcs children;
    private NonDeterministicArcs parents;
    private SuccessionOfAbstractNode associations;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected NDNode(int allocatedIndex) {
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a Node.
     * The object is managed by the allocator.
     * @return A fresh Node
     */
    public static NDNode create(){
        NDNode node = allocator().allocate();
        node.prepare();
        return node;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public INode Node(){
        return NDNode.create();
    }

    /**
     * Sort the children by their label
     */
    public void sortChildren(){
        //children.sort();
    }

    //**************************************//
    //               SETTERS                //
    //**************************************//

    /**
     * Associate nodes to this node
     * @param associations an array of nodes
     */
    public void associate(SuccessionOfNode associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length(); i++) this.associations.set(i, associations.get(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associate(ArrayOfNodeInterface associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length(); i++) this.associations.set(i, associations.get(i));
    }

    public void associate(SuccessionOfNodeInterface associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length; i++) this.associations.set(i, associations.get(i));
    }

    /**
     * Associate nodes to this node
     * @param node1 The first node
     * @param node2 The second node
     */
    public void associate(NDNode node1, NDNode node2){
        associations.set(0, node1);
        associations.set(1, node2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associate(INode x1, INode x2){
        associations.set(0, x1);
        associations.set(1, x2);
    }

    /**
     * Set the ith associated node to node
     * @param node The node to associate
     * @param position The position of the node
     */
    public void associate(INode node, int position){
        if(position >= associations.length) {
            SuccessionOfAbstractNode associations = SuccessionOfAbstractNode.create(position + 1);
            for(int i = 0; i < this.associations.length; i++) associations.set(i, this.associations.get(i));
            Memory.free(this.associations);
            this.associations = associations;
        }
        associations.set(position, node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setX(INode node, int i){
        associate(node, i);
    }

    @Override
    public void setX1(INode x1) {
        associations.set(0, x1);
    }

    @Override
    public void setX2(INode x2) {
        associations.set(1, x2);
    }

    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get all the nodes associated with this node
     * @return All the nodes associated with this node
     */
    public SuccessionOfAbstractNode getAssociations(){
        return associations;
    }

    /**
     * Get the child by the index of the value.
     * @param index Index of the value
     * @return The node associated with the given index;
     */
    public Node getChildrenByIndex(int index){
        return null;
        //return children.getByIndex(index);
    }

    /**
     * Get the value of the label located at the given index
     * @param index Given index
     * @return The value of the label located at the given index
     */
    public int getValue(int index){
        return 0;
        //return children.getValue(index);
    }

    /**
     * The outgoing arcs
     * @return all outgoing arcs
     */
    public NonDeterministicArcs getChildren(){
        return children;
    }

    /**
     * The ingoing arcs
     * @return all the ingoing arcs
     */
    public NonDeterministicArcs getParents(){
        return parents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INode getX(int i){
        return associations.get(i);
    }

    @Override
    public boolean containsLabel(int label) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INode getX1(){
        return associations.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INode getX2(){
        return associations.get(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INode getChild(int label){
        return children.getFirst(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> iterateOnChildLabels(){
        return children.values();
    }

    public SetOfNode iterateOnChildren(int label){
        return children.get(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> iterateOnParentLabels(){
        return parents.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SetOfNode iterateOnParents(int label){
        return parents.get(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numberOfChildren(){
        return children.size();
    }

    public int numberOfChildren(int label){
        return children.get(label).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numberOfParentsLabel() {
        return parents.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numberOfParents(int label) {
        return parents.get(label).size();
    }


    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAssociations(){
        associations.clear();
    }

    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    /**
     * Add a child to this node with the given label
     * @param label Label of the arc
     * @param child Node to add as a child
     */
    public void addChild(int label, NDNode child){
        this.children.add(label, child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(int label, INode child){
        addChild(label, (NDNode) child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChild(int label){
        //this.children.remove(label);
    }

    public void removeChildren(int label){
        this.children.remove(label);
    }

    /**
     * Add a parent to this node with the given label
     * @param label Label of the ingoing arc
     * @param parent Node to add as a parent
     */
    public void addParent(int label, NDNode parent){
        this.parents.add(label, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addParent(int label, INode parent){
        addParent(label, (NDNode) parent);
    }

    /**
     * Remove the given node from the parents' list corresponding to the given arc's label
     * @param label Label of the ingoing arc
     * @param parent The parent node to remove
     */
    public void removeParent(int label, NDNode parent){
        this.parents.remove(label, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeParent(int label, INode parent){
        removeParent(label, (NDNode) parent);
    }

    /**
     * Replace all parents' references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceParentsReferencesBy(NDNode node){
        for(int value : parents){
            for(INode parent : parents.get(value)) {
                parent.addChild(value, node);
                node.addParent(value, parent);
            }
        }
        parents.clear();
    }

    public void copyChildrenReferencesFrom(NDNode node){
        for(int value : node.children){
            for(INode child : node.children.get(value)){
                addChild(value, child);
                child.addParent(value, this);
            }
        }
    }

    /**
     * Replace all children's references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceChildrenReferencesBy(NDNode node){
        for(int value : children){
            for(INode child : children.get(value)) {
                node.addChild(value, child);
                child.addParent(value, node);
            }
        }
        parents.clear();
    }

    /**
     * Replace all references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceReferencesBy(NDNode node){
        replaceChildrenReferencesBy(node);
        replaceParentsReferencesBy(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearParents(){
        parents.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearChildren(){
        children.clear();
    }

    /**
     * Set the children
     * @param children The new children
     */
    protected void setChildren(NonDeterministicArcs children){
        this.children = children;
    }

    /**
     * Set the parents
     * @param parents The new parents
     */
    protected void setParents(NonDeterministicArcs parents){
        this.parents = parents;
    }

    /**
     * Create new in-going and out-going arcs structures.
     */
    protected void allocateArcs(){
        children = NonDeterministicArcs.create();
        parents = NonDeterministicArcs.create();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    /**
     * Call the allocator to free this object
     */
    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * Prepare the object to be used a new one
     */
    public void prepare() {
        allocateArcs();
        associations = SuccessionOfAbstractNode.create(2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        Memory.free(children);
        Memory.free(parents);
        Memory.free(associations);
        dealloc();
    }


    /**
     * <b>The allocator that is in charge of the NDNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<NDNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator() {
            super.init();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected NDNode[] arrayCreation(int capacity) {
            return new NDNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected NDNode createObject(int index) {
            return new NDNode(index);
        }
    }
}
