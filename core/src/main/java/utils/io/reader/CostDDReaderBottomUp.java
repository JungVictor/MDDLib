package utils.io.reader;

import dd.DecisionDiagram;
import dd.interfaces.ICostNode;
import dd.interfaces.INode;
import dd.mdd.components.Node;
import dd.mdd.costmdd.CostMDD;
import utils.io.MDDReader;

import java.io.IOException;

public class CostDDReaderBottomUp extends DDReaderBottomUp {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveNode(INode node, int nodeID, MDDFileWriter file) throws IOException {
        int numberOfValues = node.numberOfParentsLabel();
        writeInt(file, MDDReader.VALUE_NUMBER, numberOfValues);
        ICostNode costNode = (ICostNode) node;
        for(int value : node.iterateOnParentLabels()){
            int numberOfParents = node.numberOfParents(value);
            writeInt(file, MDDReader.VALUE, value);
            writeInt(file, MDDReader.PARENT_NUMBER, numberOfParents);
            for(INode parent : node.iterateOnParents(value)) {
                writeInt(file, MDDReader.COST, costNode.getArcCost(parent, value));
                writeInt(file, MDDReader.NODE, bindNextWrite(parent));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadNode(DecisionDiagram dd, INode node, int layer, MDDFileReader file) throws IOException {
        // Number of values
        int numberOfValues = readInt(file, MDDReader.VALUE_NUMBER);

        for(int i = 0; i < numberOfValues; i++){
            // value
            int value = readInt(file, MDDReader.VALUE);

            // number of parents
            int numberOfParents = readInt(file, MDDReader.PARENT_NUMBER);

            for(int n = 0; n < numberOfParents; n++){
                int cost = readInt(file, MDDReader.COST);
                int parentID = readInt(file, MDDReader.NODE);
                Node parent = (Node) addNodeToDD(dd, layer-1, parentID);
                if(dd instanceof CostMDD) ((CostMDD) dd).addArc(parent, value, (Node) node, cost,layer-1);
                else dd.addArc(parent, value, node, layer-1);
            }
        }
    }

}
