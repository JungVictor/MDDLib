package dd.proxy;

import dd.AbstractNode;
import dd.DecisionDiagram;
import dd.interfaces.NodeInterface;
import memory.Memory;
import structures.generics.MapOf;
import structures.lists.ListOfInt;
import utils.SmallMath;
import utils.io.MDDReader;

import java.io.FileInputStream;
import java.io.IOException;

public class DDProxy extends DecisionDiagram {

    private final int allocatedIndex;

    // The MDD in byte[]
    private byte[] DATA;

    // The array of elements
    private byte[][] elements;

    // The number of nodes in the current layer
    private int currentLayerNumber;
    private int nodePosition;

    // The position of the reader in the DATA array
    private int position;
    private int size;

    // Map between ProxyNodes and their ID
    private MapOf<Integer, AbstractNode> previousNodes;
    private MapOf<Integer, AbstractNode> nodes;
    private ListOfInt nodesID;

    private NodeProxy root;

    private DDProxy(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public static DDProxy create(FileInputStream file){
        DDProxy proxy = new DDProxy(0);

        try {
            proxy.DATA = file.readAllBytes();
            proxy.position = 0;
            // Mode
            byte b = proxy.DATA[proxy.position++];
            if(b != MDDReader.TOP_DOWN) throw new IllegalArgumentException("The DD must be top-down defined !");
            if(proxy.elements == null) proxy.elements = new byte[6][];
            for(int i = 0; i < proxy.elements.length; i++){
                b = proxy.DATA[proxy.position++];
                proxy.elements[i] = new byte[b];
            }
            // Skip the size
            proxy.size = proxy.read(MDDReader.SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        proxy.previousNodes = Memory.MapOfIntegerAbstractNode();
        proxy.nodes = Memory.MapOfIntegerAbstractNode();
        proxy.nodesID = ListOfInt.create();

        proxy.nodes.put(0, NodeProxy.create(proxy));
        proxy.nodesID.add(0);
        proxy.loadNodes();
        proxy.root = (NodeProxy) proxy.previousNodes.get(0);

        return proxy;
    }

    /**
     * Read the next sequence of bytes as an integer.
     * @param element The type of element to read
     * @return The integer corresponding to the asked element.
     */
    private int read(byte element){
        for(int i = 0; i < elements[element].length; i++) elements[element][i] = DATA[i+position];
        position += elements[element].length;
        return SmallMath.bytesToInt(elements[element]);
    }

    /**
     * Skip the current node and put the position at the beginning of the next node
     */
    private void goToNextNode(){
        // MAX_OUT_DEGREE VALUE CHILD_ID
        int max_out_degree = read(MDDReader.MAX_OUT_DEGREE);
        // Skip all children
        position += max_out_degree*(elements[MDDReader.VALUE].length + elements[MDDReader.NODE].length);
        nodePosition++;
    }

    /**
     * Create a NodeProxy corresponding to the definition of the node at the given ID.
     * @param nodeID The ID of the node to load
     */
    private NodeProxy loadNode(int nodeID){
        NodeProxy node = (NodeProxy) nodes.get(nodeID);
        // MAX_OUT_DEGREE VALUE CHILD_ID
        int max_out_degree = read(MDDReader.MAX_OUT_DEGREE);
        for(int i = 0; i < max_out_degree; i++){
            int value = read(MDDReader.VALUE);
            int childID = read(MDDReader.NODE);
            node.addChild(value, childID);
        }
        node.setLoaded(true);
        nodePosition++;
        return node;
    }

    /**
     * Load in memory all nodes corresponding to the given list of IDs
     */
    protected void loadNodes(){
        // Free the memory
        for(AbstractNode node : previousNodes.values()){
            Memory.free(node);
        }
        previousNodes.clear();

        // Must go to the next layer
        while(nodePosition < currentLayerNumber) goToNextNode();
        nodePosition = 0;

        // Number of nodes in the layer
        currentLayerNumber = read(MDDReader.NODE);

        // ID are ordered
        nodesID.sort();
        for(int nodeID : nodesID){
            while (nodePosition < nodeID) goToNextNode();
            loadNode(nodeID);
        }
        nodesID.clear();
        MapOf<Integer, AbstractNode> tmp = previousNodes;
        previousNodes = nodes;
        nodes = tmp;
    }

    /**
     * NodeProxy.getChild()
     * @param nodeID
     * @return
     */
    protected AbstractNode getNode(int nodeID){
        AbstractNode node = nodes.get(nodeID);
        if(node == null) {
            node = NodeProxy.create(this);
            nodes.put(nodeID, node);
            nodesID.add(nodeID);
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(){
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(NodeInterface root) {
        throw new UnsupportedOperationException("Proxy DDs are read only !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTT() {
        throw new UnsupportedOperationException("Proxy DDs are read only !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNode Node() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DecisionDiagram DD() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DecisionDiagram DD(NodeInterface root) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNode getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNode getTt() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<NodeInterface> iterateOnLayer(int i) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLayerSize(int i) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> iterateOnDomain(int i) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDomainSize(int i) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxValue() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean domainContains(int i, int v) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNode(NodeInterface node, int layer) {
        throw new UnsupportedOperationException("Proxy DDs are read only !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode(NodeInterface node, int layer) {
        throw new UnsupportedOperationException("Proxy DDs are read only !");
    }

    @Override
    public void reduce() {
        throw new UnsupportedOperationException("Proxy DDs are read only !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Proxy DDs are read only !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        previousNodes.clear();
        nodes.clear();
        nodesID.clear();
        currentLayerNumber = 0;
        nodePosition = 0;
        position = 0;
    }

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
        Memory.free(previousNodes);
        Memory.free(nodes);
        Memory.free(nodesID);
    }
}
