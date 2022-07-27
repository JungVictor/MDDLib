package utils.io.reader;

import dd.DecisionDiagram;
import dd.interfaces.INode;
import dd.mdd.MDD;
import dd.operations.Pack;
import memory.Memory;
import structures.generics.SetOf;
import utils.io.MDDReader;

import java.io.IOException;

public class DDReaderTopDown extends DDReaderAbstractClass {

    public DDReaderTopDown(){
        super();
        super.setMODE(MDDReader.TOP_DOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveNode(INode node, int nodeID, MDDFileWriter file) throws IOException {
        int numberOfValues = node.numberOfChildren();
        writeInt(file, MDDReader.MAX_OUT_DEGREE, numberOfValues);
        for(int value : node.iterateOnChildLabels()){
            writeInt(file, MDDReader.VALUE, value);
            INode child = node.getChild(value);
            int childID = bindNextWrite(child);
            writeInt(file, MDDReader.NODE, childID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(DecisionDiagram dd, MDDFileWriter file) throws IOException {
        // Write the header nad initialise the elements array
        writeHeader(dd, file);
        // Write the size of the DD
        int size = dd.size();
        writeInt(file, MDDReader.SIZE, size);

        // Initialise the maps AbstractNode -> int
        initMaps();
        // Reset the ID counter to 0
        resetIDCounter();

        // Bind the tt to the current map
        firstBind(dd.getRoot());

        // Save each layer
        for (int i = 0; i < size; i++) {
            saveLayer(dd, i, file);
            swapMaps();
            resetIDCounter();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAndFree(DecisionDiagram dd, MDDFileWriter file, int layer) throws IOException {
        if(layer == 0) {
            // Write the header nad initialise the elements array
            writeHeader(dd, file);
            // Write the size of the DD
            int size = dd.size();
            writeInt(file, MDDReader.SIZE, size);

            // Initialise the maps AbstractNode -> int
            initMaps();
            // Reset the ID counter to 0
            resetIDCounter();

            // Bind the tt to the current map
            firstBind(dd.getRoot());
        }

        saveLayer(dd, layer, file);
        swapMaps();
        resetIDCounter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadNode(DecisionDiagram dd, INode node, int layer, MDDFileReader file) throws IOException {
        // Number of values
        int numberOfValues = readInt(file, MDDReader.MAX_OUT_DEGREE);

        for(int i = 0; i < numberOfValues; i++){
            // value
            int value = readInt(file, MDDReader.VALUE);
            // child ID
            int childID = readInt(file, MDDReader.NODE);

            INode child = addNodeToDD(dd, layer+1, childID);
            dd.addArc(node, value, child, layer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(DecisionDiagram dd, MDDFileReader file) throws IOException {
        // Read the header and initialise the elements array
        readHeader(file);
        // Read and set the size of the DD
        int size = readInt(file, MDDReader.SIZE);
        dd.setSize(size);
        // Initialise the mapping ID -> AbstractNode
        initMaps();

        // Bind the root node
        bindCurrentRead(0, dd.getRoot());

        // Load each layer
        for (int i = 0; i < size; i++) {
            loadLayer(dd, i, file);
            swapMaps();
        }
        // Set the TT node of the DD
        dd.setTT();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void skipNode(MDDFileReader file) throws IOException {
        // Number of values
        int numberOfValues = readInt(file, MDDReader.MAX_OUT_DEGREE);

        for (int i = 0; i < numberOfValues; i++) {
            readInt(file, MDDReader.VALUE);
            readInt(file, MDDReader.NODE);
        }
    }

    @Override
    public void loadAndReduce(DecisionDiagram dd, MDDFileReader file) throws IOException {
        // Read the header and initialise the elements array
        readHeader(file);
        // Read and set the size of the DD
        int size = readInt(file, MDDReader.SIZE);
        dd.setSize(size);
        dd.addNode(dd.Node(), size-1);

        // Set the TT node of the DD
        dd.setTT();

        // Initialise the mapping ID -> AbstractNode
        initMaps();

        long[] layerPos = new long[size-1];

        // Skip layer and remember the position of each in the file
        for (int i = 0; i < size-1; i++) {
            layerPos[i] = file.getPointer();
            skipLayer(file);
        }


        // Bind the tt node
        bindCurrentRead(0, dd.getTt());

        SetOf<Integer> V = Memory.SetOfInteger();

        // Load each layer
        for (int i = size-2; i >= 0; i--) {
            file.setPointer(layerPos[i]);
            loadAndReduceLayer(dd, i, file);
            swapMaps();

            // Reduction
            V.clear();
            for(int v : dd.iterateOnDomain(i)) V.add(v);
            Pack.pReduceI(dd.getLayers(), i, V);
        }
    }

    @Override
    protected void loadAndReduceNode(DecisionDiagram dd, int nodeID, int layer, MDDFileReader file) throws IOException {
        // Number of values
        int numberOfValues = readInt(file, MDDReader.MAX_OUT_DEGREE);

        for(int i = 0; i < numberOfValues; i++){
            // value
            int value = readInt(file, MDDReader.VALUE);
            // child ID
            int childID = readInt(file, MDDReader.NODE);

            INode node;
            if(layer == 0) node = dd.getRoot();
            else node = nextR.get(nodeID);
            if(node == null) {
                node = dd.Node();
                nextR.put(nodeID, node);
                next.put(node, nodeID);
                dd.addNode(node, layer);
            }

            INode child = getNode(childID);
            dd.addArc(node, value, child, layer);
        }
    }
}
