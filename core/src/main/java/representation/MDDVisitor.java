package representation;


import mdd.MDD;
import mdd.components.Layer;
import mdd.components.Node;
import mdd.components.OutArcs;

public interface MDDVisitor {

    void visit(Node node);
    void visit(Layer layer);
    void visit(MDD mdd);
    void visit(OutArcs children);

}

