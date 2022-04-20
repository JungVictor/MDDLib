package dd.operations;

import builder.MDDBuilder;
import builder.rules.SuccessionRule;
import dd.DecisionDiagram;
import dd.interfaces.NodeInterface;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import memory.Memory;
import structures.Binder;
import structures.arrays.ArrayOfNodeInterface;
import structures.arrays.ArrayOfBoolean;
import structures.arrays.ArrayOfMDD;
import structures.generics.CollectionOf;
import structures.generics.SetOf;
import structures.successions.SuccessionOfNodeInterface;
import utils.Logger;

/**
 * <b>The class dedicated to perform classical operation on and between MDDs</b>
 */
public class Operation {

    private enum Operator {
        UNION, INTERSECTION, DIAMOND, MINUS, INCLUSION
    }

    //**************************************//
    //           UNARY OPERATIONS           //
    //**************************************//

    /**
     * Compute the negation of the given MDD
     * @param mdd The MDD
     * @return The negation of the given MDD
     */
    public static MDD negation(MDD mdd){
        MDD universal = MDDBuilder.universal(MDD.create(), mdd.getDomains());
        MDD negation = minus(universal, mdd);
        Memory.free(universal);
        return negation;
    }


    //**************************************//
    //          BINARY OPERATIONS           //
    //**************************************//

    // ------------ //
    // INTERSECTION //
    // ------------ //
    /**
     * Perform an intersection between mdd1 and mdd2.
     * Create a new MDD to stock the result.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @return The result of the intersection between mdd1 and mdd2
     */
    public static MDD intersection(MDD mdd1, DecisionDiagram mdd2){
        MDD result = mdd1.DD();
        perform(result, mdd1, mdd2, Operator.INTERSECTION);
        return result;
    }

    /**
     * Perform an intersection between two Decision Diagrams
     * Create a new DD to stock the result.
     * @param dd1 The first DD
     * @param dd2 The second DD
     * @return The result of the intersection between dd1 and dd2
     */
    public static DecisionDiagram intersection(DecisionDiagram dd1, DecisionDiagram dd2){
        return perform(dd1, dd2, Operator.INTERSECTION);
    }

    /**
     * Perform an intersection between mdd1 and mdd2.
     * Stock the result in the given result MDD.
     * @param result The MDD to stock the result
     * @param dd1 The first MDD
     * @param dd2 The second MDD
     * @return The result of the intersection between mdd1 and mdd2
     */
    public static MDD intersection(MDD result, DecisionDiagram dd1, DecisionDiagram dd2){
        perform(result, dd1, dd2, Operator.INTERSECTION);
        return result;
    }

    /**
     * Perform an intersection between dd1 and dd2.
     * Stock the result in the given result DD.
     * @param result The MDD to stock the result
     * @param dd1 The first MDD
     * @param dd2 The second MDD
     * @return The result of the intersection between mdd1 and mdd2
     */
    public static DecisionDiagram intersection(DecisionDiagram result, DecisionDiagram dd1, DecisionDiagram dd2){
        perform(result, dd1, dd2, Operator.INTERSECTION);
        return result;
    }

    /**
     * Perform an intersection between mdd1 and mdd2 from layer start to layer stop.
     * Create a new MDD to stock the result.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @param start The starting layer
     * @param stop The stopping layer
     * @param size The size of the result
     * @return The result of the intersection between mdd1 and mdd2 from layer start to layer stop.
     */
    public static MDD intersection(MDD mdd1, DecisionDiagram mdd2, int start, int stop, int size){
        MDD result = mdd1.DD();
        intersection(result, mdd1, mdd2, start, stop, size);
        return result;
    }

    /**
     * Perform an intersection between mdd1 and mdd2 from layer start to layer stop.
     * Stock the result in the given result MDD.
     * @param result The MDD that will stock the result of the operation
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @param start The starting layer
     * @param stop The stopping layer
     * @param size The size of the result
     * @return The result of the intersection between mdd1 and mdd2 from layer start to layer stop.
     */
    public static MDD intersection(MDD result, DecisionDiagram mdd1, DecisionDiagram mdd2, int start, int stop, int size){
        intersection((DecisionDiagram) result, mdd1, mdd2, start, stop, size);
        return result;
    }

