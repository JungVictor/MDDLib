package mdd.operations;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import structures.Binder;
import structures.generics.MapOf;
import structures.generics.SetOf;

import java.util.HashMap;

public class ConstraintOperation {

    static public PMDD allDiff(MDD mdd){
        PMDD result = PMemory.PMDD();
        result.setSize(mdd.size());

        PNode constraint = PMemory.PNode();
        constraint.addProperty(NodeProperty.ALLDIFF, PMemory.PropertyAllDiff(mdd.getV()));

        result.getRoot().associates(mdd.getRoot(), constraint);

        intersection(result, mdd, constraint, NodeProperty.ALLDIFF);

        Memory.free(constraint);

        result.reduce();
        return result;
    }

    static public PMDD sum(MDD mdd, int min, int max){
        PMDD result = PMemory.PMDD();
        result.setSize(mdd.size());

        PNode constraint = PMemory.PNode();
        constraint.addProperty(NodeProperty.SUM, PMemory.PropertySum(0, 0, min, max));

        intersection(result, mdd, constraint, NodeProperty.SUM);

        Memory.free(constraint);

        result.reduce();
        return result;
    }

    static public MDD intersection(MDD result, MDD mdd, PNode constraint, String propertyName){
        result.getRoot().associates(mdd.getRoot(), constraint);

        Binder binder = Memory.Binder();
        HashMap<String, PNode> bindings = new HashMap<>();
        SetOf<Node> currentNodesConstraint = Memory.SetOfNode(),
                    nextNodesConstraint = Memory.SetOfNode(),
                    tmp;

        for(int i = 1; i < mdd.size(); i++){
            for(Node node : result.getLayer(i-1)){
                PNode x2 = (PNode) node.getX2();
                Node x1 = node.getX1();
                for(int value : x1.getValues()) {
                    NodeProperty property = x2.getProperty(propertyName);
                    if(!property.isDegenerate(value, i == mdd.size()-1)) {
                        if(!x2.containsLabel(value)) {
                            NodeProperty newProperty = property.createProperty(value);
                            PNode y2 = bindings.get(newProperty.toString());
                            if (y2 != null) Memory.free(newProperty);
                            else {
                                y2 = PMemory.PNode();
                                y2.addProperty(propertyName, newProperty);
                                bindings.put(newProperty.toString(), y2);
                                nextNodesConstraint.add(y2);
                            }
                            x2.addChild(value, y2);
                        }
                        MDDBuilder.addArcAndNode(result, node, x1.getChild(value), x2.getChild(value), value, i, binder);
                    }
                }
            }
            for(Node node : currentNodesConstraint) Memory.free(node);
            currentNodesConstraint.clear();
            tmp = currentNodesConstraint;
            currentNodesConstraint = nextNodesConstraint;
            nextNodesConstraint = tmp;

            binder.clear();
            bindings.clear();
        }
        Memory.free(currentNodesConstraint);
        Memory.free(nextNodesConstraint);
        Memory.free(binder);
        return result;
    }

}
