package mdd.components;

import memory.*;
import representation.MDDVisitor;
import structures.arrays.ArrayOfNode;
import structures.generics.MapOf;
import structures.generics.SetOf;

import java.util.ArrayList;
import java.util.Random;

/**
 * <b>This class is used to represent a node of a MDD.</b> <br>
 * The Node might be the most important structure in the MDD.
 * It contains all information about its parents and its children.
 * Furthermore, you can associate other node to a given node, which is
 * notably useful during operations such as intersection or union.
 */
public class Node implements Allocable {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private OutArcs children;
    private InArcs parents;
    private ArrayOfNode associations;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static Node create(){
        Node node = allocator().allocate();
        node.prepare();
        return node;
    }

    protected Node(int allocatedIndex) {
        this.allocatedIndex = allocatedIndex;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * Accept a MDDVisitor. Used to represent the MDD.
     * @param visitor
     */
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
        children.accept(visitor);
    }

    /**
     * @return a Node the same type as this node.
     */
    public Node Node(){
        return Node.create();
    }

    //**************************************//
    //               SETTERS                //
    //**************************************//

    /**
     * Associate nodes to this node
     * @param associations an array of nodes
     */
    public void associate(ArrayOfNode associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length(); i++) this.associations.set(i, associations.get(i));
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

    public void associate(Node node, int position){
        if(position >= associations.length) {
            ArrayOfNode associations = ArrayOfNode.create(position + 1);
            for(int i = 0; i < this.associations.length; i++) associations.set(i, this.associations.get(i));
            Memory.free(this.associations);
            this.associations = associations;
        }
        associations.set(position, node);
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get all the nodes associated with this node
     * @return All the nodes associated with this node
     */
    public ArrayOfNode getAssociations(){
        return associations;
    }

    /**
     * Get the first node associated with this node
     * @return The first node associated with this node
     */
    public Node getX1(){
        return associations.get(0);
    }

    /**
     * Get the first node associated with this node
     * @return The first node associated with this node
     */
    public Node getX2(){
        return associations.get(1);
    }

    /**
     * Get the ith node associated with this node.
     * @param i The index of the associated node
     * @return The ith node associated with this node, null if there is none
     */
    public Node getX(int i){
        return associations.get(i);
    }

    /**
     * Get the child corresponding to the given label
     * @param label The value of the label
     * @return The node associated with the given label
     */
    public Node getChild(int label){
        return children.get(label);
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
     * Get all values of the children's labels
     * @return all values of the children's labels
     */
    public ArrayList<Integer> getValues(){
        return children.getValues();
    }

    /**
     * Check if the node has a child corresponding to the given label
     * @param label Label of the arc to check
     * @return True if there is an arc associated to the given label, false otherwise.
     */
    public boolean containsLabel(int label){
        return children.contains(label);
    }

    /**
     * The number of outgoing arcs
     * @return the number of outgoing arcs
     */
    public int numberOfChildren(){
        return children.size();
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
     * Remove all references of this node from nodes referenced by this node
     */
    public void remove(){
        for(int value : children) children.get(value).removeParent(value, this);
        for(int value : parents) for(Node node : parents.get(value)) node.removeChild(value);
    }

    /**
     * Remove all information of this node
     */
    public void clear(){
        children.clear();
        parents.clear();
        associations.clear();
    }

    /**
     * Clear all associations of the node
     */
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
     * Remove the child corresponding to the given label
     * @param label Label of the outgoing arc
     */
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
     * Remove the given node from the parents' list corresponding to the given arc's label
     * @param label Label of the ingoing arc
     * @param parent The parent node to remove
     */
    public void removeParent(int label, Node parent){
        this.parents.remove(label, parent);
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
        for(int v : getValues()) getChild(v).removeParent(v, this);

        for(int v : getValues()) {
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


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    protected void setChildren(OutArcs children){
        this.children = children;
    }

    protected void setParents(InArcs parents){
        this.parents = parents;
    }

    protected void allocateArcs(){
        children = OutArcs.create();
        parents = InArcs.create();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    protected void dealloc(){
        allocator().free(this);
    }

    public void prepare() {
        allocateArcs();
        associations = ArrayOfNode.create(2);
    }

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

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

        @Override
        protected Node[] arrayCreation(int capacity) {
            return new Node[capacity];
        }

        @Override
        protected Node createObject(int index) {
            return new Node(index);
        }
    }
}