    public static DecisionDiagram intersection(DecisionDiagram result, DecisionDiagram mdd1, DecisionDiagram mdd2, int start, int stop, int size){
        mdd1.clearAllAssociations();
        // Copy the first MDD until the first layer of intersection
        result.setSize(size);
        mdd1.getRoot().associate(result.getRoot(), null);
        mdd1.copy(result, 0, 0, start);

        // Binding all nodes in layer start to the root of mdd2
        if(start > 0)
            for(NodeInterface node : mdd1.iterateOnLayer(start))
                node.getX1().associate(node, mdd2.getRoot());
        else result.getRoot().associate(mdd1.getRoot(), mdd2.getRoot());

        // Normal intersection
        SetOf<Integer> V = Memory.SetOfInteger();
        Binder binder = Binder.create();

        // Construction of V
        // V.add(mdd1.getV());
        // V.intersect(mdd2.getV());

        for(int i = start+1; i < stop; i++){
            for(NodeInterface x : result.iterateOnLayer(i-1)){
                NodeInterface x1 = x.getX1(), x2 = x.getX2();
                for(int v : x1.iterateOnChildLabels()){
                    boolean a2 = x2.containsLabel(v);
                    if(apply(true, a2, Operator.INTERSECTION, i == stop - 1)) {
                        addArcAndNode(result, x, x1.getChild(v), x2.getChild(v), v, i, binder);
                    }
                }
            }
            if(result.getLayerSize(i) == 0) {
                result.reduce();
                Memory.free(V);
                Memory.free(binder);
                return result;
            }
            binder.clear();
        }

        Memory.free(V);
        Memory.free(binder);

        // Final step
        if(stop != size){
            if(stop < mdd1.size()){
                // mdd2 is contained in the intersection
                // Therefore, we need to construct the rest of the dd.mdd from mdd1
                for(NodeInterface node : result.iterateOnLayer(stop-1)) node.getX1().associate(node, null);
                mdd1.copy(result, 0, stop-1, mdd1.size());
            } else if (stop >= mdd1.size()){
                // We need to construct the rest of the dd.mdd from mdd2
                for(NodeInterface node : result.iterateOnLayer(stop-1)) node.getX2().associate(node, null);
                mdd2.copy(result, start, stop - start - 1, mdd2.size());
            }
        }

        result.reduce();
        return result;
    }


    // ------------- //
    // CONCATENATION //
    // ------------- //
    /**
     * Perform a concatenation of the two MDD. That is to say, the tt node of mdd1 will be replaced by
     * the root node from mdd2.
     * Create a new MDD to stock the result
     * @param mdd1 The first MDD (top)
     * @param mdd2 The second MDD (bottom)
     * @return The result of the concatenate operation.
     */
    public static MDD concatenate(MDD mdd1, DecisionDiagram mdd2){
        MDD result = mdd1.copy();
        result.setSize(mdd1.size() + mdd2.size() - 1);
        mdd2.copy(result, result.getTt(), mdd1.size() - 1);
        return result;
    }


    // ----- //
    // UNION //
    // ----- //
    /**
     * Perform the union of mdd1 and mdd2.
     * Create a new MDD to stock the result.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @return the union of mdd1 and mdd2.
     */
    public static MDD union(MDD mdd1, DecisionDiagram mdd2){
        MDD result = mdd1.DD();
        perform(result, mdd1, mdd2, Operator.UNION);
        return result;
    }

    /**
     * Perform the union of mdd1 and mdd2.
     * Create a new MDD to stock the result.
     * @param dd1 The first MDD
     * @param dd2 The second MDD
     * @return the union of mdd1 and mdd2.
     */
    public static DecisionDiagram union(DecisionDiagram dd1, DecisionDiagram dd2){
        return perform(dd1, dd2, Operator.UNION);
    }


    // ------- //
    // DIAMOND //
    // ------- //
    /**
     * Perform the diamond operation between mdd1 and mdd2.
     * Create a new MDD to stock the result.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @return the result of the diamond operation between mdd1 and mdd2
     */
    public static MDD diamond(MDD mdd1, DecisionDiagram mdd2){
        MDD result = mdd1.DD();
        perform(result, mdd1, mdd2, Operator.DIAMOND);
        return result;
    }

    /**
     * Perform the diamond operation between mdd1 and mdd2.
     * Create a new MDD to stock the result.
     * @param dd1 The first MDD
     * @param dd2 The second MDD
     * @return the result of the diamond operation between mdd1 and mdd2
     */
    public static DecisionDiagram diamond(DecisionDiagram dd1, DecisionDiagram dd2){
        return perform(dd1, dd2, Operator.DIAMOND);
    }


