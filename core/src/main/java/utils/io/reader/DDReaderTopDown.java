package utils.io.reader;

import dd.AbstractNode;
import dd.DecisionDiagram;
import utils.io.MDDReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    protected void saveNode(AbstractNode node, int nodeID, FileOutputStream file) throws IOException {
        int numberOfValues = node.numberOfChildren();
        writeInt(file, MDDReader.NODE, nodeID);
        writeInt(file, MDDReader.VALUE_NUMBER, numberOfValues);
        for(int value : node.iterateOnChildLabel()){
            writeInt(file, MDDReader.VALUE, value);
            AbstractNode child = node.getChild(value);
            int childID = bindNextWrite(child);
            writeInt(file, MDDReader.NODE, childID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(DecisionDiagram dd, FileOutputStream file) throws IOException {
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
        bindCurrentWrite(dd.getRoot());

        // Save each layer
        for (int i = 0; i < size; i++) {
            saveLayer(dd, i, file);
            swapWriteBinding();
            resetIDCounter();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadNode(DecisionDiagram dd, AbstractNode node, int layer, FileInputStream file) throws IOException {
        // Number of values
        int numberOfValues = readInt(file, MDDReader.VALUE_NUMBER);

        for(int i = 0; i < numberOfValues; i++){
            // value
            int value = readInt(file, MDDReader.VALUE);
            // child ID
            int childID = readInt(file, MDDReader.NODE);

            AbstractNode child = addNodeToDD(dd, layer+1, childID);
            dd.addArc(node, value, child, layer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(DecisionDiagram dd, FileInputStream file) throws IOException {
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
            swapReadBinding();
        }
        // Set the TT node of the DD
        dd.setTT();
    }
}
