package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import structures.Binder;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.TupleOfInt;
import utils.Logger;

import java.util.HashMap;

public class ConstraintMDD {

    // TODO : Memory Allocation
    private final HashMap<String, HashMap<String, PNode>> bindings = new HashMap<>();
    private final HashMap<String, Integer> order = new HashMap<>();
    private SetOf<Node> currentNodesConstraint = Memory.SetOfNode();
    private SetOf<Node> nextNodesConstraint = Memory.SetOfNode();
    private final ArrayOf<Node> associates = Memory.ArrayOfNode(10);
    private int numberOfConstraints = 0;

    public void feed(NodeProperty property, String name){
        PNode root = PMemory.PNode();
        property.setName(name);
        root.addProperty(name, property);
        order.put(name, numberOfConstraints);
        associates.set(1+(numberOfConstraints++), root);
        bindings.put(name, new HashMap<>());
    }

    public NodeProperty gcc(String name, MapOf<Integer, TupleOfInt> maxValues){
        NodeProperty property = PMemory.PropertyGCC(maxValues);
        feed(property, name);
        return property;
    }
    public NodeProperty gcc(MapOf<Integer, TupleOfInt> maxValues){
        return gcc(NodeProperty.GCC, maxValues);
    }

    public NodeProperty sequence(String name, int q, int min, int max, SetOf<Integer> V){
        NodeProperty property = PMemory.PropertyAmong(q, min, max, V);
        feed(property, name);
        return property;
    }
    public NodeProperty sequence(int q, int min, int max, SetOf<Integer> V){
        return sequence(NodeProperty.SEQ, q, min, max, V);
    }

    public NodeProperty sum(String name, int min, int max){
        NodeProperty property = PMemory.PropertySum(0, 0, min, max);
        feed(property, name);
        return property;
    }
    public NodeProperty sum(int min, int max){
        return sum(NodeProperty.SUM, min, max);
    }

    public NodeProperty alldiff(String name, SetOf<Integer> V){
        NodeProperty property = PMemory.PropertyAllDiff(V);
        feed(property, name);
        return property;
    }
    public NodeProperty alldiff(SetOf<Integer> V){
        return alldiff(NodeProperty.ALLDIFF, V);
    }

    public MDD intersection(MDD result, MDD mdd){
        result.setSize(mdd.size());

        associates.setLength(numberOfConstraints+1);
        associates.set(0, mdd.getRoot());
        result.getRoot().associate(associates);

        ArrayOf<Node> yis = Memory.ArrayOfNode(numberOfConstraints+1);

        Binder binder = Memory.Binder();
        SetOf<Node> tmp;

        for(int i = 1; i < mdd.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(Node node : result.getLayer(i-1)){
                //PNode x2 = (PNode) node.getX2();
                Node x1 = node.getX1();
                for(int value : x1.getValues()) {
                    boolean addArcAndNode = true;
                    for(String propertyName : bindings.keySet()) {
                        PNode xi = (PNode) node.getX(order.get(propertyName)+1);
                        NodeProperty property = xi.getProperty(propertyName);
                        if (property.isValid(value, i, mdd.size() - 1)) {
                            if (!xi.containsLabel(value)) {
                                String hash = property.hashstr(value);
                                PNode yi = bindings.get(propertyName).get(hash);
                                if (yi == null) {
                                    yi = PMemory.PNode();
                                    yi.addProperty(propertyName, property.createProperty(value));
                                    bindings.get(propertyName).put(hash, yi);
                                    nextNodesConstraint.add(yi);
                                }
                                xi.addChild(value, yi);
                            }
                        } else {
                            addArcAndNode = false;
                            break;
                        }
                    }
                    if(addArcAndNode) {
                        for(int k = 0; k < yis.length; k++) {
                            yis.set(k, node.getX(k).getChild(value));
                        }
                        Operation.addArcAndNode(result, node, yis, value, i, binder);
                    }
                }
            }
            for(Node node : currentNodesConstraint) Memory.free(node);
            currentNodesConstraint.clear();
            tmp = currentNodesConstraint;
            currentNodesConstraint = nextNodesConstraint;
            nextNodesConstraint = tmp;

            binder.clear();
            for(String propertyName : bindings.keySet()) bindings.get(propertyName).clear();

            if(result.getLayer(i).size() == 0) {
                System.out.println("EMPTY");
                return null;
            }

        }
        Memory.free(currentNodesConstraint);
        Memory.free(nextNodesConstraint);
        Memory.free(binder);

        result.reduce();
        return result;
    }

    public void clear(){
        bindings.clear();
        currentNodesConstraint.clear();
        nextNodesConstraint.clear();
        associates.clear();
        numberOfConstraints = 0;
    }

}
