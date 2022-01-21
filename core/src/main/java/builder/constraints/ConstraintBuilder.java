package builder.constraints;

import builder.constraints.parameters.*;
import builder.constraints.states.*;
import builder.rules.SuccessionRule;
import builder.rules.SuccessionRuleDefault;
import dd.AbstractNode;
import dd.DecisionDiagram;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import dd.mdd.components.SNode;
import memory.Memory;
import structures.Domains;
import structures.arrays.ArrayOfBigInteger;
import structures.arrays.ArrayOfDouble;
import structures.arrays.ArrayOfLong;
import structures.generics.CollectionOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOfNode;
import structures.tuples.TupleOfInt;
import utils.Logger;
import utils.SmallMath;

import java.math.BigInteger;
import java.util.HashMap;

public class ConstraintBuilder {

    /**
     * Build the constraint corresponding to the one held by the given SNode.
     * The succession is defined by the domains D (successors of one node at layer i = D[i]).
     * @param result The DecisionDiagram holding the result
     * @param constraint The given constraint
     * @param D The rule of succession
     * @param size The size of the DD
     * @return The DD corresponding to the given constraint
     */
    public static DecisionDiagram build(DecisionDiagram result, SNode constraint, Domains D, int size){
        return build(result, constraint, D, size, false);
    }

    /**
     * Build the constraint corresponding to the one held by the given SNode.
     * The succession is defined by the domains D (successors of one node at layer i = D[i]).
     * @param result The DecisionDiagram holding the result
     * @param constraint The given constraint
     * @param D The domains of the variables
     * @param size The size of the DD
     * @param relaxation True if the constraint must be relaxed when building, false otherwise
     * @return The DD corresponding to the given constraint
     */
    public static DecisionDiagram build(DecisionDiagram result, SNode constraint, Domains D, int size, boolean relaxation){
        SuccessionRuleDefault rule = SuccessionRuleDefault.create(D);
        build(result, constraint, rule, size, relaxation);
        Memory.free(rule);
        return result;
    }

