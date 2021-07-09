package builder.constraints;

import builder.constraints.parameters.*;
import builder.constraints.states.NodeState;
import builder.constraints.states.StateGCC;
import mdd.MDD;
import mdd.components.Node;
import mdd.components.SNode;
import memory.Memory;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.TupleOfInt;
import utils.Logger;

import java.util.HashMap;

public class ConstraintBuilder {

    static private MDD build(MDD result, SNode constraint, Domains D, int size){
        result.setSize(size+1);
        result.setRoot(constraint);

        HashMap<String, SNode> bindings = new HashMap<>();
        SetOf<Node> currentNodesConstraint = Memory.SetOfNode(),
                nextNodesConstraint = Memory.SetOfNode(),
                tmp;
        int node_constraint = 0;

        for(int i = 1; i < result.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(Node node : result.getLayer(i-1)){
                SNode x = (SNode) node;
                for(int value : D.get(i-1)) {
                    NodeState state = x.getState();
                    if(state.isValid(value, i, result.size())) {
                        if(!x.containsLabel(value)) {
                            String hash = state.hash(value, i, result.size());
                            SNode y = bindings.get(hash);
                            if (y == null) {
                                y = Memory.SNode();
                                y.setState(state.createState(value, i, result.size()));
                                bindings.put(hash, y);
                                nextNodesConstraint.add(y);
                                node_constraint++;
                            }
                            result.addArcAndNode(x, value, y, i);
                        }
                    }
                }
            }
            currentNodesConstraint.clear();
            tmp = currentNodesConstraint;
            currentNodesConstraint = nextNodesConstraint;
            nextNodesConstraint = tmp;

            bindings.clear();
        }
        Memory.free(currentNodesConstraint);
        Memory.free(nextNodesConstraint);

        Logger.out.information(node_constraint);
        return result;
    }
    
    
    static public MDD subset(MDD result, ArrayOfInt letters, SetOf<Integer> D) {
        D.add(-1);
        SNode snode = Memory.SNode();
        ParametersSubset parameters = new ParametersSubset(null);
        parameters.init(letters, D.size());
        snode.setState(Memory.StateSubset(parameters, 0));

        build(result, snode, D, letters.length);

        //Memory.free(parameters);

        result.reduce();
        return result;
    }
    

    static public MDD sequence(MDD result, Domains D, SetOf<Integer> V, int q, int min, int max, int size){
        SNode snode = Memory.SNode();
        ParametersAmong parameters = Memory.ParametersAmong(q, min, max, V);
        snode.setState(Memory.StateAmong(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);

        result.reduce();
        return result;
    }
    static public MDD sequence(MDD result, int q, int min, int max, int size){
        Domains B = Memory.Domains();
        for(int i = 0; i < size; i++) {
            B.add(i);
            B.put(i, 0); B.put(i, 1);
        }
        SetOf<Integer> One = Memory.SetOfInteger(); One.add(1);
        SNode snode = Memory.SNode();
        ParametersAmong parameters = Memory.ParametersAmong(q, min, max, One);
        snode.setState(Memory.StateAmong(parameters));

        build(result, snode, B, size);

        Memory.free(parameters);
        Memory.free(B);
        Memory.free(One);

        result.reduce();

        return result;
    }

    static public MDD sum(MDD result, Domains D, int min, int max, int size){
        SNode snode = Memory.SNode();
        ArrayOfInt minValues = Memory.ArrayOfInt(size);
        ArrayOfInt maxValues = Memory.ArrayOfInt(size);

        for(int i = size - 2; i >= 0; i--){
            int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            for(int v : D.get(i)) {
                if(v < vMin) vMin = v;
                if(v > vMax) vMax = v;
            }
            if(i < size - 1) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSum parameters = Memory.ParametersSum(min, max, minValues, maxValues);
        snode.setState(Memory.StateSum(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }
    static public MDD sum(MDD result, SetOf<Integer> V, int min, int max, int size){
        Domains D = Memory.Domains();
        for(int i = 0; i < size; i++) {
            D.add(i);
            D.get(i).add(V);
        }
        sum(result, D, min, max, size);
        Memory.free(D);
        return result;
    }

    static public MDD gcc(MDD result, Domains D, MapOf<Integer, TupleOfInt> maxValues, int size){
        SNode constraint = Memory.SNode();
        ParametersGCC parameters = Memory.ParametersGCC(maxValues);
        StateGCC state = Memory.StateGCC(parameters);
        state.initV();
        constraint.setState(state);

        build(result, constraint, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    static public MDD allDiff(MDD result, Domains D, SetOf<Integer> V, int size){
        SNode constraint = Memory.SNode();
        ParametersAllDiff parameters = Memory.ParametersAllDiff(V);
        constraint.setState(Memory.StateAllDiff(parameters));

        build(result, constraint, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }
    static public MDD allDiff(MDD result, SetOf<Integer> V, int size){
        Domains D = Memory.Domains();
        for(int i = 0; i < size; i++) {
            D.add(i);
            D.get(i).add(V);
        }
        allDiff(result, D, V, size);
        Memory.free(D);
        return result;
    }

    static public MDD diff(MDD result, Domains D, int length, int size){
        SNode snode = Memory.SNode();
        ParametersDiff parameters = Memory.ParametersDiff(length);
        snode.setState(Memory.StateDiff(parameters, size+1));

        build(result, snode, D, size);

        Memory.free(parameters);

        return result;
    }
}
