package dd.bdd;

import dd.DecisionDiagram;
import dd.bdd.components.BinaryNode;
import dd.interfaces.INode;
import dd.operations.HashReduce;
import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfInt;
import structures.lists.UnorderedListOfBinaryNode;

import java.util.InputMismatchException;

/**
 * <b>The class representing the BDD.</b> <br>
 * Contains a root node that is not null, a set of layers and a tt node if the MDD has been reduced.<br>
 * The BDD is a sub-type of MDD with binary values.
 */
public class BDD extends DecisionDiagram {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private BinaryNode root;
    private BinaryNode tt;

    private UnorderedListOfBinaryNode[] L;


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
    private BDD(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create a BDD.
     * The object is managed by the allocator.
     * @return A fresh BDD
     */
    public static BDD create(){
        BDD bdd = allocator().allocate();
        bdd.setSize(1);
        bdd.setRoot(bdd.Node());
        return bdd;
    }

    /**
     * Create a BDD of given size.
     * The object is managed by the allocator.
     * @param size The size of the BDD
     * @return A fresh BDD
     */
    public static BDD create(int size){
        BDD bdd = create();
        bdd.setSize(size);
        return bdd;
    }

    /**
     * Create a BDD with given node as root.
     * The object is managed by the allocator.
     * @param root Node to use as a root
     * @return A fresh BDD
     */
    public static BDD create(BinaryNode root){
        BDD bdd = create();
        bdd.setSize(1);
        bdd.setRoot(root);
        return bdd;
    }


    /**
     * Create a BDD of given size with given node as root
     * The object is managed by the allocator.
     * @param root Node to use as a root
     * @param size Size of the BDD
     * @return A fresh BDD
     */
    public static BDD create(BinaryNode root, int size){
        BDD bdd = create();
        bdd.setSize(size);
        bdd.setRoot(root);
        return bdd;
    }

    /**
     * Set the root of the BDD
     * @param root The node to set as root
     */
    public void setRoot(BinaryNode root){
        if(this.root != null) removeNode(this.root, 0);
        this.root = root;
        addNode(root, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSize(int size){
        int s = L == null ? 0 : L.length;
        if(s < size) {
            UnorderedListOfBinaryNode[] L = new UnorderedListOfBinaryNode[size];
            for(int i = 0; i < s; i++) L[i] = this.L[i];
            for(int i = s; i < size; i++) L[i] = UnorderedListOfBinaryNode.create();
            this.L = L;
        }
        super.setSize(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(INode root){
        if(root instanceof BinaryNode) setRoot((BinaryNode) root);
        else throw new InputMismatchException("Expected the root to be at least of BinaryNode class !");
    }


    //**************************************//
    //          SPECIAL FUNCTIONS           //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryNode Node(){
        if(root != null) return root.Node();
        return BinaryNode.create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BDD DD(){
        return BDD(Node());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BDD DD(INode root){
        if(root instanceof BinaryNode) return BDD((BinaryNode) root);
        throw new InputMismatchException("Expected the root to be at least of BinaryNode class !");
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
     * Create a BDD the same type as the current BDD
     * @return a BDD the same type as the current BDD
     */
    public BDD BDD(){
        return create(Node());
    }


    // -----------------
    //      COPY

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
                for(int arc : original.getChildren()){
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
     * {@inheritDoc}
     */
    @Override
    public BDD copy(){
        return copy(DD());
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get the Layer i
     * @param i depth of the layer
     * @return The Layer i
     */
    public UnorderedListOfBinaryNode getLayer(int i){
        return L[i];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryNode getRoot(){
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryNode getTt(){
        return tt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLayerSize(int i){
        return L[i].size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable iterateOnLayer(int i){
        return L[i];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> iterateOnDomain(int i){
        return BinaryNode.BOTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDomainSize(int i){
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxValue() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean domainContains(int i, int v){
        return v == 0 || v == 1;
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
     * {@inheritDoc}
     */
    @Override
    public void addNode(INode node, int layer){
        addNode((BinaryNode) node, layer);
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
     * {@inheritDoc}
     */
    @Override
    public void removeNode(INode node, int layer){
        removeNode((BinaryNode) node, layer);
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
     * {@inheritDoc}
     */
    @Override
    public void reduce(){
        // Merge the leaves of the BDD into a tt node
        setTT();

        // Merge similar nodes
        if(size() > 1 && getLayer(size() - 2).size() != 0) HashReduce.reduce(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTT(){
        if(getLayer(size() - 1).size() == 1) this.tt = getLayer(size() - 1).get(0);
        else {
            BinaryNode newTT = Node();
            for (BinaryNode leaf : getLayer(size() - 1)) leaf.replaceReferencesBy(newTT);
            getLayer(size() - 1).clear();
            getLayer(size() - 1).add(newTT);
            this.tt = newTT;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(){
        for(int i = 1; i < size(); i++) {
            for(BinaryNode node : L[i]) {
                node.remove();
                Memory.free(node);
            }
            L[i].clear();
        }
        this.tt = null;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of Allocable interface

    /**
     * {@inheritDoc}
     */
    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        dealloc();
    }

    /**
     * Call the allocator to free this object
     */
    protected void dealloc(){
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the BDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<BDD> {

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
        protected BDD[] arrayCreation(int capacity) {
            return new BDD[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected BDD createObject(int index) {
            return new BDD(index);
        }
    }

}