    /**
     * Build the constraint corresponding to the one held by the given SNode.
     * @param result The DecisionDiagram holding the result
     * @param constraint The given constraint
     * @param rule The rule of succession
     * @param size The size of the DD
     * @param relaxation True if the constraint must be relaxed when building, false otherwise
     * @return The DD corresponding to the given constraint
     */
    public static DecisionDiagram build(DecisionDiagram result, SNode constraint, SuccessionRule rule, int size, boolean relaxation){
        result.setSize(size+1);
        result.setRoot(constraint);

        CollectionOf<Integer> successors = rule.getCollection();
        HashMap<String, SNode> bindings = new HashMap<>();
        SetOfNode<Node> currentNodesConstraint = Memory.SetOfNode(),
                nextNodesConstraint = Memory.SetOfNode(),
                tmp;
        int node_constraint = 0;

        for(int i = 1; i < result.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(AbstractNode node : result.iterateOnLayer(i-1)){
                SNode x = (SNode) node;
                for(int value : rule.successors(successors,i - 1, x)) {
                    NodeState state = x.getState();
                    if(state.isValid(value, i, result.size())) {
                        if(!x.containsLabel(value)) {
                            String hash = state.signature(value, i, result.size());
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
        Memory.free(successors);
        Memory.free(currentNodesConstraint);
        Memory.free(nextNodesConstraint);

        Logger.out.information(node_constraint);
        return result;
    }

    /**
     * Build the DD corresponding to the subset constraint.
     * @param result The DecisionDiagram holding the result
     * @param letters The letters of the constraint
     * @param D The domains of the variables
     * @param variables The variables of the constraint
     * @return The DD corresponding to the subset constraint
     */
    public static DecisionDiagram subset(DecisionDiagram result, ArrayOfInt letters, SetOf<Integer> D, SetOf<Integer> variables) {
        D.add(-1);
        SNode snode = SNode.create();
        ParametersSubset parameters = ParametersSubset.create(letters, D.size(), letters.length, variables);
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

    /**
     * Build the DD corresponding to the subset constraint.
     * @param result The DecisionDiagram holding the result
     * @param letters The letters of the constraint
     * @param D The domains of the variables
     * @return The DD corresponding to the subset constraint
     */
    public static DecisionDiagram subset(DecisionDiagram result, ArrayOfInt letters, SetOf<Integer> D) {
        return subset(result, letters, D, null);
    }

    /**
     * Build the DD corresponding to the sequence constraint.
     * @param result The DecisionDiagram holding the result
     * @param D Domains of the variables
     * @param V The set of constrained values
     * @param q Window of the sequence
     * @param min Minimum number of occurrences of a value in V
     * @param max Maximum number of occurrences of a value in V
     * @param size The size of the DD
     * @param variables The set of constrained variables
     * @return The DecisionDiagram corresponding to the sequence constraint
     */
    public static DecisionDiagram sequence(DecisionDiagram result, Domains D, SetOf<Integer> V, int q, int min, int max, int size, SetOf<Integer> variables){
        SNode snode = SNode.create();
        ParametersAmong parameters = ParametersAmong.create(q, min, max, V, variables);
        snode.setState(StateAmong.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);

        result.reduce();
        return result;
    }

    /**
     * Build the DD corresponding to the sum constraint.
     * @param result The DecisionDiagram holding the result
     * @param D The domains of the variables
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param size The size of the DD
     * @param variables The set of constrained variables
     * @return The DecisionDiagram corresponding to the sum constraint
     */
    public static DecisionDiagram sum(DecisionDiagram result, Domains D, int min, int max, int size, SetOf<Integer> variables){
        SNode snode = SNode.create();
        ArrayOfInt minValues = ArrayOfInt.create(size);
        ArrayOfInt maxValues = ArrayOfInt.create(size);

        for(int i = size - 2; i >= 0; i--){
            int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            if(variables == null || variables.contains(i)) {
                for (int v : D.get(i + 1)) {
                    if (v < vMin) vMin = v;
                    if (v > vMax) vMax = v;
                }
            } else {
                vMin = 0; vMax = 0;
            }
            if(i < size - 1) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSum parameters = ParametersSum.create(min, max, minValues, maxValues, variables);
        snode.setState(StateSum.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Build the DD corresponding to the GCC constraint
     * @param result The DecisionDiagram holding the result
     * @param D The domains of the variables
     * @param maxValues The values of the GCC
     * @param violations The maximum number of violations in the GCC
     * @param size The size of the DD
     * @param variables The set of constrained variables
     * @return The DecisionDiagram corresponding to the GCC constraint
     */
    public static DecisionDiagram gcc(DecisionDiagram result, Domains D, MapOf<Integer, TupleOfInt> maxValues, int violations, int size, SetOf<Integer> variables){
        SNode constraint = SNode.create();
        ParametersGCC parameters = ParametersGCC.create(maxValues, violations, variables);
        StateGCC state = StateGCC.create(parameters);
        state.initV();
        constraint.setState(state);

        build(result, constraint, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Build the DD corresponding to the AllDifferent constraint
     * @param result The DD holding the result
     * @param D The domains of the variables
     * @param V The set of constrained values
     * @param size The size of the DD
     * @param variables The set of constrained variables
     * @return The DecisionDiagram corresponding to the AllDifferent constraint
     */
    public static DecisionDiagram allDifferent(DecisionDiagram result, Domains D, SetOf<Integer> V, int size, SetOf<Integer> variables){
        SNode constraint = SNode.create();
        ParametersAllDiff parameters = ParametersAllDiff.create(V, variables);
        constraint.setState(StateAllDiff.create(parameters));

        build(result, constraint, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }


    //*************************//
    //      CONFIDENCE         //
    //*************************//

    // TODO : new min/max values
    public static strictfp MDD mulRelaxed(MDD result, Domains D, double min, double max, double maxProbaDomains, double maxProbaEpsilon, int size, SetOf<Integer> variables){
        // CHECK MyConstraintOperation mulRelaxed IF MAKING CHANGE TO THIS FUNCTION !
        SNode snode = SNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à maxProbaEpsilon
        minValues.set(size-1, maxProbaEpsilon);
        maxValues.set(size-1, maxProbaEpsilon);

        for(int i = size - 2; i >= 0; i--){
            //On initialise pour ne pas avoir de souci avec les conditions dans la boucle
            double vMin = 0;
            double vMax = 0;
            boolean firstIteration = true;
            for(int v : D.get(i+1)) {

                if (firstIteration){
                    vMin = v;
                    vMax = v;
                    firstIteration = false;
                } else {
                    if(v < vMin) vMin = v;
                    if(v > vMax) vMax = v;
                }

            }

            if(i < size - 1) {
                vMin = SmallMath.multiplyFloor(vMin, minValues.get(i+1), maxProbaDomains);
                vMax = SmallMath.multiplyCeil(vMax, maxValues.get(i+1), maxProbaDomains);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        min = SmallMath.multiplyFloor(min, maxProbaEpsilon, maxProbaDomains);
        max = SmallMath.multiplyCeil(max, maxProbaEpsilon, maxProbaDomains);
        ParametersProductRelaxed parameters = ParametersProductRelaxed.create(min, max, minValues, maxValues, maxProbaDomains, maxProbaEpsilon, variables);
        snode.setState(StateProductRelaxed.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static MDD mul(MDD result, Domains D, BigInteger min, BigInteger max, int size, SetOf<Integer> variables){
        SNode snode = SNode.create();
        ArrayOfBigInteger minValues = ArrayOfBigInteger.create(size);
        ArrayOfBigInteger maxValues = ArrayOfBigInteger.create(size);

        //Important d'initialiser la dernière valeur du tableau à 1
        minValues.set(size-1, BigInteger.valueOf(1));
        maxValues.set(size-1, BigInteger.valueOf(1));

        for(int i = size - 2; i >= 0; i--){
            //On initialise pour ne pas avoir de souci avec les conditions dans la boucle
            BigInteger vMin = BigInteger.ZERO;
            BigInteger vMax = BigInteger.ZERO;
            boolean firstIteration = true;
            for(int v : D.get(i+1)) {
                BigInteger bigIntV = BigInteger.valueOf(v);

                if (firstIteration){
                    vMin = bigIntV;
                    vMax = bigIntV;
                    firstIteration = false;
                } else {
                    if(bigIntV.compareTo(vMin) < 0) vMin = bigIntV;
                    if(bigIntV.compareTo(vMax) > 0) vMax = bigIntV;
                }

            }

            if(i < size - 1) {
                vMin = vMin.multiply(minValues.get(i+1));
                vMax = vMax.multiply(maxValues.get(i+1));
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        ParametersProduct parameters = ParametersProduct.create(min, max, minValues, maxValues, variables);
        snode.setState(StateProduct.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static strictfp MDD sumDouble(MDD result, Domains D, double min, double max, MapOf<Integer, Double> mapDouble, int epsilon, int size, SetOf<Integer> variables){
        // CHECK MyConstraintOperation sumDouble IF MAKING CHANGE TO THIS FUNCTION !
        SNode snode = SNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à 0
        minValues.set(size-1, 0.0);
        maxValues.set(size-1, 0.0);

        for(int i = size - 2; i >= 0; i--){
            double vMin = Double.MAX_VALUE, vMax = -Double.MAX_VALUE;
            for(int v : D.get(i+1)) {
                double doubleV = mapDouble.get(v);
                if(doubleV < vMin) vMin = doubleV;
                if(doubleV > vMax) vMax = doubleV;
            }
            if(i < size - 1) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSumDouble parameters = ParametersSumDouble.create(min, max, minValues, maxValues, mapDouble, epsilon, variables);
        snode.setState(StateSumDouble.create(parameters));

        build(result, snode, D, size, true);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static strictfp MDD sumDoubleULP(MDD result, Domains D, double min, double max, MapOf<Integer, Double> mapDouble, int epsilon, int size, SetOf<Integer> variables){
        // CHECK MyConstraintOperation sumDoubleULP IF MAKING CHANGE TO THIS FUNCTION !
        SNode snode = SNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à 0
        minValues.set(size-1, 0.0);
        maxValues.set(size-1, 0.0);

        for(int i = size - 2; i >= 0; i--){
            double vMin = Double.MAX_VALUE, vMax = -Double.MAX_VALUE;
            for(int v : D.get(i+1)) {
                double doubleV = mapDouble.get(v);
                if(doubleV < vMin) vMin = doubleV;
                if(doubleV > vMax) vMax = doubleV;
            }
            if(i < size - 1) {
                vMin = Math.nextDown(vMin + minValues.get(i+1));
                vMax = Math.nextUp(vMax + maxValues.get(i+1));
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSumDouble parameters = ParametersSumDouble.create(min, max, minValues, maxValues, mapDouble, epsilon, variables);
        snode.setState(StateSumDoubleULP.create(parameters));

        build(result, snode, D, size, true);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static strictfp MDD sumRelaxed(MDD result, Domains D, long min, long max, MapOf<Integer, Long> map, int epsilon, int precision, int size, SetOf<Integer> variables){
        // CHECK MyConstraintOperation sumDouble IF MAKING CHANGE TO THIS FUNCTION !
        SNode snode = SNode.create();
        ArrayOfLong minValues = ArrayOfLong.create(size);
        ArrayOfLong maxValues = ArrayOfLong.create(size);

        //Important d'initialiser la dernière valeur du tableau à 0
        minValues.set(size-1, 0);
        maxValues.set(size-1, 0);

        for(int i = size - 2; i >= 0; i--){
            long vMin = Long.MAX_VALUE, vMax = Long.MIN_VALUE;
            for(int v : D.get(i+1)) {
                long value = map.get(v);
                if(value < vMin) vMin = value;
                if(value > vMax) vMax = value;
            }
            if(i < size - 1) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSumRelaxed parameters = ParametersSumRelaxed.create(min, max, minValues, maxValues, map, epsilon, precision, variables);
        snode.setState(StateSumRelaxed.create(parameters));

        build(result, snode, D, size, true);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

}
