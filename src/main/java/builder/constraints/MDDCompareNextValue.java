package builder.constraints;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;

public class MDDCompareNextValue {

    public enum OP {
        LT, LEQ, GT, GEQ, EQ, NEQ
    }

    public static MDD generate(MDD result, int size, ArrayOfInt V, OP op){
        result.setSize(size);

        // Each node is mapped to its *unique* in value
        MapOf<Integer, Node> nodes = Memory.MapOfIntegerNode();
        MapOf<Node, Integer> values = Memory.MapOfNodeInteger();

        values.put(result.getRoot(), 0);

        for(int i = 1; i < result.size(); i++){

            for(Node node : result.getLayer(i-1)){
                int last_val = values.get(node);
                values.remove(node);
                for(int v : V) {
                    // If value is less or equal, add it
                    if(condition(v, last_val, op, i - 1)) {
                        Node nextNode = nodes.get(v);
                        // Node doesn't exist yet
                        if(nextNode == null) {
                            nextNode = result.Node();
                            nodes.put(v, nextNode);
                            values.put(nextNode, v);
                            result.addNode(nextNode, i);
                        }
                        result.addArc(node, v, nextNode);
                    }
                }
            }

            nodes.clear();
        }


        result.reduce();
        return result;
    }

    private static boolean condition(int value, int last_value, OP op, int layer){
        if(layer == 0) return true;
        switch (op){
            case LT:
                return value < last_value;
            case LEQ:
                return value <= last_value;
            case GT:
                return value > last_value;
            case GEQ:
                return value >= last_value;
            case EQ:
                return value == last_value;
            case NEQ:
                return value != last_value;
        }
        return false;
    }

}
