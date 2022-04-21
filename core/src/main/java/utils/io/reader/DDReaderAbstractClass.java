package utils.io.reader;

import dd.DecisionDiagram;
import dd.bdd.BDD;
import dd.interfaces.CostNodeInterface;
import dd.interfaces.NodeInterface;
import dd.mdd.MDD;
import dd.mdd.costmdd.CostMDD;
import utils.SmallMath;
import utils.io.MDDReader;

import java.io.IOException;
import java.util.HashMap;

public abstract class DDReaderAbstractClass {

    private HashMap<NodeInterface, Integer> next, current;
    private HashMap<Integer, NodeInterface> nextR, currentR;
    private byte[][] elements;
    private int nID;
    private byte MODE;

    /**
     * Initialise the HashMaps to bind AbstractNode to Integer (and vice-versa)
     */
    protected void initMaps(){
        if(next == null) next = new HashMap<>();
        else next.clear();
        if(current == null) current = new HashMap<>();
        else current.clear();
        if(nextR == null) nextR = new HashMap<>();
        else nextR.clear();
        if(currentR == null) currentR = new HashMap<>();
        else currentR.clear();
    }

    /**
     * Reset the ID counter.<br>
     * The ID counter is used to automatically associate an ID to a AbstractNode
     */
    protected void resetIDCounter(){
        nID = 0;
    }

    /**
     * Swap current and next maps.<br>
     * Clear the next maps.
     */
    protected void swapMaps(){
        HashMap<Integer, NodeInterface> tmp = currentR;
        currentR = nextR;
        nextR = tmp;
        nextR.clear();

        HashMap<NodeInterface, Integer> tmpR = current;
        current = next;
        next = tmpR;
        next.clear();
    }

    /**
     * Set the write/load mode.
     * @param MODE Write/load mode
     */
    protected void setMODE(byte MODE){
        this.MODE = MODE;
    }

    /**
     * Bind a node to a new automatically generated ID iff it is not yet bound.<br>
     * Return the ID of the given node.
     * @param node Node to associate
     * @param map Map to put the association
     * @param mapR Reverse of the map
     * @return The ID of the given node.
     */
    private int safeBind(NodeInterface node, HashMap<NodeInterface, Integer> map, HashMap<Integer, NodeInterface> mapR){
        Integer value = map.get(node);
        if(value != null) return value;
        map.put(node, nID);
        mapR.put(nID++, node);
        return nID-1;
    }

    /**
     * Bind the node to the current layer during the write operation.
     * @param node The node to bind
     * @return The ID of the given node
     */
    protected void firstBind(NodeInterface node){
        current.put(node, 0);
        currentR.put(0, node);
    }

    /**
     * Bind the node to the next layer during the write operation.
     * @param node The node to bind
     * @return The ID of the given node
     */
    protected int bindNextWrite(NodeInterface node){
        return safeBind(node, next, nextR);
    }

    /**
     * Bind the node to the given ID for the current layer during the read operation.
     * @param ID The ID of the node
     * @param node The node to associate the ID with
     */
    protected void bindCurrentRead(int ID, NodeInterface node){
        currentR.put(ID, node);
    }

    /**
     * Add the node to the DecisionDiagram at the given layer.
     * @param dd The DecisionDiagram in which the node is added
     * @param layer The layer in which the node is added
     * @param ID The ID of the node to add
     * @return The node added.
     */
    protected NodeInterface addNodeToDD(DecisionDiagram dd, int layer, int ID){
        NodeInterface node = nextR.get(ID);
        if(node == null) {
            node = dd.Node();
            nextR.put(ID, node);
            dd.addNode(node, layer);
        }
        return node;
    }

    /**
     * Get the node corresponding to the given ID in the current layer
     * @param ID The ID of the node
     * @return The node associated with the given ID
     */
    protected NodeInterface getNode(int ID){
        return currentR.get(ID);
    }

    /**
     * Write an integer to the file.
     * @param file The file in which we write
     * @param element The nature of the element to write
     * @param value The value to write
     * @throws IOException
     */
    protected void writeInt(MDDFileWriter file, byte element, int value) throws IOException {
        file.write(SmallMath.intToBytes(elements[element], value));
    }

    /**
     * Read an integer from the file.
     * @param file The file in which we read
     * @param element The nature of the element to read
     * @throws IOException
     */
    protected int readInt(MDDFileReader file, byte element) throws IOException {
        file.read(elements[element]);
        return SmallMath.bytesToInt(elements[element]);
    }

