package mdd.operations;

import builder.constraints.parameters.*;
import builder.constraints.states.*;
import mdd.MDD;
import mdd.components.Node;
import mdd.components.SNode;
import memory.Memory;
import structures.Binder;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
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
    static public MDD allDiff(MDD result, MDD mdd, SetOf<Integer> V){
        SNode constraint = SNode.create();
        ParametersAllDiff parameters = ParametersAllDiff.create(V);
        constraint.setState(StateAllDiff.create(parameters));

        result.getRoot().associate(mdd.getRoot(), constraint);

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
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

        ArrayOfInt minValues = ArrayOfInt.create(mdd.size());
        ArrayOfInt maxValues = ArrayOfInt.create(mdd.size());

        for(int i = mdd.size() - 1; i >= 0; i--){
            int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            for(int v : mdd.getDomain(i)) {
                if(v < vMin) vMin = v;
                if(v > vMax) vMax = v;
            }
            if(i < mdd.size() - 1) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        SNode constraint = SNode.create();
        ParametersSum parameters = ParametersSum.create(min, max, minValues, maxValues);
        constraint.setState(StateSum.create(parameters));

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
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
        SNode constraint = SNode.create();
        ParametersGCC parameters = ParametersGCC.create(maxValues);
        StateGCC state = StateGCC.create(parameters);
        state.initV();
        constraint.setState(state);

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a sequence constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the sequence constraint
     */
    static public MDD sequence(MDD result, MDD mdd, int q, int min, int max, SetOf<Integer> V){
        SNode constraint = SNode.create();
        ParametersAmong parameters = ParametersAmong.create(q, min, max, V);
        constraint.setState(StateAmong.create(parameters));

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between the given mdd and the given constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param constraint The PNode containing the constraint (= root node of the constraint)
     */
    static protected void intersection(MDD result, MDD mdd, SNode constraint){
        result.setSize(mdd.size());
        result.getRoot().associate(mdd.getRoot(), constraint);

        Binder binder = Binder.create();
        HashMap<String, SNode> bindings = new HashMap<>();
        SetOf<Node> currentNodesConstraint = Memory.SetOfNode(),
                nextNodesConstraint = Memory.SetOfNode(),
                tmp;

        int node_constraint = 0;

        for(int i = 1; i < mdd.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(Node node : result.getLayer(i-1)){
                SNode x2 = (SNode) node.getX2();
                Node x1 = node.getX1();
                for(int value : x1.getValues()) {
                    NodeState state = x2.getState();
                    if(state.isValid(value, i, mdd.size())) {
                        if(!x2.containsLabel(value)) {
                            String hash = state.hash(value, i, mdd.size());
                            SNode y2 = bindings.get(hash);
                            if (y2 == null) {
                                y2 = SNode.create();
                                node_constraint++;
                                y2.setState(state.createState(value, i, mdd.size()));
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

        Logger.out.information("\rNoeuds :" + node_constraint + "\n");
    }

}
