package utils.io.reader;

import dd.AbstractNode;
import dd.DecisionDiagram;
import utils.SmallMath;
import utils.io.MDDReader;

import java.io.IOException;
import java.util.HashMap;

public abstract class DDReaderAbstractClass {

    private HashMap<AbstractNode, Integer> nextWrite, currentWrite;
    private HashMap<Integer, AbstractNode> nextRead, currentRead;
    private byte[][] elements;
    private byte[] b = new byte[1];
    private int nID;
    private byte MODE;

    protected void initMaps(){
        if(nextWrite == null) nextWrite = new HashMap<>();
        else nextWrite.clear();
        if(currentWrite == null) currentWrite = new HashMap<>();
        else currentWrite.clear();
        if(nextRead == null) nextRead = new HashMap<>();
        else nextRead.clear();
        if(currentRead == null) currentRead = new HashMap<>();
        else currentRead.clear();
    }
    protected void resetIDCounter(){
        nID = 0;
    }
    protected void swapWriteBinding(){
        HashMap<AbstractNode, Integer> tmpWrite = currentWrite;
        currentWrite = nextWrite;
        nextWrite = tmpWrite;
        nextWrite.clear();
    }
    protected void swapReadBinding(){
        HashMap<Integer, AbstractNode> tmpWrite = currentRead;
        currentRead = nextRead;
        nextRead = tmpWrite;
        nextRead.clear();
    }

    protected void setMODE(byte MODE){
        this.MODE = MODE;
    }

    protected int safeBind(AbstractNode node, HashMap<AbstractNode, Integer> map){
        Integer value = map.get(node);
        if(value != null) return value;
        map.put(node, nID++);
        return nID-1;
    }
    protected int bindCurrentWrite(AbstractNode node){
        return safeBind(node, currentWrite);
    }
    protected int bindNextWrite(AbstractNode node){
        return safeBind(node, nextWrite);
    }

    protected void bindCurrentRead(int ID, AbstractNode node){
        currentRead.put(ID, node);
    }
    protected void bindNextRead(int ID, AbstractNode node){
        nextRead.put(ID, node);
    }
    protected AbstractNode addNodeToDD(DecisionDiagram dd, int layer, int ID){
        AbstractNode node = nextRead.get(ID);
        if(node == null) {
            node = dd.Node();
            nextRead.put(ID, node);
            dd.addNode(node, layer);
        }
        return node;
    }

    protected int getID(AbstractNode node){
        return currentWrite.get(node);
    }
    protected AbstractNode getNode(int ID){
        return currentRead.get(ID);
    }
    protected void writeInt(MDDFileWriter file, byte element, int value) throws IOException {
        file.write(SmallMath.intToBytes(elements[element], value));
    }
    protected int readInt(MDDFileReader file, byte element) throws IOException {
        file.read(elements[element]);
        return SmallMath.bytesToInt(elements[element]);
    }

    protected void writeHeader(DecisionDiagram dd, MDDFileWriter file) throws IOException {
        int nodes = 0;
        for(int i = 0; i < dd.size(); i++) nodes = Math.max(nodes, dd.getLayerSize(i));

        if(elements == null) elements = new byte[5][];
        elements[MDDReader.NODE] =            new byte[SmallMath.nBytes(nodes)];
        elements[MDDReader.VALUE] =           new byte[2];
        elements[MDDReader.PARENT_NUMBER] =   new byte[1];
        elements[MDDReader.VALUE_NUMBER] =    new byte[2];
        elements[MDDReader.SIZE] =            new byte[1];

        file.write(MODE);
        file.write(elements[MDDReader.NODE].length);
        file.write(elements[MDDReader.VALUE].length);
        file.write(elements[MDDReader.PARENT_NUMBER].length);
        file.write(elements[MDDReader.VALUE_NUMBER].length);
        file.write(elements[MDDReader.SIZE].length);
    }
    protected void readHeader(MDDFileReader file) throws IOException {
        if(elements == null) elements = new byte[5][];
        for(int i = 0; i < elements.length; i++){
            file.read(b);
            elements[i] = new byte[b[0]];
        }
    }

    protected abstract void saveNode(AbstractNode node, int nodeID, MDDFileWriter file) throws IOException;
    protected void saveLayer(DecisionDiagram dd, int layer, MDDFileWriter file) throws IOException {
        int numberOfNodes = dd.getLayerSize(layer);
        writeInt(file, MDDReader.NODE, numberOfNodes);
        Iterable<AbstractNode> nodes = dd.iterateOnLayer(layer);
        for(AbstractNode node : nodes){
            int nodeID = currentWrite.get(node);
            saveNode(node, nodeID, file);
        }
    }
    public abstract void save(DecisionDiagram dd, MDDFileWriter file) throws IOException;

    protected abstract void loadNode(DecisionDiagram dd, AbstractNode node, int layer, MDDFileReader file) throws IOException;
    protected void loadLayer(DecisionDiagram dd, int layer, MDDFileReader file) throws IOException {
        // Number of nodes
        int numberOfNodes = readInt(file, MDDReader.NODE);

        for(int i = 0; i < numberOfNodes; i++) {
            // Node ID
            int nodeID = readInt(file, MDDReader.NODE);

            AbstractNode node = getNode(nodeID);
            loadNode(dd, node, layer, file);
        }
    }
    public abstract void load(DecisionDiagram dd, MDDFileReader file) throws IOException;

}
