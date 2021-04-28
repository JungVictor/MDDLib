package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import structures.generics.MapOf;

import java.util.HashMap;

public class ConstraintOperation {

    static public MDD allDiff(MDD mdd){
        PMDD result = PMemory.PMDD();
        result.setSize(mdd.size());
        result.addRootProperty(NodeProperty.ALLDIFF, PMemory.PropertyAllDiff(mdd.getV()));

        result.getRoot().associates(mdd.getRoot(), result.getRoot());

        intersection(result, mdd, result);

        result.reduce();
        return result;
    }

    static public MDD intersection(MDD result, MDD mdd, MDD constraint){
        result.getRoot().associates(mdd.getRoot(), constraint.getRoot());
        HashMap<String, PNode> bindings = new HashMap<>();

        for(int i = 0; i < mdd.size()-1; i++){
            for(Node node : result.getLayer(i)){
                PNode x2 = (PNode) node.getX2();
                Node x1 = node.getX1();
                for(int value : x1.getValues()) {
                    NodeProperty property = x2.getProperty(NodeProperty.ALLDIFF);

                    if(!property.isDegenerate(value)){
                        NodeProperty newProperty = property.createProperty(value);
                        PNode y2 = bindings.get(newProperty.toString());
                        if(y2 != null) Memory.free(newProperty);
                        else {
                            y2 = PMemory.PNode();
                            y2.addProperty(NodeProperty.ALLDIFF, newProperty);
                            constraint.addNode(y2, i+1);
                            bindings.put(newProperty.toString(), y2);
                        }
                        constraint.addArc(x2, value, y2);
                        node.getChild(value).associates(x1.getChild(value), y2);
                    }
                }
            }
            bindings.clear();
        }
        return result;
    }

}
