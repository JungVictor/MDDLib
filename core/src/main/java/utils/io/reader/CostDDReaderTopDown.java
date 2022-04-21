package utils.io.reader;

import dd.DecisionDiagram;
import dd.interfaces.CostNodeInterface;
import dd.interfaces.NodeInterface;
import dd.mdd.components.Node;
import dd.mdd.costmdd.CostMDD;
import utils.io.MDDReader;

import java.io.IOException;

public class CostDDReaderTopDown extends DDReaderTopDown {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveNode(NodeInterface node, int nodeID, MDDFileWriter file) throws IOException {
        int numberOfValues = node.numberOfChildren();
        writeInt(file, MDDReader.MAX_OUT_DEGREE, numberOfValues);
        CostNodeInterface costNode = (CostNodeInterface) node;
        for(int value : node.iterateOnChildLabels()){
            // Label
            writeInt(file, MDDReader.VALUE, value);
            // Cost
            writeInt(file, MDDReader.COST, costNode.getArcCost(value));
            // Child
            NodeInterface child = node.getChild(value);
            int childID = bindNextWrite(child);
            writeInt(file, MDDReader.NODE, childID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadNode(DecisionDiagram dd, NodeInterface node, int layer, MDDFileReader file) throws IOException {
        // Number of values
        int numberOfValues = readInt(file, MDDReader.MAX_OUT_DEGREE);

        for(int i = 0; i < numberOfValues; i++){
            // value
            int value = readInt(file, MDDReader.VALUE);
            // value
            int cost = readInt(file, MDDReader.COST);
            // child ID
            int childID = readInt(file, MDDReader.NODE);

            Node child = (Node) addNodeToDD(dd, layer+1, childID);
            if(dd instanceof CostMDD) ((CostMDD) dd).addArc((Node) node, value, child, cost, layer);
            else dd.addArc(node, value, child, layer);
        }
    }

}
