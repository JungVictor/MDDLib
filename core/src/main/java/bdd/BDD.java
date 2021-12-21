package bdd;

import bdd.components.BinaryNode;
import mdd.operations.HashReduce;
import memory.Allocable;
import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfInt;
import structures.lists.UnorderedListOfBinaryNode;

public class BDD implements Allocable {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private BinaryNode root;
    private BinaryNode tt;

    private UnorderedListOfBinaryNode[] L;
    private int size;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    private BDD(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public static BDD create(){
        return allocator().allocate();
    }

    public static BDD create(int size){
        BDD bdd = create();
        bdd.setSize(size);
        return bdd;
    }

    public static BDD create(BinaryNode root){
        BDD bdd = create();
        bdd.setRoot(root);
        return bdd;
    }

    public static BDD create(BinaryNode root, int size){
        BDD bdd = create();
        bdd.setSize(size);
        bdd.setRoot(root);
        return bdd;
    }

    public void setSize(int size){
        this.size = L == null ? 0 : L.length;
        if(this.size < size) {
            UnorderedListOfBinaryNode[] L = new UnorderedListOfBinaryNode[size];
            for(int i = 0; i < this.size; i++) L[i] = this.L[i];
            for(int i = this.size; i < size; i++) L[i] = UnorderedListOfBinaryNode.create();
            this.L = L;
        }
        this.size = size;
    }

    public void setRoot(BinaryNode root){
        this.root = root;
    }


    //**************************************//
    //          SPECIAL FUNCTIONS           //
    //**************************************//

    /**
     * If there is no root, return a default Node type associated with the BDD type
     * @return a Node the same type as the root Node.
     */
    public BinaryNode Node(){
        if(root != null) return root.Node();
        return BinaryNode.create();
    }

    /**
     * Create a BDD the same type as the current BDD, with the same Node type as the root for default Node
     * @return a BDD the same type as the current BDD, with the same Node type as the root for default Node
     */
    public BDD BDD(){
        return BDD(Node());
    }

    /**
     * Create a BDD the same type as the current BDD with the given node as a root
     * @param root The node to set as root
     * @return a BDD the same type as the current BDD with the given node as a root
     */
    public BDD BDD(BinaryNode root){
        return create(root);
    }

    /**
     * Create a copy of the given BDD from layer start to stop onto the given BDD.
     * Add the copy of the nodes to the given BDD at the same layer + the given offset.
     * Example : start = 1, stop = 3, offset = 2 will copy the layer 1 onto the layer 3 of the copy BDD,
     * layer 2 on layer 4 and layer 3 on layer 5.
     * @param copy The BDD used to stock the copy
     * @param offset The offset of the copy
     * @param start The first layer to copy
     * @param stop The last layer to copy
     * @return A copy of the current BDD from layer start to layer stop.
     */
    public BDD copy(BDD copy, int offset, int start, int stop){
        for(int i = start; i < stop; i++){
            for(BinaryNode original : getLayer(i)) {
                BinaryNode copyNode = original.getX1();
                for(byte arc : original.getChildren()){
                    BinaryNode child = original.getChild(arc);
                    BinaryNode copyChild = child.getX1();
                    // Child node is not yet copied
                    if(copyChild == null) {
                        copyChild = copy.Node();
                        child.associate(copyChild, null);
                        copy.addNode(copyChild, i+1+offset);
                    }
                    copy.addArc(copyNode, arc, copyChild, i+offset);
                }
                original.associate(null, null);
            }
        }
        return copy;
    }

    /**
     * Create a copy of the given BDD from root to tt onto the given BDD.
     * Add the copy of the nodes to the given BDD at the same layer + the given offset.
     * @param copy The BDD used to stock the copy
     * @param root The root node of the BDD
     * @param offset The offset of the copy
     * @return A copy of the current BDD from root to tt
     */
    public BDD copy(BDD copy, BinaryNode root, int offset){
        getRoot().associate(root, null);
        copy(copy, offset, 0, size());
        copy.setTT();
        return copy;
    }

    /**
     * Create a copy of the given BDD from root to tt onto the given BDD.
     * @param copy The BDD used to stock the copy
     * @return A copy of the current BDD from root to tt
     */
    public BDD copy(BDD copy){
        copy.setSize(size());
        return copy(copy, copy.getRoot(), 0);
    }

    /**
     * Create a copy of the given BDD from root to tt.
     * @return A copy of the current BDD from root to tt
     */
    public BDD copy(){
        return copy(BDD());
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get the root of the BDD
     * @return The root of the BDD
     */
    public BinaryNode getRoot(){
        return root;
    }

    /**
     * Get the terminal node of the BDD
     * @return The terminal node of the BDD
     */
    public BinaryNode getTt(){
        return tt;
    }

    /**
     * Get the Layer i
     * @param i depth of the layer
     * @return The Layer i
     */
    public UnorderedListOfBinaryNode getLayer(int i){
        return L[i];
    }

    /**
     * Get the depth of the BDD (the number of layers)
     * @return int size of the BDD
     */
    public int size(){
        return size;
    }

    /**
     * Get the number of nodes in the BDD
     * @return The number of nodes in the BDD
     */
    public int nodes(){
        int n = 0;
        for(int i = 0; i < size; i++) if(L.length >= size()) n += getLayer(i).size();
        return n;
    }

    /**
     * Get the number of arcs in the BDD
     * @return The number of arcs in the BDD
     */
    public int arcs(){
        int n = 0;
        for(int i = 0; i < size; i++) for(BinaryNode x : getLayer(i)) n += x.numberOfChildren();
        return n;
    }

    /**
     * Get the number of solutions represented by the BDD
     * @return The number of solutions represented by the BDD
     */
    public double nSolutions(){
        return 0.0;
    }

    //**************************************//
    //             MANAGE BDD               //
    //**************************************//

    /**
     * Add the path corresponding to the given values from the BDD's root.
     * @param values Labels of the path
     */
    public void addPath(ArrayOfInt values){
        BinaryNode current = getRoot();
        for(int i = 0; i < values.length; i++) {
            int v = values.get(i);
            if(current.containsLabel(v)) current = current.getChild(v);
            else{
                BinaryNode next = Node();
                addArcAndNode(current, v, next, i+1);
                current = next;
            }
        }
    }

    /**
     * Add the given Node to the given layer.
     * @param node The node to add
     * @param layer The index of the layer
     */
    public void addNode(BinaryNode node, int layer){
        getLayer(layer).add(node);
    }

    /**
     * Remove a node from the given layer
     * @param node The node to remove
     * @param layer The index of the layer
     */
    public void removeNode(BinaryNode node, int layer){
        node.remove();
        L[layer].removeElement(node);
        Memory.free(node);
    }

    /**
     * Add an arc between the source node and the destination node with the given value as label.
     * Ensures the connection between the two nodes
     * @param source The source node (parent)
     * @param value The value of the arc's label
     * @param destination The destination node (child)
     * @param layer The layer of the PARENT node (source)
     */
    public void addArc(BinaryNode source, int value, BinaryNode destination, int layer){
        source.addChild(value, destination);
        destination.addParent(value, source);
    }

    /**
     * Remove the arc from the source node with the given label's value.
     * Ensures to delete references of this arc in both nodes.
     * @param source The source node
     * @param value The value of the arc's label.
     */
    public void removeArc(BinaryNode source, int value){
        source.getChild(value).removeParent(value, source);
        source.removeChild(value);
    }

    /**
     * Add the node destination to the given layer, and add an arc between the source node
     * and the destination node with the given value as label.
     * @param source The source node
     * @param value The value of the arc's label
     * @param destination The destination node - node to add in the BDD
     * @param layer The layer of the BDD where the node destination will be added
     */
    public void addArcAndNode(BinaryNode source, int value, BinaryNode destination, int layer){
        addArc(source, value, destination, layer-1);
        addNode(destination, layer);
    }

    /**
     * Transform the BDD into a normal form (delete useless nodes, merge leaves, reduce).
     */
    public void reduce(){
        // Merge the leaves of the BDD into a tt node
        if(getLayer(size - 1).size() == 1) this.tt = getLayer(size - 1).get(0);
        else {
            BinaryNode newTT = Node();
            for (BinaryNode leaf : getLayer(size - 1)) leaf.replaceReferencesBy(newTT);
            getLayer(size - 1).clear();
            getLayer(size - 1).add(newTT);
            this.tt = newTT;
        }

        // Merge similar nodes
        if(size > 1 && getLayer(size - 2).size() != 0) HashReduce.reduce(this);
    }

    /**
     * Automatically set the terminal node.
     */
    private void setTT(){
        if(getLayer(size - 1).size() == 1) this.tt = getLayer(size - 1).get(0);
        else {
            BinaryNode newTT = Node();
            for (BinaryNode leaf : getLayer(size - 1)) leaf.replaceReferencesBy(newTT);
            getLayer(size - 1).clear();
            getLayer(size - 1).add(newTT);
            this.tt = newTT;
        }
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public void free() {
        allocator().free(this);
    }

    static final class Allocator extends AllocatorOf<BDD> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected BDD[] arrayCreation(int capacity) {
            return new BDD[capacity];
        }

        @Override
        protected BDD createObject(int index) {
            return new BDD(index);
        }
    }

}