    // ----- //
    // MINUS //
    // ----- //
    /**
     * Perform the minus operation between mdd1 and mdd2.
     * Create a new MDD to stock the result.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @return the result of the minus operation between mdd1 and mdd2
     */
    public static MDD minus(MDD mdd1, DecisionDiagram mdd2){
        MDD result = mdd1.DD();
        perform(result, mdd1, mdd2, Operator.MINUS);
        return result;
    }

    /**
     * Perform the minus operation between mdd1 and mdd2.
     * Create a new MDD to stock the result.
     * @param dd1 The first MDD
     * @param dd2 The second MDD
     * @return the result of the minus operation between mdd1 and mdd2
     */
    public static DecisionDiagram minus(DecisionDiagram dd1, DecisionDiagram dd2){
        return perform(dd1, dd2, Operator.MINUS);
    }


    // --------- //
    // INCLUSION //
    // --------- //
    /**
     * Check if mdd2 is included in mdd1.
     * @param dd1 The first MDD
     * @param dd2 The second MDD
     * @return true if mdd2 is included in mdd1, false otherwise
     */
    public static boolean inclusion(DecisionDiagram dd1, DecisionDiagram dd2){
        if(dd2.size() > dd1.size()) return false;

        for (int i = 0; i < dd1.size(); i++) {
            if(dd1.getDomainSize(i) < dd2.getDomainSize(i)) return false;
            for(int value : dd1.iterateOnDomain(i)) if(!dd2.domainContains(i, value)) return false;
        }

        return inclusion(dd1.getRoot(), dd2.getRoot(), dd2.size());
    }

    /**
     * Check if the sub-MDD of the given size starting from node root2 is included in the sub-MDD starting from
     * node root1 of the given size.
     * @param root1 The first root
     * @param root2 The second root
     * @param size The size of both sub-MDDs
     * @return true if there is an inclusion, false otherwise
     */
    public static boolean inclusion(NodeInterface root1, NodeInterface root2, int size){
        MDD inclusion = MDD.create();
        boolean result = perform(inclusion, root1, root2, size, SuccessionRule.INTERSECTION, Operator.INCLUSION) != null;
        Memory.free(inclusion);
        return result;
    }


    // ---- //
    // CORE //
    // ---- //

    /**
     * Check if an arc must be created, given the different inputs.
     * @param a1 Existence of the arc in the first MDD
     * @param a2 Existence of the arc in the second MDD
     * @param OP The type of operation
     * @param isFinalLayer Whether the given layer is the final one
     * @return true if an arc must be created, false otherwise
     */
    private static boolean apply(boolean a1, boolean a2, Operator OP, boolean isFinalLayer){
        switch (OP){
            case INTERSECTION:
            case INCLUSION:
                return a1 && a2;
            case UNION: return a1 || a2;
            case MINUS: return (a1 && !a2) || (a1 && !isFinalLayer);
            case DIAMOND: return (a1 && a2 && !isFinalLayer) || (a1 ^ a2);
        }
        return false;
    }

    /**
     *
     * @param result The MDD that will contains the result of the operation
     * @param root1 The root node of the first MDD
     * @param root2 The root node of the second MDD
     * @param size The size of the MDD result
     * @param rule The succession rule
     * @param OP The type of operation
     * @return The MDD resulting from the operation
     */
    private static DecisionDiagram perform(DecisionDiagram result, NodeInterface root1, NodeInterface root2, int size, SuccessionRule rule, Operator OP){
        result.setSize(size);
        Binder binder = Binder.create();

        CollectionOf<Integer> successors = rule.getCollection();

        result.getRoot().associate(root1, root2);

        for(int i = 1; i < size; i++){
            Logger.out.information("\rLAYER " + i);
            for(NodeInterface x : result.iterateOnLayer(i-1)){
                NodeInterface x1 = x.getX1(), x2 = x.getX2();
                NodeInterface y1, y2;
                for(int v : rule.successors(successors, i-1, x)){
                    boolean a1, a2;
                    a1 = x1 != null && x1.containsLabel(v);
                    a2 = x2 != null && x2.containsLabel(v);
                    if(apply(a1, a2, OP, i == size - 1)) {
                        y1 = x1 == null ? null : x1.getChild(v);
                        y2 = x2 == null ? null : x2.getChild(v);
                        addArcAndNode(result, x, y1, y2, v, i, binder);
                    }
                    else if(OP == Operator.INCLUSION && a1) return null;
                }
            }
            if(result.getLayerSize(i) == 0) {
                binder.clear();
                Memory.free(binder);
                if (OP == Operator.INCLUSION) return null;
                if (OP != Operator.MINUS) result.setSize(i);
                result.reduce();
                return result;
            }
            binder.clear();
        }
        Memory.free(binder);
        Memory.free(successors);

        result.reduce();
        return result;
    }

