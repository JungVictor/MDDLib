package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import structures.booleans.ArrayOfBoolean;
import structures.generics.ArrayOf;
import structures.Binder;
import structures.generics.SetOf;
import utils.Logger;

/**
 * The class dedicated to perform classical operation on and between MDDs
 */
public class Operation {

    private enum Operator {
        UNION, INTERSECTION, DIAMOND, MINUS, INCLUSION;
    }

    //**************************************//
    //           UNARY OPERATIONS           //
    //**************************************//

    public static MDD negation(MDD mdd){
        MDD universal = Memory.MDD();
        return minus(universal, mdd);
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
    public static MDD intersection(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.INTERSECTION);
    }

    /**
     * Perform an intersection between mdd1 and mdd2.
     * Stock the result in the given result MDD.
     * @param result The MDD to stock the result
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @return The result of the intersection between mdd1 and mdd2
     */
    public static MDD intersection(MDD result, MDD mdd1, MDD mdd2){
        return perform(result, mdd1, mdd2, Operator.INTERSECTION);
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
    public static MDD intersection(MDD mdd1, MDD mdd2, int start, int stop, int size){
        return intersection(mdd1.MDD(), mdd1, mdd2, start, stop, size);
    }

    /**
     * Perform an intersection between mdd1 and mdd2 from layer start to layer stop.
     * Stock the result in the given result MDD.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @param start The starting layer
     * @param stop The stopping layer
     * @param size The size of the result
     * @return The result of the intersection between mdd1 and mdd2 from layer start to layer stop.
     */
    public static MDD intersection(MDD result, MDD mdd1, MDD mdd2, int start, int stop, int size){
        mdd1.clearAllAssociations();
        // Copy the first MDD until the first layer of intersection
        result.setSize(size);
        mdd1.getRoot().associates(result.getRoot(), null);
        mdd1.copy(result, 0, 0, start);

        // Binding all nodes in layer start to the root of mdd2
        if(start > 0)
            for(Node node : mdd1.getLayer(start))
                node.getX1().associates(node, mdd2.getRoot());
        else result.getRoot().associates(mdd1.getRoot(), mdd2.getRoot());

        // Normal intersection
        SetOf<Integer> V = Memory.SetOfInteger();
        Binder binder = Memory.Binder();

        // Construction of V
        V.add(mdd1.getV());
        V.intersect(mdd2.getV());

        for(int i = start+1; i < stop; i++){
            for(Node x : result.getLayer(i-1)){
                Node x1 = x.getX1(), x2 = x.getX2();
                for(int v : V){
                    boolean a1 = x1.containsLabel(v), a2 = a1;
                    if(x2 != x1) a2 = x2.containsLabel(v);
                    if(apply(a1, a2, Operator.INTERSECTION, i == stop - 1)) {
                        addArcAndNode(result, x, x1.getChild(v), x2.getChild(v), v, i, binder);
                    }
                }
            }
            if(result.getLayer(i).size() == 0) {
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
                // Therefore, we need to construct the rest of the mdd from mdd1
                for(Node node : result.getLayer(stop-1)) node.getX1().associates(node, null);
                mdd1.copy(result, 0, stop-1, mdd1.size());
            } else if (stop >= mdd1.size()){
                // We need to construct the rest of the mdd from mdd2
                for(Node node : result.getLayer(stop-1)) node.getX2().associates(node, null);
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
    public static MDD concatenate(MDD mdd1, MDD mdd2){
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
    public static MDD union(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.UNION);
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
    public static MDD diamond(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.DIAMOND);
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
    public static MDD minus(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.MINUS);
    }


    // --------- //
    // INCLUSION //
    // --------- //

    /**
     * Check if mdd2 is included in mdd1.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @return true if mdd2 is included in mdd1, false otherwise
     */
    public static boolean inclusion(MDD mdd1, MDD mdd2){
        if(mdd2.size() > mdd1.size()) return false;

        // Construction of V
        SetOf<Integer> V = Memory.SetOfInteger();
        V.add(mdd1.getV()); V.intersect(mdd2.getV());

        if(V.size() == 0) return false;

        boolean result = inclusion(mdd1.getRoot(), mdd2.getRoot(), mdd2.size(), V);
        Memory.free(V);
        return result;
    }

    /**
     * Check if the sub-MDD of the given size starting from node root2 is included in the sub-MDD starting from
     * node root1 of the given size.
     * @param root1 The first root
     * @param root2 The second root
     * @param size The size of both sub-MDDs
     * @param V The set of values to check
     * @return true if there is an inclusion, false otherwise
     */
    public static boolean inclusion(Node root1, Node root2, int size, SetOf<Integer> V){
        MDD inclusion = Memory.MDD();
        boolean result = perform(inclusion, root1, root2, size, V, Operator.INCLUSION) != null;
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
     * @param V The set of values to consider during the operation
     * @param OP The type of operation
     * @return The MDD resulting from the operation
     */
    private static MDD perform(MDD result, Node root1, Node root2, int size, SetOf<Integer> V, Operator OP){
        result.setSize(size);
        Binder binder = Memory.Binder();

        result.getRoot().associates(root1, root2);

        for(int i = 1; i < size; i++){
            for(Node x : result.getLayer(i-1)){
                Node x1 = x.getX1(), x2 = x.getX2();
                for(int v : V){
                    boolean a1 = x1.containsLabel(v), a2 = a1;
                    if(x2 != x1) a2 = x2.containsLabel(v);
                    if(apply(a1, a2, OP, i == size - 1)) {
                        addArcAndNode(result, x, x1.getChild(v), x2.getChild(v), v, i, binder);
                    }
                    else if(OP == Operator.INCLUSION && a1) return null;
                }
            }
            if(result.getLayer(i).size() == 0) {
                if (OP == Operator.INCLUSION) return null;
                return result;
            }
            binder.clear();
        }
        Memory.free(binder);

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
    private static MDD perform(MDD result, MDD mdd1, MDD mdd2, Operator OP){
        result.setSize(mdd1.size());
        // Construction of V
        SetOf<Integer> V = Memory.SetOfInteger();
        V.add(mdd1.getV());
        if(OP == Operator.INTERSECTION) V.intersect(mdd2.getV());
        else V.add(mdd2.getV());

        perform(result, mdd1.getRoot(), mdd2.getRoot(), mdd1.size(), V, OP);
        Memory.free(V);
        return result;
    }

    /**
     * Create a new MDD by applying the operator OP between mdd1 and mdd2.
     * @param mdd1 The first MDD
     * @param mdd2 The second MDD
     * @param OP Type of operation (union, intersection, ...)
     * @return The MDD resulting from : mdd1 OP mdd2.
     */
    private static MDD perform(MDD mdd1, MDD mdd2, Operator OP){
        return perform(mdd1.MDD(), mdd1, mdd2, OP);
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
     */
    public static void addArcAndNode(MDD mdd, Node x, Node y1, Node y2, int label, int layer, Binder binder){
        if(y1 == null) y1 = y2;
        else if(y2 == null) y2 = y1;

        Node y;
        if(binder == null){
            y = Memory.Node();
            y.associates(y1, y2);
            mdd.addNode(y, layer);
        } else {
            ArrayOf<Node> nodes = Memory.ArrayOfNode(2);
            nodes.set(0, y1); nodes.set(1, y2);
            Binder lastBinder = binder.path(nodes);
            y = lastBinder.getLeaf();
            if (y == null) {
                y = mdd.Node();
                y.associates(y1, y2);
                lastBinder.setLeaf(y);
                mdd.addNode(y, layer);
            }
            Memory.free(nodes);
        }
        mdd.addArc(x, label, y);
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
    public static MDD concatenate(ArrayOf<MDD> mdds){
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
    public static MDD union(ArrayOf<MDD> mdds){
        return perform(mdds, Operator.UNION);
    }

    /**
     * Perform the intersection of multiple MDDs
     * Create a new MDD to stock the result.
     * @param mdds The array of all MDDs
     * @return the intersection of all the MDDs.
     */
    public static MDD intersection(ArrayOf<MDD> mdds){
        return perform(mdds, Operator.INTERSECTION);
    }

    /**
     * Perform the intersection of multiple MDDs
     * Stock the result in the given result MDD.
     * @param mdds The array of all MDDs
     * @return the intersection of all the MDDs.
     */
    public static MDD intersection(MDD result, ArrayOf<MDD> mdds){
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
    private static MDD perform(ArrayOf<MDD> mdds, Operator OP){
        return perform(mdds.get(0).MDD(), mdds, OP);
    }

    /**
     * Stock the result in the given result MDD
     * @param result The MDD that will contains the result of the operation
     * @param mdds The first MDD
     * @param OP Type of operation (union, intersection, ...)
     * @return The MDD resulting from : mdd1 OP mdd2 OP mdd3 OP ... OP mddn
     */
    private static MDD perform(MDD result, ArrayOf<MDD> mdds, Operator OP){

        // Allocations : Need to be free
        ArrayOf<Node> ys = Memory.ArrayOfNode(mdds.length());
        ArrayOfBoolean a = Memory.ArrayOfBoolean(mdds.length());
        Binder binder = Memory.Binder();
        SetOf<Integer> V = Memory.SetOfInteger();
        //

        if(OP != Operator.INTERSECTION && OP != Operator.UNION) {
            throw new UnsupportedOperationException("N-ary operations only support intersection and union !");
        }
        result.setSize(mdds.get(0).size());

        // Construction of V
        V.add(mdds.get(0).getV());
        if(OP == Operator.INTERSECTION) for(int i = 1; i < mdds.length(); i++) V.intersect(mdds.get(i).getV());
        else for(int i = 1; i < mdds.length(); i++) V.add(mdds.get(i).getV());


        for(int i = 0; i < mdds.length(); i++) ys.set(i, mdds.get(i).getRoot());
        result.getRoot().associates(ys);
        int r = result.size();

        Logger.out.information("N-ARY Intersection ["+mdds.length+" MDDs]\n");
        for(int i = 1; i < r; i++){
            Logger.out.information("\rCurrent layer : " + i);
            for(Node x : result.getLayer(i-1)){
                ArrayOf<Node> xs = x.getAssociations();
                for(int v : V){
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
        Memory.free(V);

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
    public static void addArcAndNode(MDD mdd, Node x, ArrayOf<Node> ys, int label, int layer, Binder binder){
        Node y;
        if(binder == null){
            y = Memory.Node();
            y.associates(ys);
            mdd.addNode(y, layer);
        } else {
            Binder lastBinder = binder.path(ys);
            y = lastBinder.getLeaf();
            if (y == null) {
                y = mdd.Node();
                y.associates(ys);
                lastBinder.setLeaf(y);
                mdd.addNode(y, layer);
            }
        }
        mdd.addArc(x, label, y);
    }

}
