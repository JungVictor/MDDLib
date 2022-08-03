package utils.io.reader;

import dd.DecisionDiagram;
import dd.bdd.BDD;
import dd.interfaces.ICostNode;
import dd.interfaces.INode;
import dd.mdd.MDD;
import dd.mdd.costmdd.CostMDD;
import dd.operations.Pack;
import memory.Memory;
import structures.generics.SetOf;
import utils.SmallMath;
import utils.io.MDDReader;

import java.io.IOException;
import java.util.HashMap;

public class DDSaverTopDownOTF {

    protected HashMap<INode, Integer> next, current;
    protected HashMap<Integer, INode> nextR, currentR;
    private byte[][] elements;
    private int nID;

    public DDSaverTopDownOTF(){
    }

    /**
     * Initialise the HashMaps to bind AbstractNode to Integer (and vice-versa)
     */
    private void initMaps(){
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
     * Swap current and next maps.<br>
     * Clear the next maps.
     */
    public void swapMaps(){
        HashMap<Integer, INode> tmp = currentR;
        currentR = nextR;
        nextR = tmp;
        nextR.clear();

        HashMap<INode, Integer> tmpR = current;
        current = next;
        next = tmpR;
        next.clear();

        nID = 0;
    }

    /**
     * Bind a node to a new automatically generated ID iff it is not yet bound.<br>
     * Return the ID of the given node.
     * @param node Node to associate
     * @param map Map to put the association
     * @param mapR Reverse of the map
     * @return The ID of the given node.
     */
    private int safeBind(INode node, HashMap<INode, Integer> map, HashMap<Integer, INode> mapR){
        Integer value = map.get(node);
        if(value != null) return value;
        map.put(node, nID);
        mapR.put(nID++, node);
        return nID-1;
    }

    /**
     * Bind the node to the next layer during the write operation.
     * @param node The node to bind
     * @return The ID of the given node
     */
    private int bindNextWrite(INode node){
        return safeBind(node, next, nextR);
    }

    /**
     * Write an integer to the file.
     * @param file The file in which we write
     * @param element The nature of the element to write
     * @param value The value to write
     * @throws IOException
     */
    private void writeInt(MDDFileWriter file, byte element, int value) throws IOException {
        file.write(SmallMath.intToBytes(elements[element], value));
    }

    /**
     * Instantly write an integer to the file.
     * @param file The file in which we write
     * @param element The nature of the element to write
     * @param value The value to write
     * @throws IOException
     */
    private void instantWriteInt(MDDFileWriter file, byte element, int value) throws IOException {
        file.instantWrite(SmallMath.intToBytes(elements[element], value));
    }

    public INode getNode(int ID){
        return currentR.get(ID);
    }

    public void saveNode(INode node, MDDFileWriter file) throws IOException {
        int numberOfValues = node.numberOfChildren();
        writeInt(file, MDDReader.MAX_OUT_DEGREE, numberOfValues);
        for(int value : node.iterateOnChildLabels()){
            writeInt(file, MDDReader.VALUE, value);
            INode child = node.getChild(value);
            int childID = bindNextWrite(child);
            writeInt(file, MDDReader.NODE, childID);
        }
    }

    public long tmpWriteNumberOfNodes(MDDFileWriter file) throws IOException {
        long ptr = file.getPointer();
        // Temp. write 0 as value, yet unknown!
        // Use writeNumberOfNodesAtLayer() to override this 0!
        writeInt(file, MDDReader.NODE, 0);
        return ptr;
    }

    public void writeNumberOfNodesAtLayer(MDDFileWriter file, long ptr, int numberOfNodes) throws IOException {
        long current = file.getPointer();
        // Must flush before instant writing... otherwise we might be writing somewhere
        // that is not already existing...
        file.flush();
        file.setPointer(ptr);
        instantWriteInt(file, MDDReader.NODE, numberOfNodes);
        file.setPointer(current);
    }

    /**
     * Write the header of the .mdd file.<br>
     * Automatically compute the best space to allocate to each component.<br>
     * Initialise the array of elements to fit the best space.
     * @param dd The DecisionDiagram
     * @param file The file in which we write
     * @throws IOException
     */
    private void writeHeader(DecisionDiagram dd, MDDFileWriter file,
                               byte nodes, byte value_number,
                               byte max_degree, byte value) throws IOException {

        if (elements == null || elements.length < 7) elements = new byte[7][];
        elements[MDDReader.NODE] =           new byte[nodes];
        elements[MDDReader.VALUE] =          new byte[value];
        elements[MDDReader.PARENT_NUMBER] =  new byte[SmallMath.nBytes(0)];
        elements[MDDReader.VALUE_NUMBER] =   new byte[value_number];
        elements[MDDReader.SIZE] =           new byte[SmallMath.nBytes(dd.size())];
        elements[MDDReader.MAX_OUT_DEGREE] = new byte[max_degree];
        elements[MDDReader.COST] =           new byte[SmallMath.nBytes(0)];

        // Write the type of DD saved
        if (dd instanceof CostMDD) file.write(MDDReader.COST_MDD);
        else if (dd instanceof MDD) file.write(MDDReader.MDD);
        else if (dd instanceof BDD) file.write(MDDReader.BDD);

        file.write(MDDReader.TOP_DOWN);
        for (int i = 0; i < elements.length; i++) file.write(elements[i].length);
    }

    public void init(DecisionDiagram dd, MDDFileWriter file) throws IOException {
        // Write the header nad initialise the elements array
        writeHeader(dd, file, (byte) 5, (byte) 4, (byte) 4, (byte) 4);
        // Write the size of the DD
        int size = dd.size();
        writeInt(file, MDDReader.SIZE, size);

        // Initialise the maps AbstractNode -> int
        initMaps();
        // Reset the ID counter to 0
        nID = 0;

        // Bind the tt to the current map
        current.put(dd.getRoot(), 0);
        currentR.put(0, dd.getRoot());
    }

}
