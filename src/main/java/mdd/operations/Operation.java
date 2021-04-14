package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import representation.MDDPrinter;
import structures.booleans.ArrayOfBoolean;
import structures.generics.ArrayOf;
import structures.Binder;
import structures.generics.SetOf;
import utils.Logger;

import java.lang.reflect.Array;

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

    public static MDD layerIntersection(MDD mdd1, MDD mdd2, int start){
        return layerIntersection(mdd1.MDD(), mdd1, mdd2, start, Math.min(start + mdd2.size(), mdd1.size()), Math.max(start + mdd2.size(), mdd1.size()));
    }
    public static MDD layerIntersection(MDD mdd1, MDD mdd2, int start, int stop, int size){
        return layerIntersection(mdd1.MDD(), mdd1, mdd2, start, stop, size);
    }
    public static MDD layerIntersection(MDD result, MDD mdd1, MDD mdd2, int start, int stop, int size){
        mdd1.clearAllAssociations();
        mdd2.clearAllAssociations();
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

    public static MDD concatenate(MDD mdd1, MDD mdd2){
        MDD result = mdd1.copy();
        result.setSize(mdd1.size() + mdd2.size() - 1);
        mdd2.copy(result, result.getTt(), mdd1.size() - 1);
        return result;
    }

    public static MDD union(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.UNION);
    }

    public static MDD intersection(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.INTERSECTION);
    }
    public static MDD intersection(MDD result, MDD mdd1, MDD mdd2){
        return perform(result, mdd1, mdd2, Operator.INTERSECTION);
    }

    public static MDD diamond(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.DIAMOND);
    }

    public static MDD minus(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.MINUS);
    }

    public static boolean inclusion(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.INCLUSION) != null;
    }

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
     * Create a new MDD by applying the operator OP between this and mdd.
     * @param OP Type of operation (union, intersection[])
     * @return The MDD resulting from : this OP mdd.
     */
    private static MDD perform(MDD result, MDD mdd1, MDD mdd2, Operator OP){
        result.setSize(mdd1.size());
        Binder binder = Memory.Binder();

        // Construction of V
        SetOf<Integer> V = Memory.SetOfInteger();
        V.add(mdd1.getV());
        if(OP == Operator.INTERSECTION) V.intersect(mdd2.getV());
        else V.add(mdd2.getV());


        result.getRoot().associates(mdd1.getRoot(), mdd2.getRoot());
        int r = mdd1.size();

        for(int i = 1; i < r; i++){
            for(Node x : result.getLayer(i-1)){
                Node x1 = x.getX1(), x2 = x.getX2();
                for(int v : V){
                    boolean a1 = x1.containsLabel(v), a2 = a1;
                    if(x2 != x1) a2 = x2.containsLabel(v);
                    if(apply(a1, a2, OP, i == r - 1)) {
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
        Memory.free(V);

        result.reduce();
        return result;
    }
    private static MDD perform(MDD mdd1, MDD mdd2, Operator OP){
        return perform(mdd1.MDD(), mdd1, mdd2, OP);
    }

    private static void addArcAndNode(MDD mdd, Node x, Node y1, Node y2, int label, int layer, Binder binder){
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

    public static MDD concatenate(ArrayOf<MDD> mdds){
        MDD result = mdds.get(0).copy();
        for(int i = 1; i < mdds.length(); i++){
            result.setSize(result.size() + mdds.get(i).size() - 1);
            mdds.get(i).copy(result, result.getTt(), result.size() - 1);
        }
        return result;
    }

    public static MDD union(ArrayOf<MDD> mdds){
        return perform(mdds, Operator.UNION);
    }

    public static MDD intersection(ArrayOf<MDD> mdds){
        return perform(mdds, Operator.INTERSECTION);
    }
    public static MDD intersection(MDD result, ArrayOf<MDD> mdds){
        if(mdds.length == 1) return mdds.get(0).copy(result);
        return perform(result, mdds, Operator.INTERSECTION);
    }

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

    private static MDD perform(ArrayOf<MDD> mdds, Operator OP){
        return perform(mdds.get(0).MDD(), mdds, OP);
    }
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

    private static void addArcAndNode(MDD mdd, Node x, ArrayOf<Node> ys, int label, int layer, Binder binder){
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