    /**
     * @param result The MDD that will contains the result of the operation
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @param OP Type of operation (union, intersection, ...)
     * @return The MDD resulting from : mdd1 OP mdd2 stocked in result
     */
    private static DecisionDiagram perform(DecisionDiagram result, DecisionDiagram mdd1, DecisionDiagram mdd2, Operator OP){
        result.setSize(mdd1.size());
        SuccessionRule rule;
        if(OP == Operator.INTERSECTION) rule = SuccessionRule.INTERSECTION;
        else rule = SuccessionRule.UNION;

        perform(result, mdd1.getRoot(), mdd2.getRoot(), mdd1.size(), rule, OP);
        return result;
    }

    /**
     * Create a new MDD by applying the operator OP between mdd1 and mdd2.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @param OP Type of operation (union, intersection, ...)
     * @return The MDD resulting from : mdd1 OP mdd2.
     */
    private static DecisionDiagram perform(DecisionDiagram mdd1, DecisionDiagram mdd2, Operator OP){
        return perform(mdd1.DD(), mdd1, mdd2, OP);
    }

    /**
     * Add an arc and a node to the given MDD
     * @param mdd The MDD where the node will be added
     * @param x The source node
     * @param y1 The child of the first associated node to x (x1), corresponding to the given label
     * @param y2 The child of the second associated node to x (x2), corresponding to the given label
     * @param label Label of the arc to add
     * @param layer index of the layer where the node will be added
     * @param binder The binder
     * @return The node added
     */
    public static NodeInterface addArcAndNode(DecisionDiagram mdd, NodeInterface x, NodeInterface y1, NodeInterface y2, int label, int layer, Binder binder){
        NodeInterface y;
        if(binder == null){
            y = x.Node();
            y.associate(y1, y2);
            mdd.addNode(y, layer);
        } else {
            ArrayOfNodeInterface nodes = ArrayOfNodeInterface.create(2);
            nodes.set(0, y1); nodes.set(1, y2);
            Binder lastBinder = binder.path(nodes);
            y = lastBinder.getLeaf();
            if (y == null) {
                y = x.Node();
                y.associate(y1, y2);
                lastBinder.setLeaf(y);
                mdd.addNode(y, layer);
            }
            Memory.free(nodes);
        }
        mdd.addArc(x, label, y, layer-1);
        return y;
    }


    //**************************************//
    //           N-ARY OPERATIONS           //
    //**************************************//

    /**
     * Perform the concatenation of multiple MDDs.
     * Create a new MDD to stock the result.
     * The operation is performed in chain, so the first MDD will me on top and the last on bottom.
     * @param mdds The array of all MDDs
     * @return The concatenation of all the MDDs
     */
    public static MDD concatenate(ArrayOfMDD mdds){
        MDD result = mdds.get(0).copy();
        for(int i = 1; i < mdds.length(); i++){
            result.setSize(result.size() + mdds.get(i).size() - 1);
            mdds.get(i).copy(result, result.getTt(), result.size() - 1);
        }
        return result;
    }

    /**
     * Perform the union of multiple MDDs
     * Create a new MDD to stock the result.
     * @param mdds The array of all MDDs
     * @return the union of all the MDDs.
     */
    public static MDD union(ArrayOfMDD mdds){
        return perform(mdds, Operator.UNION);
    }

    /**
     * Perform the intersection of multiple MDDs
     * Create a new MDD to stock the result.
     * @param mdds The array of all MDDs
     * @return the intersection of all the MDDs.
     */
    public static MDD intersection(ArrayOfMDD mdds){
        return perform(mdds, Operator.INTERSECTION);
    }

