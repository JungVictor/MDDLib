package confidence;

import confidence.properties.PropertySumDouble;
import mdd.MDD;
import mdd.components.InArcs;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import representation.MDDPrinter;
import structures.generics.MapOf;
import structures.generics.SetOf;

import java.util.LinkedList;

public class ConstraintPruning {

    private static LinkedList<PNode> toUpdate = new LinkedList<>(),
                                     Q = new LinkedList<>();

    @SuppressWarnings("unchecked")
    public static MDD prune(PMDD mdd, String propertyName, MapOf<Integer, Double> values, double min) {
        PNode tt = (PNode) mdd.getTt();
        tt.value[0] = 0;
        tt.value[1] = 0;
        tt.marked = false;
        tt.updated = false;

        Q.push(tt);

        SetOf<Integer> labels = Memory.SetOfInteger();
        SetOf<Node> parents = Memory.SetOfNode();

        while (!Q.isEmpty()) {
            PNode node = Q.pop();

            //if(node.marked && !node.updated) continue;

            node.updated = false;

            labels.add(node.getParents().values());

            for (int label : labels) {
                double value = values.get(label);
                parents.add(node.getParents().get(label));
                for(Node parent : parents) {
                    if(parent.getChildren().getMark(label) >= 2) continue;
                    PNode source = (PNode) parent;
                    MapOf<Integer, Double> property = (MapOf<Integer, Double>) source.getProperty(propertyName).getData();

                    double vInf = node.value[0] + value;
                    double vSup = node.value[1] + value;

                    if (property.get(0) + vInf < min) {
                        if (property.get(1) + vSup < min) {
                            markArc(source, label, node, 2);
                            repropagate(propertyName, node, Q);
                        } else {
                            markArc(source, label, node, 1);
                            node.marked = true;
                            source.value[0] = Math.min(source.value[0], vInf);
                            source.value[1] = Math.max(source.value[1], vSup);
                            Q.push(source);
                        }
                    } else markArc(source, label, node, 0);
                }
                parents.clear();
            }
            labels.clear();
        }

        PMDD marked = PMDD.create();
        createMDD(mdd, marked);
        marked.reduce();
        return marked;
    }

    private static void confidence(PMDD confidence, MapOf<Integer, Double> mapLog){
        PropertySumDouble confidenceProperty = MyMemory.PropertySumDouble(0, 0, mapLog);
        confidence.addRootProperty("confidence", confidenceProperty);
        MapOf<Integer, Double> ranges = (MapOf<Integer, Double>) confidence.propagateProperties(false).get("confidence").getData();
        double borne_sup = Math.exp(ranges.get(1));
        double borne_inf = Math.exp(ranges.get(0));
        System.out.println("\rCONFIDENCE _= ["+borne_inf+", " + borne_sup + "]");
    }

    private static MDD createMDD(MDD mdd, MDD marked) {
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
        return marked;
    }

    private static void markArc(Node source, int label, Node destination, int value){
        source.getChildren().mark(label, value);
    }

    private static void repropagate(String propertyName, PNode destination, LinkedList<PNode> Q){
        toUpdate.push(destination);
        while (!toUpdate.isEmpty()) {
            PNode node = toUpdate.pop();
            NodeProperty current = node.getProperty(propertyName);
            NodeProperty new_property = updateProperty(propertyName, node.getParents());
            if(!current.equals(new_property)) {
                node.addProperty(propertyName, new_property);
                for(int value : node.getChildren()) {
                    PNode child = (PNode) node.getChild(value);
                    if(!toUpdate.contains(child)) toUpdate.push(child);
                }

                node.updated = true;
                node.value[0] = 1;
                node.value[1] = 0;
                Q.addFirst(node);

                //Memory.free(current);
            } //else Memory.free(new_property);
        }
    }

    private static NodeProperty updateProperty(String propertyName, InArcs arcs) {
        NodeProperty property = null;
        for(int value : arcs) {
            for(Node parent : arcs.get(value)) {
                if(parent.getChildren().getMark(value) == 2) continue;
                PNode source = (PNode) parent;
                if (property == null) property = source.getProperty(propertyName).createProperty(value);
                else source.getProperty(propertyName).mergeWithProperty(value, property);
            }
        }
        return property;
    }

}
