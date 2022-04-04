package utils.io.reader;

import dd.AbstractNode;
import dd.DecisionDiagram;
import utils.io.MDDReader;

import java.io.FileOutputStream;
import java.io.IOException;

public class DDReaderBottomUp extends DDReaderAbstractClass{

    public DDReaderBottomUp(){
        super();
        super.setMODE(MDDReader.BOTTOM_UP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveNode(AbstractNode node, int nodeID, MDDFileWriter file) throws IOException {
        int numberOfValues = node.numberOfParentsLabel();
        writeInt(file, MDDReader.NODE, nodeID);
        writeInt(file, MDDReader.VALUE_NUMBER, numberOfValues);
        for(int value : node.iterateOnParentLabel()){
            int numberOfParents = node.numberOfParents(value);
            writeInt(file, MDDReader.VALUE, value);
            writeInt(file, MDDReader.PARENT_NUMBER, numberOfParents);
            for(AbstractNode parent : node.iterateOnParents(value)) {
                writeInt(file, MDDReader.NODE, bindNextWrite(parent));
            }
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
        bindCurrentWrite(dd.getTt());

        // Save each layer
        for (int i = size - 1; i > 0; i--) {
            saveLayer(dd, i, file);
            swapWriteBinding();
            resetIDCounter();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractNode addNodeToDD(DecisionDiagram dd, int layer, int ID){
        if(layer == 0) return dd.getRoot(); // Add root case for bottom-up
        return super.addNodeToDD(dd, layer, ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadNode(DecisionDiagram dd, AbstractNode node, int layer, MDDFileReader file) throws IOException {
        // Number of values
        int numberOfValues = readInt(file, MDDReader.VALUE_NUMBER);

        for(int i = 0; i < numberOfValues; i++){
            // value
            int value = readInt(file, MDDReader.VALUE);

            // number of parents
            int numberOfParents = readInt(file, MDDReader.PARENT_NUMBER);

            for(int n = 0; n < numberOfParents; n++){
                int parentID = readInt(file, MDDReader.NODE);
                AbstractNode parent = addNodeToDD(dd, layer-1, parentID);
                dd.addArc(parent, value, node, layer-1);
            }
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

        // Create a tt node, add it to the DD and bind it
        AbstractNode tt = dd.Node();
        dd.addNode(tt, size - 1);
        bindCurrentRead(0, tt);

        // Load each layer
        for (int i = size - 1; i > 0; i--) {
            loadLayer(dd, i, file);
            swapReadBinding();
        }
        // Set the TT node of the DD
        dd.setTT();
    }

}
