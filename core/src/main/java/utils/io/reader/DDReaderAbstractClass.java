package utils.io.reader;

import dd.AbstractNode;
import dd.DecisionDiagram;
import utils.SmallMath;
import utils.io.MDDReader;

import java.io.IOException;
import java.util.HashMap;

public abstract class DDReaderAbstractClass {

    private HashMap<AbstractNode, Integer> next, current;
    private HashMap<Integer, AbstractNode> nextR, currentR;
    private byte[][] elements;
    private int nID;
    private byte MODE;

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
    protected void resetIDCounter(){
        nID = 0;
    }
    protected void swapWriteBinding(){
        swapReadBinding();
        HashMap<AbstractNode, Integer> tmp = current;
        current = next;
        next = tmp;
        next.clear();
    }
    protected void swapReadBinding(){
        HashMap<Integer, AbstractNode> tmp = currentR;
        currentR = nextR;
        nextR = tmp;
        nextR.clear();
    }

    protected void setMODE(byte MODE){
        this.MODE = MODE;
    }

    protected int safeBind(AbstractNode node, HashMap<AbstractNode, Integer> map, HashMap<Integer, AbstractNode> mapR){
        Integer value = map.get(node);
        if(value != null) return value;
        map.put(node, nID);
        mapR.put(nID++, node);
        return nID-1;
    }
    protected int bindCurrentWrite(AbstractNode node){
        return safeBind(node, current, currentR);
    }
    protected int bindNextWrite(AbstractNode node){
        return safeBind(node, next, nextR);
    }

    protected void bindCurrentRead(int ID, AbstractNode node){
        currentR.put(ID, node);
    }
    protected void bindNextRead(int ID, AbstractNode node){
        nextR.put(ID, node);
    }
    protected AbstractNode addNodeToDD(DecisionDiagram dd, int layer, int ID){
        AbstractNode node = nextR.get(ID);
        if(node == null) {
            node = dd.Node();
            nextR.put(ID, node);
            dd.addNode(node, layer);
        }
        return node;
    }

    protected int getID(AbstractNode node){
        return current.get(node);
    }
    protected AbstractNode getNode(int ID){
        return currentR.get(ID);
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
        int max_degree = 0;
        int value_number = 0;
        for(int i = 0; i < dd.size(); i++) {
            nodes = Math.max(nodes, dd.getLayerSize(i));
            for(AbstractNode node : dd.iterateOnLayer(i)) {
                max_degree = Math.max(max_degree, node.numberOfChildren());
                value_number = Math.max(value_number, node.numberOfParentsLabel());
            }
        }

        if(elements == null) elements = new byte[6][];
        elements[MDDReader.NODE] =            new byte[SmallMath.nBytes(nodes)];
        elements[MDDReader.VALUE] =           new byte[SmallMath.nBytes(dd.getMaxValue())];
        elements[MDDReader.PARENT_NUMBER] =   new byte[1];
        elements[MDDReader.VALUE_NUMBER] =    new byte[SmallMath.nBytes(value_number)];
        elements[MDDReader.SIZE] =            new byte[1];
        elements[MDDReader.MAX_OUT_DEGREE] =  new byte[SmallMath.nBytes(max_degree)];

        file.write(MODE);
        file.write(elements[MDDReader.NODE].length);
        file.write(elements[MDDReader.VALUE].length);
        file.write(elements[MDDReader.PARENT_NUMBER].length);
        file.write(elements[MDDReader.VALUE_NUMBER].length);
        file.write(elements[MDDReader.SIZE].length);
        file.write(elements[MDDReader.MAX_OUT_DEGREE].length);
    }
    protected void readHeader(MDDFileReader file) throws IOException {
        if(elements == null) elements = new byte[6][];
        for(int i = 0; i < elements.length; i++){
            byte b = file.nextByte();
            elements[i] = new byte[b];
        }
    }

    protected abstract void saveNode(AbstractNode node, int nodeID, MDDFileWriter file) throws IOException;
    protected void saveLayer(DecisionDiagram dd, int layer, MDDFileWriter file) throws IOException {
        int numberOfNodes = dd.getLayerSize(layer);
        writeInt(file, MDDReader.NODE, numberOfNodes);

        for(int nodeID = 0; nodeID < numberOfNodes; nodeID++){
            AbstractNode node = currentR.get(nodeID);
            saveNode(node, nodeID, file);
        }



    }
    public abstract void save(DecisionDiagram dd, MDDFileWriter file) throws IOException;

    protected abstract void loadNode(DecisionDiagram dd, AbstractNode node, int layer, MDDFileReader file) throws IOException;
    protected void loadLayer(DecisionDiagram dd, int layer, MDDFileReader file) throws IOException {
        // Number of nodes
        int numberOfNodes = readInt(file, MDDReader.NODE);

        for(int i = 0; i < numberOfNodes; i++) {
            AbstractNode node = getNode(i);
            loadNode(dd, node, layer, file);
        }
    }
    public abstract void load(DecisionDiagram dd, MDDFileReader file) throws IOException;

}
