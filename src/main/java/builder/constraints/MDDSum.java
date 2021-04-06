package builder.constraints;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
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
        //MemoryManager.freeLast(nodes, 2);
        Memory.free(nodes);
        Memory.free(next);
        sum.reduce();
        return sum;
    }

}