    /**
     * Write the header of the .mdd file.<br>
     * Automatically compute the best space to allocate to each component.<br>
     * Initialise the array of elements to fit the best space.
     * @param dd The DecisionDiagram
     * @param file The file in which we write
     * @throws IOException
     */
    protected void writeHeader(DecisionDiagram dd, MDDFileWriter file) throws IOException {
        int nodes = 0;
        int max_degree = 0;
        int max_in_degree = 0;
        int value_number = 0;
        int cost = 0;
        for(int i = 0; i < dd.size(); i++) {
            nodes = Math.max(nodes, dd.getLayerSize(i));
            for(NodeInterface node : dd.iterateOnLayer(i)) {
                max_degree = Math.max(max_degree, node.numberOfChildren());
                value_number = Math.max(value_number, node.numberOfParentsLabel());
                for(int value : node.iterateOnParentLabels()) {
                    int in_degree = node.numberOfParents(value);
                    if(in_degree > max_in_degree) max_in_degree = in_degree;
                }
                if(node instanceof CostNodeInterface) {
                    CostNodeInterface costNode = (CostNodeInterface) node;
                    for(int value : costNode.iterateOnChildLabels()) cost = Math.max(cost, costNode.getArcCost(value));
                }
            }
        }

        if(elements == null || elements.length < 7) elements = new byte[7][];
        elements[MDDReader.NODE] =            new byte[SmallMath.nBytes(nodes)];
        elements[MDDReader.VALUE] =           new byte[SmallMath.nBytes(dd.getMaxValue())];
        elements[MDDReader.PARENT_NUMBER] =   new byte[SmallMath.nBytes(max_in_degree)];
        elements[MDDReader.VALUE_NUMBER] =    new byte[SmallMath.nBytes(value_number)];
        elements[MDDReader.SIZE] =            new byte[SmallMath.nBytes(dd.size())];
        elements[MDDReader.MAX_OUT_DEGREE] =  new byte[SmallMath.nBytes(max_degree)];
        elements[MDDReader.COST] =            new byte[SmallMath.nBytes(cost)];

        // Write the type of DD saved
        if(dd instanceof CostMDD) file.write(MDDReader.COST_MDD);
        else if (dd instanceof MDD) file.write(MDDReader.MDD);
        else if (dd instanceof BDD) file.write(MDDReader.BDD);

        file.write(MODE);
        for(int i = 0; i < elements.length; i++) file.write(elements[i].length);
    }

    /**
     * Read the header of the .mdd file.<br>
     * Initialise the array of elements to the specified spaces.
     * @param file The file to read
     * @throws IOException
     */
    protected void readHeader(MDDFileReader file) throws IOException {
        if(elements == null) elements = new byte[7][];
        for(int i = 0; i < elements.length; i++){
            byte b = file.nextByte();
            elements[i] = new byte[b];
        }
    }

    /**
     * Save a node of the DecisionDiagram in the given file.
     * @param node The node to save
     * @param nodeID The ID of the node to save
     * @param file The file in which the node is saved.
     * @throws IOException
     */
    protected abstract void saveNode(NodeInterface node, int nodeID, MDDFileWriter file) throws IOException;

    /**
     * Save a layer of the DecisionDiagram in the given file.
     * @param dd The DecisionDiagram
     * @param layer The layer to save
     * @param file The file in which the layer is saved.
     * @throws IOException
     */
    protected void saveLayer(DecisionDiagram dd, int layer, MDDFileWriter file) throws IOException {
        int numberOfNodes = dd.getLayerSize(layer);
        writeInt(file, MDDReader.NODE, numberOfNodes);

        for(int nodeID = 0; nodeID < numberOfNodes; nodeID++){
            NodeInterface node = currentR.get(nodeID);
            saveNode(node, nodeID, file);
        }
    }

    /**
     * Save a DecisionDiagram in the given file.
     * @param dd The DecisionDiagram to save
     * @param file The file in which the DecisionDiagram is saved
     * @throws IOException
     */
    public abstract void save(DecisionDiagram dd, MDDFileWriter file) throws IOException;

    /**
     * Load a node from the given file and add it to the given DecisionDiagram
     * @param dd The DecisionDiagram in which the node is added
     * @param node The node to load
     * @param layer The layer in which the node is added
     * @param file The file from which the node is loaded
     * @throws IOException
     */
    protected abstract void loadNode(DecisionDiagram dd, NodeInterface node, int layer, MDDFileReader file) throws IOException;

    /**
     * Load a layer from the given file and add it to the given DecisionDiagram
     * @param dd The DecisionDiagram in which the layer is added
     * @param layer The index of the layer to load
     * @param file The file from which the layer is loaded
     * @throws IOException
     */
    protected void loadLayer(DecisionDiagram dd, int layer, MDDFileReader file) throws IOException {
        // Number of nodes
        int numberOfNodes = readInt(file, MDDReader.NODE);

        for(int i = 0; i < numberOfNodes; i++) {
            NodeInterface node = getNode(i);
            loadNode(dd, node, layer, file);
        }
    }

    /**
     * Load a DecisionDiagram from the given file
     * @param dd The DecisionDiagram in which the DecisionDiagram loaded will be saved
     * @param file The file from which the DecisionDiagram is loaded
     * @throws IOException
     */
    public abstract void load(DecisionDiagram dd, MDDFileReader file) throws IOException;

}
