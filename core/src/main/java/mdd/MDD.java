package mdd;

import mdd.components.Layer;
import mdd.components.Node;
import mdd.operations.Pack;
import memory.*;
import representation.MDDVisitor;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.lists.ListOfLayer;

import java.util.Random;

/**
 * <b>The class representing the MDD.</b> <br>
 * Contains a root node that is not null, a set of layers and a tt node if the MDD has been reduce.
 */
public class MDD implements Allocable {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    // Root node
    private Node root;
    private Node tt;

    // Layers
    private final ListOfLayer L = ListOfLayer.create();
    private int size;

    // Domain of the MDD
    private final Domains D = Domains.create();

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

    public static MDD create(Node root){
        MDD mdd = allocator().allocate();
        mdd.setRoot(root);
        return mdd;
    }

    public static MDD create(){
        MDD mdd = allocator().allocate();
        mdd.setRoot(mdd.Node());
        return mdd;
    }

    protected MDD(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
        L.add(Layer.create());
    }

    /**
     * Free the current root node and add as a root the given node
     * @param root
     */
    public void setRoot(Node root){
        L.get(0).freeAllNodes();    // remove the current root
        this.root = root;           // set the pointer
        L.get(0).add(root);         // add the new root
    }

    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * Accept a MDDVisitor (design pattern).
     * Mainly used to represent a MDD
     * @param visitor MDDVisitor
     */
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
        for(int i = 0; i < size; i++) getLayer(i).accept(visitor);
    }

    /**
     * If there is no root, return a default Node type associated with the MDD type
     * @return a Node the same type as the root Node.
     */
    public Node Node(){
        if(root != null) return root.Node();
        return Node.create();
    }

    /**
     * @return a MDD the same type as the current MDD, with the same Node type as the root for default Node
     */
    public MDD MDD(){
        return MDD(Node());
    }

    /**
     * @return a MDD the same type as the current MDD with the given node as a root
     */
    public MDD MDD(Node root){
        return create(root);
    }

    /**
     * Create a copy of the given MDD from layer start to stop onto the given MDD.
     * Add the copy of the nodes to the given MDD at the same layer + the given offset.
     * Example : start = 1, stop = 3, offset = 2 will copy the layer 1 onto the layer 3 of the copy MDD,
     * layer 2 on layer 4 and layer 3 on layer 5.
     * @param copy The MDD used to stock the copy
     * @param offset The offset of the copy
     * @param start The first layer to copy
     * @param stop The last layer to copy
     * @return A copy of the current MDD from layer start to layer stop.
     */
    public MDD copy(MDD copy, int offset, int start, int stop){
        for(int i = start; i < stop; i++){
            for(Node original : getLayer(i)) {
                Node copyNode = original.getX1();
                for(int arc : original.getChildren()){
                    Node child = original.getChild(arc);
                    Node copyChild = child.getX1();
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
     * Create a copy of the given MDD from root to tt onto the given MDD.
     * Add the copy of the nodes to the given MDD at the same layer + the given offset.
     * @param copy The MDD used to stock the copy
     * @param offset The offset of the copy
     * @return A copy of the current MDD from root to tt
     */
    public MDD copy(MDD copy, Node root, int offset){
        getRoot().associate(root, null);
        copy(copy, offset, 0, size());
        copy.setTT();
        return copy;
    }

    /**
     * Create a copy of the given MDD from root to tt onto the given MDD.
     * @param copy The MDD used to stock the copy
     * @return A copy of the current MDD from root to tt
     */
    public MDD copy(MDD copy){
        copy.setSize(size());
        return copy(copy, copy.getRoot(), 0);
    }

    /**
     * Create a copy of the given MDD from root to tt.
     * @return A copy of the current MDD from root to tt
     */
    public MDD copy(){
        return copy(MDD());
    }

    /**
     * Clear all nodes' associations
     */
    public void clearAllAssociations(){
        for(int i = 0; i < size(); i++) for(Node node : getLayer(i)) node.clearAssociations();
    }

    /**
     * Replace the values of all arcs according to the given mapping.
     * For instance, you want to replace all arcs with value 0 by [1, 2] :
     * all you have to do is make a mapping 0 -> {1, 2} and give it as an input.
     * @param mapping The mapping of the values
     */
    public void replace(MapOf<Integer, SetOf<Integer>> mapping){
        replace(mapping, 0, size);
    }

    /**
     * Replace the values of all arcs according to the given mapping, from layer start to layer stop.
     * For instance, you want to replace all arcs with value 0 by [1, 2] :
     * all you have to do is make a mapping 0 -> {1, 2} and give it as an input.
     * @param mapping The mapping of the values
     * @param start The first layer of the operation
     * @param stop The last layer of the operation
     */
    public void replace(MapOf<Integer, SetOf<Integer>> mapping, int start, int stop){
        SetOf<Integer> setV = Memory.SetOfInteger();
        for(int i = start; i < stop - 1; i++){
            for(Node node : getLayer(i)) {
                node.replace(mapping, setV);
                this.D.get(i).add(setV);
            }
        }
        Memory.free(setV);
    }

    /**
     * Replace the values of all arcs according to the map : [ 0 -> V0, 1 -> V1 ].
     * Simplification of the main function to use with binary MDDs.
     * @param V0 All values associated to 0
     * @param V1 All values associated to 1
     */
    public void replace(SetOf<Integer> V0, SetOf<Integer> V1){
        MapOf<Integer, SetOf<Integer>> values = Memory.MapOfIntegerSetOfInteger();
        values.put(0, V0);
        values.put(1, V1);
        replace(values);
        Memory.free(values);
    }

    /**
     * Perform a random walk in the MDD.
     * FOR TESTING PURPOSES
     * @return A valid path in the MDD
     */
    public int[] randomWalk(){
        Node current = root;
        Random random = new Random();
        int[] path = new int[size-1];
        int i=0;
        while (current != tt){
            int idx = random.nextInt(current.numberOfChildren());
            path[i++] = current.getValue(idx);
            current = current.getChild(path[i-1]);
        }
        return path;
    }

    /**
     * Clear the MDD without freeing it.
     * That is, remove and free all nodes from the layers,
     * clear the domains and set the tt's pointer to null.
     * The layers are kept.
     */
    public void clear(){
        for(int i = 1; i < size; i++) getLayer(i).freeAllNodes();
        D.clear();
        this.tt = null;
    }

    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get the root of the mdd
     * @return The root of the mdd
     */
    public Node getRoot(){
        return root;
    }

    /**
     * Get the terminal node of the MDD
     * @return The terminal node of the MDD
     */
    public Node getTt(){
        return tt;
    }

    /**
     * Get the Layer i
     * @param i depth of the layer
     * @return The Layer i
     */
    public Layer getLayer(int i){
        return L.get(i);
    }

    /**
     * Get the depth of the MDD (the number of layers)
     * @return int size of the MDD
     */
    public int size(){
        return size;
    }

    /**
     * Get the set of all values that are in the MDD
     * @return The set of all values that are in the MDD
     */
    public Domains getDomains(){
        return D;
    }

    public SetOf<Integer> getDomain(int index){
        return D.get(index);
    }

    /**
     * Get the number of nodes in the MDD
     * @return The number of nodes in the MDD
     */
    public int nodes(){
        int n = 0;
        for(int i = 0; i < size; i++) if(L.size() >= size()) n += getLayer(i).size();
        return n;
    }

    /**
     * Get the number of arcs in the MDD
     * @return The number of arcs in the MDD
     */
    public int arcs(){
        int n = 0;
        for(int i = 0; i < size; i++) for(Node x : getLayer(i)) n += x.numberOfChildren();
        return n;
    }

    /**
     * Get the number of solutions represented by the MDD
     * @return The number of solutions represented by the MDD
     */
    public double nSolutions(){
        // Initialize the count of solution of each node to 0
        for(int i = 0; i < size; i++) for(Node x : getLayer(i)) x.s = 0;
        if(tt == null) return 0;
        root.s = 1;
        for(int i = 0; i < size; i++){
            for(Node x : getLayer(i)){
                for(int arc : x.getChildren()) x.getChild(arc).s += x.s;
            }
        }
        return tt.s;
    }

    //**************************************//
    //               SETTERS                //
    //**************************************//

    /**
     * Set the size of the MDD.
     * That is to say, it ensures that the MDD contains at least a size number of Layer.
     * @param size The size of the MDD
     */
    public void setSize(int size){
        if(this.L.size() < size) for(int i = 0, diff = size - L.size(); i < diff; i++) L.add(Layer.create());
        this.size = size;
    }

    /**
     * Add a value to the set of values that are in the MDD
     * @param v the value
     * @param layer the number of the layer
     */
    public void addValue(int v, int layer){
        D.put(layer, v);
    }


    //**************************************//
    //             MANAGE MDD               //
    //**************************************//

    /**
     * Add the path corresponding to the given values from the MDD's root.
     * @param values Labels of the path
     */
    public void addPath(int... values){
        Node current = getRoot();
        for(int i = 0; i < values.length; i++) {
            int v = values[i];
            if(current.containsLabel(v)) current = current.getChild(v);
            else{
                Node next = Node();
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
    public void addNode(Node node, int layer){
        getLayer(layer).add(node);
    }

    /**
     * Remove a node from the given layer
     * @param node The node to remove
     * @param layer The index of the layer
     */
    public void removeNode(Node node, int layer){
        getLayer(layer).removeAndFree(node);
    }

    /**
     * Add an arc between the source node and the destination node with the given value as label.
     * Ensures the connection between the two nodes
     * @param source The source node (parent)
     * @param value The value of the arc's label
     * @param destination The destination node (child)
     */
    public void addArc(Node source, int value, Node destination, int layer){
        source.addChild(value, destination);
        destination.addParent(value, source);
        addValue(value, layer);
    }

    /**
     * Remove the arc from the source node with the given label's value.
     * Ensures to delete references of this arc in both nodes.
     * @param source The source node
     * @param value The value of the arc's label.
     */
    public void removeArc(Node source, int value){
        source.getChild(value).removeParent(value, source);
        source.removeChild(value);
    }

    /**
     * Add the node destination to the given layer, and add an arc between the source node
     * and the destination node with the given value as label.
     * @param source The source node
     * @param value The value of the arc's label
     * @param destination The destination node - node to add in the MDD
     * @param layer The layer of the MDD where the node destination will be added
     */
    public void addArcAndNode(Node source, int value, Node destination, int layer){
        addArc(source, value, destination, layer-1);
        addNode(destination, layer);
    }

    /**
     * Transform the MDD into a normal form (delete useless nodes, merge leaves, reduce).
     */
    public void reduce(){
        // Merge the leaves of the MDD into a tt node
        if(getLayer(size - 1).size() == 1) this.tt = getLayer(size - 1).getNode();
        else {
            Node newTT = Node();
            for (Node leaf : getLayer(size - 1)) leaf.replaceReferencesBy(newTT);
            getLayer(size - 1).clear();
            getLayer(size - 1).add(newTT);
            this.tt = newTT;
        }

        // Merge similar nodes
        SetOf<Integer> V = Memory.SetOfInteger();
        D.fillWithValues(V);
        if(getLayer(size - 2).size() != 0) Pack.pReduce(L, size, V);
        Memory.free(V);
    }

    /**
     * Remove all nodes from the MDD that do not have a child.
     * This can be used as a pre-reduce operation to remove nodes
     * from a MDD to free some memory, typically during an operation.
     */
    public void removeChildless(){
        SetOf<Node> childless = Memory.SetOfNode();
        for(int i = size-2; i >= 0; i--) {
            for(Node node : getLayer(i)) if(node.numberOfChildren() == 0) childless.add(node);
            for(Node node : childless) removeNode(node, i);
        }
    }

    /**
     * Automatically set the terminal node.
     */
    private void setTT(){
        if(getLayer(size - 1).size() == 1) this.tt = getLayer(size - 1).getNode();
        else {
            Node newTT = Node();
            for (Node leaf : getLayer(size - 1)) leaf.replaceReferencesBy(newTT);

            getLayer(size - 1).clear();
            getLayer(size - 1).add(newTT);
            this.tt = newTT;
        }
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free() {
        for(int i = 0; i < size; i++){
            getLayer(i).freeAllNodes();
            Memory.free(getLayer(i));
        }
        Memory.free(root);
        L.clear();
        L.add(Layer.create());
        D.clear();
        this.root = null;
        this.tt = null;
        dealloc();
    }

    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the MDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<MDD> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(16);
        }

        @Override
        protected MDD[] arrayCreation(int capacity) {
            return new MDD[capacity];
        }

        @Override
        protected MDD createObject(int index) {
            return new MDD(index);
        }
    }

}
