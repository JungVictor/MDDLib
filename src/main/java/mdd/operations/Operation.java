package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import structures.generics.ArrayOf;
import structures.Binder;
import structures.generics.SetOf;

public class Operation {

    private enum Operator {
        UNION, INTERSECTION, DIAMOND, MINUS, INCLUSION;
    }

    public static MDD union(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.UNION);
    }

    public static MDD intersection(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.INTERSECTION);
    }

    public static MDD diamond(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.DIAMOND);
    }

    public static MDD minus(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.MINUS);
    }

    public static MDD negation(MDD mdd){
        MDD universal = Memory.MDD();
        return minus(universal, mdd);
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
    private static MDD perform(MDD mdd1, MDD mdd2, Operator OP){
        MDD result = Memory.MDD();
        result.setSize(mdd1.getSize());
        Binder binder = Memory.Binder();

        // Construction of V
        SetOf<Integer> V = Memory.SetOfInteger();
        V.add(mdd1.getV());
        if(OP == Operator.INCLUSION) V.intersect(mdd2.getV());
        else V.add(mdd2.getV());


        result.getRoot().associates(mdd1.getRoot(), mdd2.getRoot());
        int r = mdd1.getSize();

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
                y = Memory.Node();
                y.associates(y1, y2);
                lastBinder.setLeaf(y);
                mdd.addNode(y, layer);
            }
            Memory.free(nodes);
        }
        mdd.addArc(x, label, y);
    }

}
