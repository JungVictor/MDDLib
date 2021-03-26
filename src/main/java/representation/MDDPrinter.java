package representation;

import mdd.MDD;
import mdd.components.Layer;
import mdd.components.Node;
import mdd.components.OutArcs;

import java.util.HashMap;

public class MDDPrinter implements MDDVisitor {

    private int node_number, layer_number;
    private final HashMap<Node, String> names = new HashMap<>();

    public MDDPrinter(){}

    @Override
    public void visit(Node node) {
        System.out.print(names.get(node));
    }

    @Override
    public void visit(Layer layer) {
        System.out.println();
        System.out.println("Layer " + (layer_number++));
    }

    @Override
    public void visit(MDD mdd) {
        names.clear();
        node_number = 0;
        layer_number = 0;

        names.put(mdd.getRoot(), "root");
        names.put(mdd.getTt(), "tt");
    }

    @Override
    public void visit(OutArcs children) {
        if(children.size() > 0) System.out.print(" : ");
        for(int value : children) {
            Node node = children.get(value);
            if(!names.containsKey(node)) names.put(node, "node"+(node_number++));
            System.out.print("("+value+", " + names.get(node)+") ");
        }
        System.out.println();
    }
}

