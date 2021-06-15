package costmdd;

import costmdd.components.CostNode;
import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import memory.MemoryPool;

import java.util.InputMismatchException;

public class CostMDD extends MDD {

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public CostMDD(MemoryPool<MDD> pool) {
        super(pool);
    }

    @Override
    public void setRoot(Node node){
        if(node instanceof CostNode) super.setRoot(node);
        else throw new InputMismatchException("Expected the root to be at least a CostNode !");
    }

    @Override
    public Node Node(){
        if(getRoot() != null) return getRoot().Node();
        return Memory.CostNode();
    }

    @Override
    public MDD MDD(){
        return MDD(Node());
    }

    @Override
    public MDD MDD(Node root){
        return Memory.CostMDD(root);
    }


    //**************************************//
    //             MANAGE MDD               //
    //**************************************//

    /**
     * Add an arc between the source node and the destination node with the given value as label.
     * Ensures the connection between the two nodes
     * @param source The source node (parent)
     * @param value The value of the arc's label
     * @param destination The destination node (child)
     * @param cost Cost of the arc
     */
    public void addArc(Node source, int value, Node destination, int cost){
        ((CostNode) source).addChild(value, destination, cost);
        ((CostNode) destination).addParent(value, source, cost);
        addValue(value);
    }

    /**
     * Add the node destination to the given layer, and add an arc between the source node
     * and the destination node with the given value as label.
     * @param source The source node
     * @param value The value of the arc's label
     * @param destination The destination node - node to add in the MDD
     * @param cost Cost of the arc
     * @param layer The layer of the MDD where the node destination will be added
     */
    public void addArcAndNode(Node source, int value, Node destination, int cost, int layer){
        addArc(source, value, destination, cost);
        addNode(destination, layer);
    }

}
