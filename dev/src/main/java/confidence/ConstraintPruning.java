package confidence;

import confidence.properties.PropertySumDouble;
import mdd.MDD;
import mdd.components.InArcs;
import mdd.components.Node;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import structures.generics.MapOf;
import structures.generics.SetOf;
import utils.Logger;

import java.util.LinkedList;

public strictfp class ConstraintPruning {

    private static final LinkedList<PNode> toUpdate = new LinkedList<>();
    private static final LinkedList<PNode> Q = new LinkedList<>();

    /**
     * Take a confidence constraint's MDD and extract all arcs and nodes leading to a
     * false solution.
     * Build and return the MDD containing said arcs and nodes.
     * This function is iterative, and check a node at most once. Useful when you know
     * that the structure of the MDD does not allow purely bad arcs.
     * @param mdd Confidence Constraint's MDD
     * @param propertyName The name of the property ("confidence" by default)
     * @param values The binding (int label -> double value)
     * @param min The lower threshold of the constraint
     * @return The MDD containing arcs and nodes leading to a false solution
     */
    @SuppressWarnings("unchecked")
    public static MDD iterative_prune(PMDD mdd, String propertyName, MapOf<Integer, Double> values, double min) {

        // Initialisation. SUM neutral = [0, 0]
        PNode tt = (PNode) mdd.getTt();
        tt.value[0] = 0;
        tt.value[1] = 0;

        // While there are node left to check
        for(int layer = mdd.size() - 1; layer >= 0; layer--) {
            for (Node current : mdd.getLayer(layer)) {

                PNode node = (PNode) current;
                //if(!node.isMarked()) continue;

                // For all its in-going arcs
                for (int label : node.getParents().values()) {
                    // Transform the label into its value (default : log)
                    double value = values.get(label);
                    // For every parent corresponding to an in-going arc of current label
                    for (Node parent : node.getParents().get(label)) {

                        // Get the property of the parent
                        PNode source = (PNode) parent;
                        MapOf<Integer, Double> property = (MapOf<Integer, Double>) source.getProperty(propertyName).getData();

                        // Compute the sum stocked in the node
                        // It is the equivalent of virtually swapping node's layers...
                        // But only for suspects values !
                        double vInf = node.value[0] + value;
                        double vSup = node.value[1] + value;

                        // If the value obtained by the lower bound is below the threshold
                        if (property.get(0) + vInf < min) {
                            // We mark the arc as suspect, update its value if necessary and push it to the queue
                            markArc(source, label, node, 1);
                            source.value[0] = Math.min(source.value[0], vInf);
                            source.value[1] = Math.max(source.value[1], vSup);

                            // If the value obtained by the lower bound is ABOVE the threshold, then everything is fine
                        } else markArc(source, label, node, 0);
                    }
                }
            }
        }

        // Create the MDD corresponding to all marked arcs of the main MDD
        Logger.out.information("\rCreating Marked MDD");
        PMDD marked = PMDD.create();
        createMDD(mdd, marked);
        marked.reduce();
        return marked;
    }

    /**
     * Take a confidence constraint's MDD and extract all arcs and nodes leading to a
     * false solution.
     * Build and return the MDD containing said arcs and nodes.
     * @param mdd Confidence Constraint's MDD
     * @param propertyName The name of the property ("confidence" by default)
     * @param values The binding (int label -> double value)
     * @param min The lower threshold of the constraint
     * @return The MDD containing arcs and nodes leading to a false solution
     */
    @SuppressWarnings("unchecked")
    public static MDD prune(PMDD mdd, String propertyName, MapOf<Integer, Double> values, double min) {

        // Initialisation. SUM neutral = [0, 0]
        PNode tt = (PNode) mdd.getTt();
        tt.value[0] = 0;
        tt.value[1] = 0;

        // Push the terminal node to the queue
        Q.push(tt);

        // While there are node left to check
        while (!Q.isEmpty()) {

            // Take the most priority node
            PNode node = Q.pop();

            // For all its in-going arcs
            for (int label : node.getParents().values()) {
                // Transform the label into its value (default : log)
                double value = values.get(label);
                // For every parent corresponding to an in-going arc of current label
                for(Node parent : node.getParents().get(label)) {

                    // If the arc is marked highly suspect, skip it
                    // Not sure of it, never actually happened...
                    // Maybe the structure of the MDD is already detecting these "obvious" cases...
                    if(parent.getChildren().getMark(label) >= 2) continue;

                    // Get the property of the parent
                    PNode source = (PNode) parent;
                    MapOf<Integer, Double> property = (MapOf<Integer, Double>) source.getProperty(propertyName).getData();

                    // Compute the sum stocked in the node
                    // It is the equivalent of virtually swapping node's layers...
                    // But only for suspects values !
                    double vInf = node.value[0] + value;
                    double vSup = node.value[1] + value;

                    // If the value obtained by the lower bound is below the threshold
                    if (property.get(0) + vInf < min) {
                        // If the the value obtained by the UPPER bound is BELOW the threshold,
                        // Then it means that taking this arc is highly dangerous
                        // -> taking this arc is a necessary condition for some bad solutions to happen...
                        // /!\ TAKING THIS ARC DOESN'T HOWEVER NECESSARILY LEAD TO A BAD SOLUTION
                        if (property.get(1) + vSup < min) {
                            markArc(source, label, node, 2);
                            propagateBack(propertyName, node, Q);
                        } else {
                            // We mark the arc as suspect, update its value if necessary and push it to the queue
                            markArc(source, label, node, 1);
                            source.value[0] = Math.min(source.value[0], vInf);
                            source.value[1] = Math.max(source.value[1], vSup);
                            Q.push(source);
                        }
                    // If the value obtained by the lower bound is ABOVE the threshold, then everything is fine
                    } else markArc(source, label, node, 0);
                }
            }
        }

        // Create the MDD corresponding to all marked arcs of the main MDD
        PMDD marked = PMDD.create();
        createMDD(mdd, marked);
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
        PropertySumDouble confidenceProperty = MyMemory.PropertySumDouble(0, 0, mapLog);
        confidence.addRootProperty("confidence", confidenceProperty);
        MapOf<Integer, Double> ranges = (MapOf<Integer, Double>) confidence.propagateProperties(false).get("confidence").getData();
        double borne_sup = Math.exp(ranges.get(1));
        double borne_inf = Math.exp(ranges.get(0));
        System.out.println("\rCONFIDENCE _= ["+borne_inf+", " + borne_sup + "]");
    }

    /**
     * Build the marked MDD corresponding to all marked nodes and arcs in mdd.
     * @param mdd The full MDD containing marked arcs and nodes
     * @param marked The MDD that will contains only marked arcs and nodes
     */
    private static void createMDD(MDD mdd, MDD marked) {
        marked.setSize(mdd.size());
        marked.getRoot().associate(mdd.getRoot(), null);
        mdd.getRoot().associate(marked.getRoot(), null);

        SetOf<Integer> labels = Memory.SetOfInteger();

        for(int i = 0; i < mdd.size(); i++) {
            for(Node node : mdd.getLayer(i)) {
                labels.add(node.getChildren().getValues());
                for(int label : labels) {
                    if(node.getX1() == null) {
                        if (node.getChildren().getMark(label) == 2) mdd.removeArc(node, label);
                    }
                    else if (node.getChildren().getMark(label) >= 1) {
                        Node child = node.getChild(label);
                        Node nodeChild = child.getX1();
                        if(nodeChild == null) {
                            nodeChild = marked.Node();
                            nodeChild.associate(child, null);
                            child.associate(nodeChild, null);
                            marked.addNode(nodeChild, i+1);
                        }
                        marked.addArc(node.getX1(), label, nodeChild, i);
                    }
                }
                labels.clear();
            }
        }
    }

    /**
     * Mark the arc corresponding to : source -> (label) -> destination
     * @param source The source node (parent)
     * @param label The label of the arc
     * @param destination The destination node (child)
     * @param value Value of the mark (default : 0 clean, 1 suspect, 2 highly suspect
     */
    private static void markArc(Node source, int label, Node destination, int value){
        source.getChildren().mark(label, value);
    }

    /**
     * When marking a highly suspect arc, virtually delete him and repropagate all properties from the
     * deleted arc to all children.
     * The propagation stop when there is no node left to update
     * @param propertyName The name of the property ("confidence" by default)
     * @param destination The destination node of the arc
     * @param Q The queue used in the main algorithm to push in priority the updated nodes
     */
    private static void propagateBack(String propertyName, PNode destination, LinkedList<PNode> Q){
        // We push the initial node to update
        toUpdate.push(destination);
        while (!toUpdate.isEmpty()) {
            // Take a node out of the queue and take its current property
            PNode node = toUpdate.pop();
            NodeProperty current = node.getProperty(propertyName);

            // Compute a new property by asking the parents to do the propagation step
            NodeProperty new_property = updateProperty(propertyName, node.getParents());

            // If the current property is not the same as the new one : THERE IS AN UPDATE !
            if(!current.equals(new_property)) {
                // Update the property
                node.addProperty(propertyName, new_property);

                // Add all children to the queue : because at least ONE parent is updated, all children need
                // to be checked
                for(int value : node.getChildren()) {
                    PNode child = (PNode) node.getChild(value);
                    if(!toUpdate.contains(child)) toUpdate.push(child);
                }

                // Reinitialise the value of the node and add it in the queue IN HIGH PRIORITY !
                node.value[0] = 1;
                node.value[1] = 0;
                Q.addFirst(node);

                // Free the unused property
                Memory.free(current);
            } else Memory.free(new_property);
        }
    }

    /**
     * Update the property of a node by asking all parents (in-going arcs) to update its value.
     * @param propertyName The name of the property ("confidence" by default)
     * @param arcs All the in-going arcs (=> parents)
     * @return The property obtained after the propagation process
     */
    private static NodeProperty updateProperty(String propertyName, InArcs arcs) {
        NodeProperty property = null;
        // For every value
        for(int value : arcs) {
            // For every parent corresponding to this value
            for(Node parent : arcs.get(value)) {
                // If the arc is highly suspect, ignore it
                if(parent.getChildren().getMark(value) == 2) continue;

                PNode source = (PNode) parent;
                // If the property is not yet set, create one by propagation
                if (property == null) property = source.getProperty(propertyName).createProperty(value);
                // Otherwise, merge it by propagation
                else source.getProperty(propertyName).mergeWithProperty(value, property);
            }
        }
        return property;
    }

}