    /**
     * Perform the intersection of multiple MDDs
     * Stock the result in the given result MDD.
     * @param result The MDD that will stock the result of the operation
     * @param mdds The array of all MDDs
     * @return the intersection of all the MDDs.
     */
    public static MDD intersection(MDD result, ArrayOfMDD mdds){
        if(mdds.length == 1) return mdds.get(0).copy(result);
        return perform(result, mdds, Operator.INTERSECTION);
    }

    /**
     * Check if an arc must be created, given the different inputs.
     * @param a Existence of the arc in the different MDDs
     * @param OP The type of operation
     * @return true if an arc must be created, false otherwise
     */
    private static boolean apply(ArrayOfBoolean a, Operator OP){
        switch (OP){
            case INTERSECTION:
            case INCLUSION:
                for(boolean b : a) if(!b) return false;
                return true;
            case UNION:
                for(boolean b : a) if(b) return true;
                return false;
        }
        return false;
    }

    /**
     * Create a new MDD to stock the result
     * @param mdds The array of all MDDs
     * @param OP Type of operation (union, intersection, ...)
     * @return The MDD resulting from : mdd1 OP mdd2 OP mdd3 OP ... OP mddn
     */
    private static MDD perform(ArrayOfMDD mdds, Operator OP){
        return perform(mdds.get(0).DD(), mdds, OP);
    }

    /**
     * Stock the result in the given result MDD
     * @param result The MDD that will contains the result of the operation
     * @param mdds The first MDD
     * @param OP Type of operation (union, intersection, ...)
     * @return The MDD resulting from : mdd1 OP mdd2 OP mdd3 OP ... OP mddn
     */
    private static MDD perform(MDD result, ArrayOfMDD mdds, Operator OP){
        // Allocations : Need to be free
        ArrayOfNodeInterface ys = ArrayOfNodeInterface.create(mdds.length());
        ArrayOfBoolean a = ArrayOfBoolean.create(mdds.length());
        Binder binder = Binder.create();
        //

        if(OP != Operator.INTERSECTION && OP != Operator.UNION) {
            throw new UnsupportedOperationException("N-ary operations only support intersection and union !");
        }
        result.setSize(mdds.get(0).size());

        SuccessionRule rule;
        if(OP == Operator.INTERSECTION) rule = SuccessionRule.INTERSECTION;
        else rule = SuccessionRule.UNION;

        CollectionOf<Integer> successors = rule.getCollection();


        for(int i = 0; i < mdds.length(); i++) ys.set(i, mdds.get(i).getRoot());
        result.getRoot().associate(ys);
        int r = result.size();

        Logger.out.information("\rN-ARY Intersection ["+mdds.length+" MDDs]\n");
        for(int i = 1; i < r; i++){
            Logger.out.information("\rCurrent layer : " + i);
            for(Node x : result.getLayer(i-1)){
                SuccessionOfNodeInterface xs = x.getAssociations();
                for(int v : rule.successors(successors, i-1, x)){
                    for(int n = 0; n < xs.length(); n++) a.set(n, xs.get(n).containsLabel(v));
                    if(apply(a, OP)) {
                        for(int n = 0; n < xs.length(); n++) ys.set(n, xs.get(n).getChild(v));
                        addArcAndNode(result, x, ys, v, i, binder);
                    }
                    for(int n = 0; n < xs.length(); n++) a.set(n, false);
                }
            }
            if(result.getLayer(i).size() == 0) return result;
            binder.clear();
        }
        Memory.free(ys);
        Memory.free(a);
        Memory.free(binder);
        Memory.free(successors);

        result.reduce();
        return result;
    }

    /**
     * Add an arc and a node to the given MDD
     * @param mdd The MDD where the node will be added
     * @param x The source node
     * @param ys The child of the associated nodes to x, corresponding to the given label
     * @param label Label of the arc to add
     * @param layer index of the layer where the node will be added
     * @param binder The binder
     */
    public static void addArcAndNode(MDD mdd, Node x, ArrayOfNodeInterface ys, int label, int layer, Binder binder){
        NodeInterface y;
        if(binder == null){
            y = Node.create();
            y.associate(ys);
            mdd.addNode(y, layer);
        } else {
            Binder lastBinder = binder.path(ys);
            y = lastBinder.getLeaf();
            if (y == null) {
                y = mdd.Node();
                y.associate(ys);
                lastBinder.setLeaf(y);
                mdd.addNode(y, layer);
            }
        }
        mdd.addArc(x, label, y, layer-1);
    }

}