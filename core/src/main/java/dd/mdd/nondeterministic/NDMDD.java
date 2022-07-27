package dd.mdd.nondeterministic;

import dd.DecisionDiagram;
import dd.interfaces.INode;
import dd.mdd.components.Layer;
import dd.mdd.nondeterministic.components.ILayer;
import dd.mdd.nondeterministic.components.NDNode;
import dd.operations.Pack;
import memory.AllocatorOf;
import memory.Memory;
import structures.Domains;
import structures.generics.SetOf;
import structures.generics.SetOfNode;
import structures.lists.ListOfILayer;
import structures.lists.ListOfLayer;

import java.util.InputMismatchException;

public class NDMDD extends DecisionDiagram {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;


    // Root node
    private NDNode root;
    private NDNode tt;

    // Layers
    private final ListOfILayer L = ListOfILayer.create();

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

    public void removeArcs(NDNode node, int layer, int label){
        SetOfNode<INode> toRemove = Memory.SetOfINode();
        for(INode inode : node.getChildren().get(label)) {
            inode.removeParent(label, node);
            if(inode.numberOfParentsLabel() == 0) toRemove.add(inode);
        }
        for(INode inode : toRemove) removeNode(inode, layer);
        Memory.free(toRemove);
        node.removeChildren(label);
    }


    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected NDMDD(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
        L.add(ILayer.create());
    }

    /**
     * Create an MDD with given node as root.
     * The object is managed by the allocator.
     * @param root Node to use as a root
     * @return A fresh MDD
     */
    public static NDMDD create(NDNode root){
        NDMDD mdd = allocator().allocate();
        mdd.setRoot(root);
        return mdd;
    }

    /**
     * Create an MDD.
     * The object is managed by the allocator.
     * @return A fresh MDD
     */
    public static NDMDD create(){
        NDMDD mdd = allocator().allocate();
        mdd.setRoot(mdd.Node());
        return mdd;
    }

    /**
     * Free the current root node and add as a root the given node
     * @param root The node to set as root
     */
    public void setRoot(NDNode root){
        L.get(0).freeAllNodes();    // remove the current root
        this.root = root;           // set the pointer
        L.get(0).add(root);         // add the new root
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(INode root){
        if(root instanceof NDNode) setRoot((NDNode) root);
        else throw new InputMismatchException("Expected the root to be at least of Node class !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTT(){
        if(getLayer(size() - 1).size() == 1) this.tt = (NDNode) getLayer(size() - 1).getNode();
        else {
            NDNode newTT = Node();
            for (INode leaf : getLayer(size() - 1)) leaf.replaceReferencesBy(newTT);

            getLayer(size() - 1).clear();
            getLayer(size() - 1).add(newTT);
            this.tt = newTT;
        }
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//

    /**
     * If there is no root, return a default Node type associated with the MDD type
     * @return a Node the same type as the root Node.
     */
    public NDNode Node(){
        if(root != null) return (NDNode) root.Node();
        return NDNode.create();
    }

    /**
     * Create an MDD.
     * @return a new MDD
     */
    public NDMDD NDMDD(){
        return create(Node());
    }

    /**
     * Create an MDD with given node as root.
     * @param root The node to set as root
     * @return a new MDD with given node as root.
     */
    public NDMDD NDMDD(NDNode root){
        return create(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NDMDD DD(){
        return NDMDD(Node());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NDMDD DD(INode root){
        if(root instanceof NDNode) NDMDD((NDNode) root);
        else throw new InputMismatchException("Expected the root to be at least of Node class !");
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public NDMDD copy(){
        // TODO ?
        return null;
    }

    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public NDNode getRoot(){
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NDNode getTt(){
        return tt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLayerSize(int i){
        return L.get(i).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<INode> iterateOnLayer(int i){
        return L.get(i);
    }

    @Override
    public ListOfLayer getLayers() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SetOf<Integer> iterateOnDomain(int index){
        return D.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDomainSize(int i){
        return D.get(i).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxValue(){
        return D.getMaxValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean domainContains(int i, int v){
        return D.get(i).contains(v);
    }

    /**
     * Get the Layer i
     * @param i depth of the layer
     * @return The Layer i
     */
    public ILayer getLayer(int i){
        return L.get(i);
    }

    /**
     * Get the set of all values that are in the MDD
     * @return The set of all values that are in the MDD
     */
    public Domains getDomains(){
        return D;
    }

    //**************************************//
    //               SETTERS                //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSize(int size){
        if(this.L.size() < size) for(int i = 0, diff = size - L.size(); i < diff; i++) L.add(ILayer.create());
        super.setSize(size);
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
     * {@inheritDoc}
     */
    @Override
    public void addNode(INode node, int layer){
        addNode((NDNode) node, layer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode(INode node, int layer){
        removeNode((NDNode) node, layer);
    }

    /**
     * Add the given Node to the given layer.
     * @param node The node to add
     * @param layer The index of the layer
     */
    public void addNode(NDNode node, int layer){
        getLayer(layer).add(node);
    }

    /**
     * Remove a node from the given layer
     * @param node The node to remove
     * @param layer The index of the layer
     */
    public void removeNode(NDNode node, int layer){
        getLayer(layer).removeAndFree(node);
    }

    /**
     * Add an arc between the source node and the destination node with the given value as label.
     * Ensures the connection between the two nodes
     * @param source The source node (parent)
     * @param value The value of the arc's label
     * @param destination The destination node (child)
     * @param layer The layer of the PARENT node (source)
     */
    public void addArc(NDNode source, int value, NDNode destination, int layer){
        source.addChild(value, destination);
        destination.addParent(value, source);
        addValue(value, layer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addArc(INode source, int value, INode destination, int layer){
        addArc((NDNode) source, value, (NDNode) destination, layer);
    }

    /**
     * Remove the arc from the source node with the given label's value.
     * Ensures to delete references of this arc in both nodes.
     * @param source The source node
     * @param value The value of the arc's label.
     */
    public void removeArc(NDNode source, int value){
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
    public void addArcAndNode(NDNode source, int value, NDNode destination, int layer){
        addArc(source, value, destination, layer-1);
        addNode(destination, layer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reduce(){
        // TODO ?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(){
        for(int i = 1; i < size(); i++) getLayer(i).freeAllNodes();
        D.clear();
        this.tt = null;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

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
        for(int i = 0; i < size(); i++){
            getLayer(i).freeAllNodes();
            Memory.free(getLayer(i));
        }
        Memory.free(root);
        L.clear();
        L.add(ILayer.create());
        D.clear();
        this.root = null;
        this.tt = null;
        dealloc();
    }

    /**
     * Call the allocator to free this object
     */
    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the NDMDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<NDMDD> {

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
        protected NDMDD[] arrayCreation(int capacity) {
            return new NDMDD[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected NDMDD createObject(int index) {
            return new NDMDD(index);
        }
    }

}
