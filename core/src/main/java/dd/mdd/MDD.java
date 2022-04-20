package dd.mdd;

import dd.DecisionDiagram;
import dd.interfaces.NodeInterface;
import dd.mdd.components.Layer;
import dd.mdd.components.Node;
import dd.operations.Pack;
import memory.*;
import representation.MDDVisitor;
import structures.Domains;
import structures.MDDTable;
import structures.arrays.ArrayOfInt;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.lists.ListOfInt;
import structures.lists.ListOfLayer;
import structures.lists.UnorderedListOfNode;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Random;

/**
 * <b>The class representing the MDD.</b> <br>
 * Contains a root node that is not null, a set of layers and a tt node if the MDD has been reduced.
 */
public class MDD extends DecisionDiagram {

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

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected MDD(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
        L.add(Layer.create());
    }

    /**
     * Create an MDD with given node as root.
     * The object is managed by the allocator.
     * @param root Node to use as a root
     * @return A fresh MDD
     */
    public static MDD create(Node root){
        MDD mdd = allocator().allocate();
        mdd.setRoot(root);
        return mdd;
    }

    /**
     * Create an MDD.
     * The object is managed by the allocator.
     * @return A fresh MDD
     */
    public static MDD create(){
        MDD mdd = allocator().allocate();
        mdd.setRoot(mdd.Node());
        return mdd;
    }

