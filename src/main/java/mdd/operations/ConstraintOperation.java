package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import structures.Binder;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.TupleOfInt;
import utils.Logger;

import java.util.HashMap;

/**
 * <b>The class dedicated to perform on-the-fly constraint operations</b>
 */
public class ConstraintOperation {

    /**
     * Perform the intersection operation between mdd and a alldiff constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the alldiff constraint
     */
    static public MDD allDiff(MDD result, MDD mdd){
        PNode constraint = PMemory.PNode();
        constraint.addProperty(NodeProperty.ALLDIFF, PMemory.PropertyAllDiff(mdd.getV()));

        result.getRoot().associates(mdd.getRoot(), constraint);

        intersection(result, mdd, constraint, NodeProperty.ALLDIFF);

        Memory.free(constraint);

        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a sum constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the sum constraint
     */
    static public MDD sum(MDD result, MDD mdd, int min, int max){
        PNode constraint = PMemory.PNode();
        constraint.addProperty(NodeProperty.SUM, PMemory.PropertySum(0, 0, min, max));

        intersection(result, mdd, constraint, NodeProperty.SUM);

        Memory.free(constraint);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a gcc constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the gcc constraint
     */
    static public MDD gcc(MDD result, MDD mdd, MapOf<Integer, TupleOfInt> maxValues){
        PNode constraint = PMemory.PNode();
        constraint.addProperty(NodeProperty.GCC, PMemory.PropertyGCC(maxValues));

        intersection(result, mdd, constraint, NodeProperty.GCC);

        Memory.free(constraint);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a sequence constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the sequence constraint
     */
    static public MDD sequence(MDD result, MDD mdd, int q, int min, int max){
        PNode constraint = PMemory.PNode();
        constraint.addProperty(NodeProperty.AMONG, PMemory.PropertyAmong(q, min, max, mdd.getV()));

        intersection(result, mdd, constraint, NodeProperty.AMONG);

        Memory.free(constraint);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between the given mdd and the given constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param constraint The PNode containing the constraint (= root node of the constraint)
     * @param propertyName The name of the property to propagate
     */
    static private void intersection(MDD result, MDD mdd, PNode constraint, String propertyName){
        result.setSize(mdd.size());
        result.getRoot().associates(mdd.getRoot(), constraint);

        Binder binder = Memory.Binder();
        HashMap<String, PNode> bindings = new HashMap<>();
        SetOf<Node> currentNodesConstraint = Memory.SetOfNode(),
                    nextNodesConstraint = Memory.SetOfNode(),
                    tmp;

        for(int i = 1; i < mdd.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(Node node : result.getLayer(i-1)){
                PNode x2 = (PNode) node.getX2();
                Node x1 = node.getX1();
                for(int value : x1.getValues()) {
                    NodeProperty property = x2.getProperty(propertyName);
                    if(property.isValid(value, i, mdd.size()-1)) {
                        if(!x2.containsLabel(value)) {
                            String hash = property.hashstr(value);
                            PNode y2 = bindings.get(hash);
                            if (y2 == null) {
                                y2 = PMemory.PNode();
                                y2.addProperty(propertyName, property.createProperty(value));
                                bindings.put(hash, y2);
                                nextNodesConstraint.add(y2);
                            }
                            x2.addChild(value, y2);
                        }
                        Operation.addArcAndNode(result, node, x1.getChild(value), x2.getChild(value), value, i, binder);
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
    }

}
