package builder.constraints;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.components.Layer;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Memory;
import structures.Binder;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;

import java.util.HashMap;

public class MDDSum {

    private MDDSum(){}

    public static MDD generate(MDD sum, int s_min, int s_max, int n, ArrayOfInt V){
        sum.setSize(n+1);
        MapOf<Integer, Node> nodes = Memory.MapOfIntegerNode();
        MapOf<Integer, Node> next = Memory.MapOfIntegerNode();
        MapOf<Integer, Node> tmp;
        nodes.put(0, sum.getRoot());
        for(int layer = 1; layer < n+1; layer++){
            for(int cSum : nodes.keySet()){
                Node current = nodes.get(cSum);
                for(int v : V) {
                    int nSum = cSum + v;
                    if ((nSum <= s_max && layer < n) || (nSum >= s_min && nSum <= s_max)) {
                        Node node;
                        if (!next.contains(nSum)) {
                            node = sum.Node();
                            next.put(nSum, node);
                            sum.addNode(node, layer);
                        } else node = next.get(nSum);
                        sum.addArc(current, v, node);
                    }
                }
            }
            nodes.clear();
            tmp = nodes;
            nodes = next;
            next = tmp;
        }
        Memory.free(nodes);
        Memory.free(next);
        sum.reduce();
        return sum;
    }

    public static MDD intersection(MDD result, MDD mdd, int s_min, int s_max){
        result.setSize(mdd.size());

        MapOf<Node, Integer> nodeValue = Memory.MapOfNodeInteger(),
                             nextNodeValue = Memory.MapOfNodeInteger(),
                             tmpNodeValue;

        MapOf<Integer, Node> nodes = Memory.MapOfIntegerNode(),
                             next = Memory.MapOfIntegerNode(),
                             tmp;

        Binder binder = Memory.Binder();

        Node root = Memory.Node();
        nodes.put(0, root);
        nodeValue.put(root, 0);
        result.getRoot().associates(mdd.getRoot(), root);

        for(int i = 1; i < result.size(); i++){
            for(Node x : result.getLayer(i-1)){
                Node x1 = x.getX1(), x2 = x.getX2();
                int cSum = nodeValue.get(x2);
                for(int v : x1.getValues()) {
                    int nSum = cSum + v;
                    if ((nSum <= s_max && i < result.size() - 1) || (nSum >= s_min && nSum <= s_max)) {
                        Node y2;
                        if (!next.contains(nSum)) {
                            y2 = root.Node();
                            next.put(nSum, y2);
                            nextNodeValue.put(y2, nSum);
                        } else y2 = next.get(nSum);
                        x2.addChild(v, y2);
                        MDDBuilder.addArcAndNode(result, x, x1.getChild(v), y2, v, i, binder);
                    }
                }
                x.clearAssociations();
            }
            nodes.clear();
            tmp = nodes;
            nodes = next;
            next = tmp;

            for(Node node : nodeValue) Memory.free(node);
            nodeValue.clear();
            tmpNodeValue = nodeValue;
            nodeValue = nextNodeValue;
            nextNodeValue = tmpNodeValue;

            if(result.getLayer(i).size() == 0) {
                Memory.free(nodes);
                Memory.free(next);
                Memory.free(nodeValue);
                Memory.free(nextNodeValue);
                return result;
            }
        }

        Memory.free(nodes);
        Memory.free(next);
        Memory.free(nodeValue);
        Memory.free(nextNodeValue);
        result.reduce();
        return result;
    }

}
