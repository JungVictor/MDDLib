package dd.mdd.nondeterministic;

import builder.rules.SuccessionRule;
import dd.DecisionDiagram;
import dd.interfaces.INode;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import dd.mdd.nondeterministic.components.NDNode;
import memory.Memory;
import structures.Binder;
import structures.arrays.ArrayOfInt;
import structures.arrays.ArrayOfNodeInterface;
import structures.generics.CollectionOf;
import structures.successions.SuccessionOfNodeInterface;
import utils.Logger;

public class NDOperation {

    public static NDMDD partiallyOrderedIntersection(NDMDD result, DecisionDiagram dd1, DecisionDiagram dd2, ArrayOfNodeInterface roots){
        result.setSize(dd1.size());

        // dd1 is the partially ordered MDD
        // dd2 is the fully ordered MDD

        boolean instanceOfND = dd1 instanceof NDMDD;

        ArrayOfNodeInterface tts = ArrayOfNodeInterface.create(roots.length);
        for(int i = 0; i < roots.length - 1; i++) tts.set(i, roots.get(i+1));
        tts.set(tts.length - 1, dd1.getTt());

        // First association is always the node from dd2
        result.getRoot().setX1(dd2.getRoot());

        // Associate all roots from dd1 to the result root
        for(int i = 0; i < roots.length; i++) result.getRoot().setX(roots.get(i), i+1);

        Binder binder = Binder.create();

        // SuccessionRule of intersection iterate over the child of x1
        // That's why we need to set x1 to the node of the fully ordered MDD
        SuccessionRule rule = SuccessionRule.INTERSECTION;
        CollectionOf<Integer> successors = rule.getCollection();

        for(int i = 1; i < result.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(INode x : result.iterateOnLayer(i-1)){
                SuccessionOfNodeInterface xs = x.getAssociations();
                INode y1, y2;
                INode x1 = x.getX1(), x2;
                for(int v : rule.successors(successors, i-1, x)) {
                    for (int a = 1; a < xs.length; a++) {
                        x2 = x.getX(a);
                        // If we reached the terminal node for this branch, skip
                        if (x2 == tts.get(a-1)) continue;
                        // Else continue
                        if (x2 != null && x2.containsLabel(v)) {
                            y2 = x2.getChild(v);
                            // If first MDD is non-deterministic
                            if(x1 instanceof NDNode) {
                                for(Object nd_y1 : ((NDNode) x1).iterateOnChildren(v)) {
                                    addArcAndNode(result, x, (INode) nd_y1, y2, v, i, a, xs, binder);
                                }
                                // If first MDD is deterministic
                            } else {
                                y1 = x1.getChild(v);
                                addArcAndNode(result, x, y1, y2, v, i, a, xs, binder);
                            }
                        }
                    }
                }
            }
            if(result.getLayerSize(i) == 0) {
                binder.clear();
                Memory.free(binder);
                result.setSize(i);
                result.reduce();
                return result;
            }
            binder.clear();
        }
        Memory.free(binder);
        Memory.free(successors);

        //result.reduce();
        return result;
    }

    public static MDD partiallyOrderedIntersection(MDD result, DecisionDiagram dd1, DecisionDiagram dd2, ArrayOfNodeInterface roots){
        NDMDD tmp_res = partiallyOrderedIntersection(NDMDD.create(), dd1, dd2, roots);
        NDOperation.determinise(result, tmp_res);
        Memory.free(tmp_res);
        return result;
    }

    private static void addArcAndNode(NDMDD result, INode x, INode y1, INode y2, int v, int i, int a, SuccessionOfNodeInterface xs, Binder binder) {
        // Updating associations
        ArrayOfNodeInterface ys = ArrayOfNodeInterface.create(xs.length);
        for(int association = 0; association < xs.length; association++) ys.set(association, xs.get(association));
        ys.set(0, y1);
        ys.set(a, y2);

        Binder path = binder.path(ys);
        INode y = path.getLeaf();
        // The node does not exist
        if(y == null){
            y = result.Node();
            y.associate(ys);
            result.addNode(y, i);
            path.setLeaf(y);
        }
        result.addArc(x, v, y, i-1);
    }

    public static MDD determinise(MDD result, NDMDD mdd){
        Binder binder = Binder.create();
        ArrayOfNodeInterface array = ArrayOfNodeInterface.create(10);

        for(int i = 0; i < mdd.size() - 1; i++){
            for(Object inode : mdd.iterateOnLayer(i)){
                NDNode node = (NDNode) inode;
                for(int label : node.iterateOnChildLabels()){

                    // Non deterministic for this label
                    if(node.numberOfChildren(label) > 1) {
                        array.setLength(node.numberOfChildren(label));
                        int c = 0;
                        for(INode child : node.getChildren().get(label)) array.set(c++, child);
                        Binder path = binder.path(array);
                        NDNode acc = (NDNode) path.getLeaf();
                        if(acc == null) {
                            acc = mdd.Node();
                            for(INode child : array) acc.copyChildrenReferencesFrom((NDNode) child);
                            mdd.removeArcs(node, i+1, label);
                            mdd.addNode(acc, i+1);
                            path.setLeaf(acc);
                        }
                        mdd.addArc(node, label, acc, i);
                    }
                }
            }
        }
        Memory.free(binder);
        Memory.free(array);

        mdd.clearAllAssociations();

        result.setSize(mdd.size());
        result.getRoot().setX1(mdd.getRoot());
        mdd.getRoot().setX1(result.getRoot());

        for(int i = 0; i < mdd.size() - 1; i++){
            for(Object inode : result.iterateOnLayer(i)){
                Node node = (Node) inode;
                INode x1 = node.getX1();
                for(int label : x1.iterateOnChildLabels()) {
                    INode y1 = x1.getChild(label);
                    if(y1 == null) continue;
                    INode child = y1.getX1();
                    if(child == null){
                        child = result.Node();
                        child.setX1(y1);
                        y1.setX1(child);
                        result.addNode(child, i+1);
                    }
                    result.addArc(node, label, child, i);
                }
            }
        }

        result.reduce();

        return result;
    }

}
