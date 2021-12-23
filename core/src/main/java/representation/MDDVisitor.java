package representation;


import dd.mdd.MDD;
import dd.mdd.components.Layer;
import dd.mdd.components.Node;
import dd.mdd.components.OutArcs;

/**
 * <b>MDDVisitor</b><br>
 * The Visitor pattern for MDD
 */
public interface MDDVisitor {

    /**
     * Visit an MDD
     * @param mdd MDD to visit
     */
    void visit(MDD mdd);

    /**
     * Visit a layer
     * @param layer Layer to visit
     */
    void visit(Layer layer);

    /**
     * Visit a node
     * @param node Node to visit
     */
    void visit(Node node);

    /**
     * Visit all children
     * @param children Children to visit
     */
    void visit(OutArcs children);

}

