package builder.constraints;

import builder.constraints.parameters.ParametersAmong;
import builder.constraints.parameters.ParametersDiff;
import builder.constraints.parameters.ParametersGCC;
import builder.constraints.parameters.ParametersSum;
import builder.constraints.states.NodeState;
import builder.constraints.states.StateGCC;
import mdd.MDD;
import mdd.components.Node;
import mdd.components.SNode;
import mdd.operations.Operation;
import memory.Memory;
import structures.Binder;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.TupleOfInt;
import utils.Logger;

import java.util.HashMap;

public class ConstraintBuilder {

    static private MDD build(MDD result, SNode constraint, SetOf<Integer> D, int size){
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
                for(int value : D) {
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

    static public MDD sequence(MDD result, SetOf<Integer> D, SetOf<Integer> V, int q, int min, int max, int size){
        SNode snode = Memory.SNode();
        ParametersAmong parameters = Memory.ParametersAmong(q, min, max, V);
        snode.setState(Memory.StateAmong(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);

        return result;
    }
    static public MDD sequence(MDD result, int q, int min, int max, int size){
        SetOf<Integer> B = Memory.SetOfInteger(); B.add(0); B.add(1);
        SetOf<Integer> One = Memory.SetOfInteger(); One.add(1);
        SNode snode = Memory.SNode();
        ParametersAmong parameters = Memory.ParametersAmong(q, min, max, One);
        snode.setState(Memory.StateAmong(parameters));

        build(result, snode, B, size);

        Memory.free(parameters);
        Memory.free(B);
        Memory.free(One);

        return result;
    }

    static public MDD sum(MDD result, SetOf<Integer> V, int min, int max, int size){
        SNode snode = Memory.SNode();
        int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
        for(int v : V){
            if(v > vMax) vMax = v;
            if(v < vMin) vMin = v;
        }
        ParametersSum parameters = Memory.ParametersSum(min, max, vMin, vMax);
        snode.setState(Memory.StateSum(parameters));

        build(result, snode, V, size);

        Memory.free(parameters);

        return result;
    }

    static public MDD gcc(MDD result, SetOf<Integer> D, MapOf<Integer, TupleOfInt> maxValues, int size){
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

    static public MDD diff(MDD result, SetOf<Integer> D, int length, int size){
        SNode snode = Memory.SNode();
        ParametersDiff parameters = Memory.ParametersDiff(length);
        snode.setState(Memory.StateDiff(parameters, size+1));

        build(result, snode, D, size);

        Memory.free(parameters);

        return result;
    }
}
