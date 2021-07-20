package builder.constraints;

import builder.constraints.parameters.*;
import builder.constraints.states.*;
import mdd.MDD;
import mdd.components.Node;
import mdd.components.SNode;
import memory.Memory;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import structures.integers.TupleOfInt;
import utils.Logger;

import java.util.HashMap;

public class ConstraintBuilder {

    protected static MDD build(MDD result, SNode constraint, Domains D, int size){
        return build(result, constraint, D, size, false);
    }
    protected static MDD build(MDD result, SNode constraint, Domains D, int size, boolean relaxation){
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
                                y = SNode.create();
                                y.setState(state.createState(value, i, result.size()));
                                bindings.put(hash, y);
                                nextNodesConstraint.add(y);
                                node_constraint++;
                            } else if(relaxation) y.getState().merge(state, value, i, result.size());
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

    public static MDD subset(MDD result, ArrayOfInt letters, SetOf<Integer> D) {
        D.add(-1);
        SNode snode = SNode.create();
        ParametersSubset parameters = ParametersSubset.create(letters, D.size(), letters.length);
        snode.setState(StateSubset.create(parameters, 0));

        Domains domains = Domains.create();
        for(int i = 0; i < letters.length; i++) {
            domains.add(i);
            for(int v : D) domains.put(i, v);
        }

        build(result, snode, domains, letters.length);

        Memory.free(parameters);

        result.reduce();
        return result;
    }

    public static MDD sequence(MDD result, Domains D, SetOf<Integer> V, int q, int min, int max, int size){
        SNode snode = SNode.create();
        ParametersAmong parameters = ParametersAmong.create(q, min, max, V);
        snode.setState(StateAmong.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);

        result.reduce();
        return result;
    }
    public static MDD sequence(MDD result, int q, int min, int max, int size){
        Domains B = Domains.create();
        for(int i = 0; i < size; i++) {
            B.add(i);
            B.put(i, 0); B.put(i, 1);
        }
        SetOf<Integer> One = Memory.SetOfInteger(); One.add(1);
        SNode snode = SNode.create();
        ParametersAmong parameters = ParametersAmong.create(q, min, max, One);
        snode.setState(StateAmong.create(parameters));

        build(result, snode, B, size);

        Memory.free(parameters);
        Memory.free(B);
        Memory.free(One);

        result.reduce();

        return result;
    }

    public static MDD sum(MDD result, Domains D, int min, int max, int size){
        SNode snode = SNode.create();
        ArrayOfInt minValues = ArrayOfInt.create(size);
        ArrayOfInt maxValues = ArrayOfInt.create(size);

        for(int i = size - 2; i >= 0; i--){
            int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            for(int v : D.get(i+1)) {
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
        ParametersSum parameters = ParametersSum.create(min, max, minValues, maxValues);
        snode.setState(StateSum.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }
    public static MDD sum(MDD result, SetOf<Integer> V, int min, int max, int size){
        Domains D = Domains.create();
        for(int i = 0; i < size; i++) {
            D.add(i);
            D.get(i).add(V);
        }
        sum(result, D, min, max, size);
        Memory.free(D);
        return result;
    }

    public static MDD gcc(MDD result, Domains D, MapOf<Integer, TupleOfInt> maxValues, int size){
        SNode constraint = SNode.create();
        ParametersGCC parameters = ParametersGCC.create(maxValues);
        StateGCC state = StateGCC.create(parameters);
        state.initV();
        constraint.setState(state);

        build(result, constraint, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static MDD allDiff(MDD result, Domains D, SetOf<Integer> V, int size){
        SNode constraint = SNode.create();
        ParametersAllDiff parameters = ParametersAllDiff.create(V);
        constraint.setState(StateAllDiff.create(parameters));

        build(result, constraint, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }
    public static MDD allDiff(MDD result, SetOf<Integer> V, int size){
        Domains D = Domains.create();
        for(int i = 0; i < size; i++) {
            D.add(i);
            D.get(i).add(V);
        }
        allDiff(result, D, V, size);
        Memory.free(D);
        return result;
    }
}
