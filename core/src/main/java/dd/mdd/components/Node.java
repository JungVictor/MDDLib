package dd.mdd.components;

import dd.AbstractNode;
import memory.*;
import representation.MDDVisitor;
import structures.arrays.ArrayOfAbstractNode;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.generics.SetOfNode;
import structures.successions.SuccessionOfNode;

import java.util.Random;

/**
 * <b>This class is used to represent a node of a MDD.</b> <br>
 * The Node might be the most important structure in the MDD.
 * It contains all information about its parents and its children.
 * Furthermore, you can associate other node to a given node, which is
 * notably useful during operations such as intersection or union.
 */
public class Node extends AbstractNode {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private OutArcs children;
    private InArcs parents;
    private SuccessionOfNode associations;


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
    protected Node(int allocatedIndex) {
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a Node.
     * The object is managed by the allocator.
     * @return A fresh Node
     */
    public static Node create(){
        Node node = allocator().allocate();
        node.prepare();
        return node;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * Accept a MDDVisitor. Used to represent the MDD.
     * @param visitor The visitor
     */
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
        children.accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node Node(){
        return Node.create();
    }

    /**
     * Sort the children by their label
     */
    public void sortChildren(){
        children.sort();
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
    public void associate(ArrayOfAbstractNode associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length(); i++) this.associations.set(i, (Node) associations.get(i));
    }

    /**
     * Associate nodes to this node
     * @param node1 The first node
     * @param node2 The second node
     */
    public void associate(Node node1, Node node2){
        associations.set(0, node1);
        associations.set(1, node2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associate(AbstractNode x1, AbstractNode x2){
        associate((Node) x1, (Node) x2);
    }

    /**
     * Set the ith associated node to node
     * @param node The node to associate
     * @param position The position of the node
     */
    public void associate(Node node, int position){
        if(position >= associations.length) {
            SuccessionOfNode associations = SuccessionOfNode.create(position + 1);
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
    public void setX(AbstractNode node, int i){
        associate((Node) node, i);
    }

    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get all the nodes associated with this node
     * @return All the nodes associated with this node
     */
    public SuccessionOfNode getAssociations(){
        return associations;
    }

    /**
     * Get the child by the index of the value.
     * @param index Index of the value
     * @return The node associated with the given index;
     */
    public Node getChildByIndex(int index){
        return children.getByIndex(index);
    }

    /**
     * Get the value of the label located at the given index
     * @param index Given index
     * @return The value of the label located at the given index
     */
    public int getValue(int index){
        return children.getValue(index);
    }

    /**
     * The outgoing arcs
     * @return all outgoing arcs
     */
    public OutArcs getChildren(){
        return children;
    }

    /**
     * The ingoing arcs
     * @return all the ingoing arcs
     */
    public InArcs getParents(){
        return parents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getX(int i){
        return associations.get(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getX1(){
        return associations.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getX2(){
        return associations.get(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getChild(int label){
        return children.get(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutArcs iterateOnChildLabel(){
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> iterateOnParentLabel(){
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


    // ******
    // TEST
    // ******

    /**
     * Get the value of a random ingoing arc
     * @param random The random object
     * @return the value of a random ingoing arc
     */
    public int getRandomParentValue(Random random){
        return parents.randomValue(random);
    }

    /**
     * Check if the given node is a child of this node
     * @param child The node to check
     * @return true if the given node is a child, false otherwise
     */
    public boolean containsChild(Node child){
        return children.contains(child);
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
    public void addChild(int label, Node child){
        this.children.add(label, child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(int label, AbstractNode child){
        addChild(label, (Node) child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChild(int label){
        this.children.remove(label);
    }

    /**
     * Add a parent to this node with the given label
     * @param label Label of the ingoing arc
     * @param parent Node to add as a parent
     */
    public void addParent(int label, Node parent){
        this.parents.add(label, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addParent(int label, AbstractNode parent){
        addParent(label, (Node) parent);
    }

    /**
     * Remove the given node from the parents' list corresponding to the given arc's label
     * @param label Label of the ingoing arc
     * @param parent The parent node to remove
     */
    public void removeParent(int label, Node parent){
        this.parents.remove(label, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeParent(int label, AbstractNode parent){
        removeParent(label, (Node) parent);
    }

    /**
     * Replace all parents' references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceParentsReferencesBy(Node node){
        for(int value : parents){
            for(Node parent : parents.get(value)) {
                parent.addChild(value, node);
                node.addParent(value, parent);
            }
        }
        parents.clear();
    }

    /**
     * Replace all children's references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceChildrenReferencesBy(Node node){
        for(Integer value : children){
            Node child = children.get(value);
            child.removeParent(value, this);
            child.addParent(value, node);
            node.addChild(value, child);
        }
        children.clear();
    }

    /**
     * Replace all references of this node by the given node
     * @param node Node to replace this node
     */
    public void replaceReferencesBy(Node node){
        replaceChildrenReferencesBy(node);
        replaceParentsReferencesBy(node);
    }

    /**
     * Replace all arcs values according to the given map
     * @param mapping The mapping of the values
     */
    public void replace(MapOf<Integer, SetOf<Integer>> mapping){
        SetOf<Integer> added = Memory.SetOfInteger();
        replace(mapping, added);
        Memory.free(added);
    }

    /**
     * Replace all arcs values according to the given map.
     * Use the added set to store all the newly added labels
     * @param mapping The mapping of values
     * @param added The set to store all the newly added labels (memory management)
     */
    public void replace(MapOf<Integer, SetOf<Integer>> mapping, SetOf<Integer> added){
        OutArcs new_children = OutArcs.create();
        for(int v : iterateOnChildLabel()) getChild(v).removeParent(v, this);

        for(int v : iterateOnChildLabel()) {
            if(mapping.contains(v)) {
                for (int arc : mapping.get(v)) {
                    new_children.add(arc, getChild(v));
                    added.add(arc);
                    getChild(v).addParent(arc, this);
                }
            }
            else {
                new_children.add(v, getChild(v));
                getChild(v).addParent(v, this);
                added.add(v);
            }
        }

        Memory.free(children);
        children = new_children;
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
    protected void setChildren(OutArcs children){
        this.children = children;
    }

    /**
     * Set the parents
     * @param parents The new parents
     */
    protected void setParents(InArcs parents){
        this.parents = parents;
    }

    /**
     * Create new in-going and out-going arcs structures.
     */
    protected void allocateArcs(){
        children = OutArcs.create();
        parents = InArcs.create();
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
        associations = SuccessionOfNode.create(2);
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
     * <b>The allocator that is in charge of the Node type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<Node> {

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
        protected Node[] arrayCreation(int capacity) {
            return new Node[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Node createObject(int index) {
            return new Node(index);
        }
    }
}
