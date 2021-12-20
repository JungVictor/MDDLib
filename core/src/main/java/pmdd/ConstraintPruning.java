package pmdd;

import pmdd.components.properties.PropertySumDouble;
import mdd.MDD;
import mdd.components.Node;
import pmdd.components.PNode;
import structures.generics.MapOf;

public strictfp class ConstraintPruning {

    /**
     * Take a confidence constraint's MDD and extract all arcs and nodes leading to a
     * false solution.
     * Build and return the MDD containing said arcs and nodes.
     * This function is iterative, and check a node at most once. Useful when you know
     * that the structure of the MDD does not allow purely bad arcs.
     * @param mdd Confidence Constraint's MDD
     * @param propertyName The name of the property ("confidence" by default)
     * @param values The binding (int label → double value)
     * @param min The lower threshold of the constraint
     * @return The MDD containing arcs and nodes leading to a false solution
     */
    @SuppressWarnings("unchecked")
    public static MDD iterative_prune(PMDD mdd, String propertyName, MapOf<Integer, Double> values, double min) {

        PMDD marked = PMDD.create();
        marked.setSize(mdd.size());

        // Initialisation. SUM neutral = [0, 0]
        PNode start = (PNode) mdd.getRoot();
        start.value[0] = 0;
        start.value[1] = 0;

        start.associate(marked.getRoot(), 0);

        // While there are node left to check
        for(int layer = 0; layer < mdd.size(); layer++) {
            for (Node current : mdd.getLayer(layer)) {

                PNode node = (PNode) current;
                //if(!node.isMarked()) continue;

                // For all its in-going arcs
                for (int label : node.getChildren()) {
                    // Transform the label into its value (default : log)
                    double value = values.get(label);
                    // For every parent corresponding to an in-going arc of current label

                    // Get the property of the parent
                    PNode child = (PNode) node.getChild(label);
                    MapOf<Integer, Double> property = (MapOf<Integer, Double>) child.getProperty(propertyName).getData();

                    // Compute the sum stocked in the node
                    // It is the equivalent of virtually swapping node's layers...
                    // But only for suspects values !
                    double vInf = node.value[0] + value;
                    double vSup = node.value[1] + value;

                    // If the value obtained by the lower bound is below the threshold
                    if (property.get(0) + vInf < min) {
                        // We mark the arc as suspect, update its value if necessary and push it to the queue
                        Node associate = node.getX1();
                        Node associateChild = child.getX1();
                        if(associateChild == null) {
                            associateChild = marked.Node();
                            child.associate(associateChild, 0);
                            marked.addNode(associateChild, layer + 1);
                        }

                        marked.addArc(associate, label, associateChild, layer);

                        child.value[0] = Math.min(child.value[0], vInf);
                        child.value[1] = Math.max(child.value[1], vSup);
                    }
                }
            }
        }

        marked.reduce();
        return marked;
    }

    /**
     * Check the confidence bound of a Confidence Constraint's MDD.
     * @param confidence The MDD of the confidence constraint
     * @param mapLog The binding (int label -> double value)
     */
    @SuppressWarnings("unchecked")
    private static void confidence(PMDD confidence, MapOf<Integer, Double> mapLog){
        PropertySumDouble confidenceProperty = PropertySumDouble.create(0, 0, mapLog);
        confidence.addRootProperty("confidence", confidenceProperty);
        MapOf<Integer, Double> ranges = (MapOf<Integer, Double>) confidence.propagateProperties(false).get("confidence").getData();
        double borne_sup = Math.exp(ranges.get(1));
        double borne_inf = Math.exp(ranges.get(0));
        System.out.println("\rCONFIDENCE _= ["+borne_inf+", " + borne_sup + "]");
    }

}
