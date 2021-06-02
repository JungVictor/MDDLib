package mdd.operations;

import builder.constraints.parameters.*;
import builder.constraints.states.NodeState;
import builder.constraints.states.StateAmong;
import builder.constraints.states.StateGCC;
import mdd.MDD;
import mdd.components.Node;
import mdd.components.SNode;
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
        SNode constraint = Memory.SNode();
        ParametersAllDiff parameters = Memory.ParametersAllDiff(mdd.getV());
        constraint.setState(Memory.StateAllDiff(parameters));

        result.getRoot().associates(mdd.getRoot(), constraint);

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a diff constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param length The length between different values
     * @return the MDD resulting from the intersection between mdd and the alldiff constraint
     */
    static public MDD diff(MDD result, MDD mdd, int length, boolean carry){
        SNode constraint = Memory.SNode();
        ParametersDiff parameters = Memory.ParametersDiff(length);
        constraint.setState(Memory.StateDiff(parameters, mdd.size()));

        result.setRoot(Memory.SNode());
        ((SNode) result.getRoot()).setState(Memory.StateDiff(parameters, mdd.size()));
        result.getRoot().associates(mdd.getRoot(), constraint);

        intersection(result, mdd, constraint, carry);

        Memory.free(constraint);
        Memory.free(parameters);
        //result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a sum constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the sum constraint
     */
    static public MDD sum(MDD result, MDD mdd, int min, int max){

        int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
        for(int v : mdd.getV()) {
            if(v < vMin) vMin = v;
            if(v > vMax) vMax = v;
        }

        SNode constraint = Memory.SNode();
        ParametersSum parameters = Memory.ParametersSum(min, max, vMin, vMax);
        constraint.setState(Memory.StateSum(parameters));

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
        SNode constraint = Memory.SNode();
        ParametersGCC parameters = Memory.ParametersGCC(maxValues);
        StateGCC state = Memory.StateGCC(parameters);
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
    static public MDD sequence(MDD result, MDD mdd, int q, int min, int max){
        SNode constraint = Memory.SNode();
        ParametersAmong parameters = Memory.ParametersAmong(q, min, max, mdd.getV());
        constraint.setState(Memory.StateAmong(parameters));

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
    static private void intersection(MDD result, MDD mdd, SNode constraint){
        result.setSize(mdd.size());
        result.getRoot().associates(mdd.getRoot(), constraint);

        Binder binder = Memory.Binder();
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
                                y2 = Memory.SNode();
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

    /**
     * Perform the intersection operation between the given mdd and the given constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param constraint The PNode containing the constraint (= root node of the constraint)
     */
    static private void intersection(MDD result, MDD mdd, SNode constraint, boolean carryState){
        result.setSize(mdd.size());
        result.getRoot().associates(mdd.getRoot(), constraint);

        Binder binder = Memory.Binder();
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
                    Node y1 = x1.getChild(value);
                    boolean isValid;
                    if(carryState) isValid = state.isValid(value, i, mdd.size(), ((SNode) y1).getState());
                    else isValid = state.isValid(value, i, mdd.size());

                    if(isValid) {
                        if(!x2.containsLabel(value)) {
                            String hash = state.hash(value, i, mdd.size());
                            SNode y2 = bindings.get(hash);
                            if (y2 == null) {
                                y2 = Memory.SNode();
                                node_constraint++;
                                y2.setState(state.createState(value, i, mdd.size()));
                                bindings.put(hash, y2);
                                nextNodesConstraint.add(y2);
                            }
                            x2.addChild(value, y2);
                        }
                        SNode y2 = (SNode) x2.getChild(value);
                        SNode y = (SNode) Operation.addArcAndNode(result, node, y1, y2, value, i, binder);
                        if(carryState) y.setState(y2.getState().merge(((SNode) y1).getState(), value, i, mdd.size()));
                        else y.setState(y2.getState().copy());
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
