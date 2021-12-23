package representation;

import dd.mdd.MDD;
import dd.mdd.components.Layer;
import dd.mdd.components.Node;
import dd.mdd.components.OutArcs;

import java.util.HashMap;

/**
 * <b>MDDPrinter</b><br>
 * This class is used to print the MDD in the console.
 */
public class MDDPrinter implements MDDVisitor {

    private int node_number, layer_number;
    private final HashMap<Node, String> names = new HashMap<>();
    private boolean intAsChar = false;

    /**
     * Create a new MDDPrinter
     */
    public MDDPrinter(){}

    /**
     * Create a new MDDPrinter that will show the integer (labels) as characters
     * @param intAsChar True to show int as char, false otherwise (default = false)
     */
    public MDDPrinter(boolean intAsChar){
        this.intAsChar = intAsChar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Node node) {
        System.out.print(names.get(node));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Layer layer) {
        System.out.println();
        System.out.println("Layer " + (layer_number++));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MDD mdd) {
        names.clear();
        node_number = 0;
        layer_number = 0;

        names.put(mdd.getRoot(), "root");
        names.put(mdd.getTt(), "tt");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OutArcs children) {
        if(children.size() > 0) System.out.print(" : ");
        for(int value : children) {
            Node node = children.get(value);
            if(!names.containsKey(node)) names.put(node, "node"+(node_number++));
            if(intAsChar) {
                if (value >= 0) System.out.print("(" + ((char) value) + ", " + names.get(node) + ") ");
                else System.out.print("( , " + names.get(node) + ") ");
            }
            else System.out.print("("+value+", " + names.get(node)+") ");

        }
        System.out.println();
    }
}