    /**
     * Free the current root node and add as a root the given node
     * @param root The node to set as root
     */
    public void setRoot(Node root){
        L.get(0).freeAllNodes();    // remove the current root
        this.root = root;           // set the pointer
        L.get(0).add(root);         // add the new root
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(NodeInterface root){
        if(root instanceof Node) setRoot((Node) root);
        else throw new InputMismatchException("Expected the root to be at least of Node class !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTT(){
        if(getLayer(size() - 1).size() == 1) this.tt = getLayer(size() - 1).getNode();
        else {
            Node newTT = Node();
            for (Node leaf : getLayer(size() - 1)) leaf.replaceReferencesBy(newTT);

            getLayer(size() - 1).clear();
            getLayer(size() - 1).add(newTT);
            this.tt = newTT;
        }
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
        for(int i = 0; i < size(); i++) getLayer(i).accept(visitor);
    }

    /**
     * If there is no root, return a default Node type associated with the MDD type
     * @return a Node the same type as the root Node.
     */
    public Node Node(){
        if(root != null) return (Node) root.Node();
        return Node.create();
    }

    /**
     * Create an MDD.
     * @return a new MDD
     */
    public MDD MDD(){
        return create(Node());
    }

    /**
     * Create an MDD with given node as root.
     * @param root The node to set as root
     * @return a new MDD with given node as root.
     */
    public MDD MDD(Node root){
        return create(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDD DD(){
        return MDD(Node());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDD DD(NodeInterface root){
        if(root instanceof Node) MDD((Node) root);
        else throw new InputMismatchException("Expected the root to be at least of Node class !");
        return null;
    }


    // -----------------
    //      COPY

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
                NodeInterface copyNode = original.getX1();
                for(int arc : original.getChildren()){
                    Node child = original.getChild(arc);
                    NodeInterface copyChild = child.getX1();
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
     * @param root The root node of the MDD
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
     * {@inheritDoc}
     */
    @Override
    public MDD copy(){
        return copy(DD());
    }


    // -----------------
    //     REPLACE

    /**
     * Replace the values of all arcs according to the given mapping.
     * For instance, you want to replace all arcs with value 0 by [1, 2] :
     * all you have to do is make a mapping 0 → {1, 2} and give it as an input.
     * @param mapping The mapping of the values
     */
    public void replace(MapOf<Integer, SetOf<Integer>> mapping){
        replace(mapping, 0, size());
    }

    /**
     * Replace the values of all arcs according to the given mapping, from layer start to layer stop.
     * For instance, you want to replace all arcs with value 0 by [1, 2] :
     * all you have to do is make a mapping 0 → {1, 2} and give it as an input.
     * @param mapping The mapping of the values
     * @param start The first layer of the operation
     * @param stop The last layer of the operation
     */
    public void replace(MapOf<Integer, SetOf<Integer>> mapping, int start, int stop){
        SetOf<Integer> setV = Memory.SetOfInteger();
        for(int i = start; i < stop - 1; i++){
            this.D.get(i).clear();
            for(Node node : getLayer(i)) {
                node.replace(mapping, setV);
                this.D.get(i).add(setV);
            }
        }
        Memory.free(setV);
    }

    /**
     * Replace the values of all arcs according to the given mapping, from layer start to layer stop.
     * For instance, you want to replace all arcs with value 0 by [1, 2] :
     * all you have to do is make a mapping 0 → {1, 2} and give it as an input.
     * This function takes an array of map, so that you can have a map for each layer.
     * mapping[0] corresponds to the mapping for layer "start".
     * @param mapping The mapping of the values
     * @param start The first layer of the operation
     * @param stop The last layer of the operation
     */
    public void replace(MapOf<Integer, SetOf<Integer>>[] mapping, int start, int stop){
        SetOf<Integer> setV = Memory.SetOfInteger();
        for(int i = start; i < stop - 1; i++){
            this.D.get(i).clear();
            for(Node node : getLayer(i)) {
                node.replace(mapping[i-start], setV);
                this.D.get(i).add(setV);
            }
        }
        Memory.free(setV);
    }

    /**
     * Replace the values of all arcs according to the map : [ 0 → V0, 1 → V1 ].
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


    // -----------------
    //    RANDOM WALK

    /**
     * Perform a random walk in the MDD.
     * FOR TESTING PURPOSES
     * @return A valid path in the MDD
     */
    public int[] randomWalk(){
        Node current = root;
        Random random = new Random();
        int[] path = new int[size()-1];
        int i=0;
        while (current != tt){
            int idx = random.nextInt(current.numberOfChildren());
            path[i++] = current.getValue(idx);
            current = current.getChild(path[i-1]);
        }
        return path;
    }

    public int[] stochasticRandomWalk(HashMap<Integer, Double> probabilities){
        Node current = root;
        Random random = new Random();
        int[] path = new int[size()-1];
        int i=0;
        while (current != tt){
            double sumProba = 0.0;
            for(int idx = 0; idx < current.numberOfChildren(); idx++){
                sumProba += probabilities.get(current.getValue(idx));
            }
            double randomSelection = random.nextDouble() * sumProba;
            double cumulativeProba = probabilities.get(current.getValue(0));
            int idx = 1;
            while (cumulativeProba < randomSelection && idx < current.numberOfChildren()){
                cumulativeProba += probabilities.get(current.getValue(idx));
                idx++;
            }
            path[i++] = current.getValue(idx-1);
            current = current.getChild(path[i-1]);
        }
        return path;
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getRoot(){
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getTt(){
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
    public Iterable iterateOnLayer(int i){
        return L.get(i);
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
    public Layer getLayer(int i){
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
        if(this.L.size() < size) for(int i = 0, diff = size - L.size(); i < diff; i++) L.add(Layer.create());
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
    public void addNode(NodeInterface node, int layer){
        addNode((Node) node, layer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode(NodeInterface node, int layer){
        removeNode((Node) node, layer);
    }

    /**
     * Add the path corresponding to the given values from the MDD's root.
     * @param values Labels of the path
     */
    public void addPath(ArrayOfInt values){
        Node current = getRoot();
        for(int i = 0; i < values.length; i++) {
            int v = values.get(i);
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
     * @param layer The layer of the PARENT node (source)
     */
    public void addArc(Node source, int value, Node destination, int layer){
        source.addChild(value, destination);
        destination.addParent(value, source);
        addValue(value, layer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addArc(NodeInterface source, int value, NodeInterface destination, int layer){
        addArc((Node) source, value, (Node) destination, layer);
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
     * {@inheritDoc}
     */
    @Override
    public void reduce(){
        // Merge the leaves of the MDD into a tt node
        if(getLayer(size() - 1).size() == 1) this.tt = getLayer(size() - 1).getNode();
        else {
            Node newTT = Node();
            for (Node leaf : getLayer(size() - 1)) leaf.replaceReferencesBy(newTT);
            getLayer(size() - 1).clear();
            getLayer(size() - 1).add(newTT);
            this.tt = newTT;
        }

        // Merge similar nodes
        SetOf<Integer> V = Memory.SetOfInteger();
        D.fillWithValues(V);
        if(size() > 1 && getLayer(size() - 2).size() != 0) Pack.pReduce(L, size(), V);
        //if(size > 1 && getLayer(size - 2).size() != 0) HashReduce.reduce(this);
        Memory.free(V);
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
    //           TABLE FUNCTIONS            //
    //**************************************//

    /**
     * Fill the given array with values from the table's given row.
     * @param table The table
     * @param row The row
     * @param values The array to fill
     * @return The array filled with values from the given row
     */
    private static ArrayOfInt fillTupleValue(MDDTable table, int row, ArrayOfInt values){
        for(int i = 0; i < values.length; i++) values.set(i, table.valueOfIndex(row, i));
        return values;
    }

    /**
     * Create the MDD corresponding to the given Table.
     * @param table The table
     * @return The MDD corresponding to the table
     */
    public static MDD createFromTable(MDDTable table){
        MDD result = MDD.create();
        result.setSize(table.tupleSize() + 1);

        ArrayOfInt values = ArrayOfInt.create(table.tupleSize());
        for(int i = 0; i < table.numberOfTuples(); i++) result.addPath(fillTupleValue(table, i, values));
        Memory.free(values);

        result.reduce();
        return result;
    }

    /**
     * Create the MDD corresponding to the given <b>SORTED</b> Table.
     * @param table The sorted table
     * @return The MDD corresponding to the sorted table
     */
    public static MDD createFromSortedTable(MDDTable table){
        MDD result = MDD.create();

        ListOfInt nextIndices = ListOfInt.create();
        ListOfInt indices = ListOfInt.create();
        ListOfInt tmp_switch_i;
        indices.add(0);

        UnorderedListOfNode nextNodes = UnorderedListOfNode.create();
        UnorderedListOfNode nodes = UnorderedListOfNode.create();
        UnorderedListOfNode tmp_switch_n;
        nodes.add(result.getRoot());

        int size = table.numberOfTuples();
        int length = table.tupleSize();
        result.setSize(length+1);

        Node source;
        int lastIndex;
        int maxRowCurrentIndex;
        int value;

        for(int column = 0; column < size; column++) {
            source = nodes.get(0);
            value = table.noneValue();
            lastIndex = 0;
            if(indices.size() > 1) maxRowCurrentIndex = indices.get(1);
            else maxRowCurrentIndex = Integer.MAX_VALUE;
            for(int row = 0; row < length; row++) {
                if(table.valueOfIndex(row, column) == value && maxRowCurrentIndex > row) continue;
                value = table.valueOfIndex(row, column);

                if(maxRowCurrentIndex <= row) {
                    lastIndex++;
                    if(indices.size() > lastIndex+1) maxRowCurrentIndex = indices.get(lastIndex+1);
                    else maxRowCurrentIndex = Integer.MAX_VALUE;
                    source = nodes.get(lastIndex);
                }

                Node elementNode = result.Node();
                result.addArcAndNode(source, value, elementNode, column + 1);
                nextIndices.add(row);
                nextNodes.add(elementNode);
            }
            nodes.clear();
            tmp_switch_n = nextNodes;
            nextNodes = nodes;
            nodes = tmp_switch_n;

            indices.clear();
            tmp_switch_i = nextIndices;
            nextIndices = indices;
            indices = tmp_switch_i;
        }

        Memory.free(nodes);
        Memory.free(nextNodes);
        Memory.free(indices);
        Memory.free(nextIndices);
        result.reduce();
        return result;
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
        L.add(Layer.create());
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
     * <b>The allocator that is in charge of the MDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<MDD> {

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
        protected MDD[] arrayCreation(int capacity) {
            return new MDD[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected MDD createObject(int index) {
            return new MDD(index);
        }
    }

}
