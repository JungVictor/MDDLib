package dd.operations;

import builder.MDDBuilder;
import builder.constraints.parameters.*;
import builder.constraints.states.*;
import dd.DecisionDiagram;
import dd.interfaces.INode;
import dd.interfaces.IStateNode;
import dd.mdd.MDD;
import dd.mdd.components.StateNode;
import memory.Memory;
import structures.Binder;
import structures.Domains;
import structures.arrays.ArrayOfDouble;
import structures.arrays.ArrayOfLong;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOfNode;
import structures.tuples.TupleOfInt;
import utils.Logger;
import utils.SmallMath;
import utils.io.reader.DDReaderTopDown;
import utils.io.reader.MDDFileWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * <b>The class dedicated to perform on-the-fly constraint operations</b>
 */
public class ConstraintOperation {

    /**
     * Perform the intersection operation between mdd and an alldiff constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param V The set of constrained values
     * @param variables The set of constrained variables
     * @return the MDD resulting from the intersection between mdd and the alldiff constraint
     */
    static public DecisionDiagram allDiff(DecisionDiagram result, DecisionDiagram mdd, SetOf<Integer> V, SetOf<Integer> variables){
        StateNode constraint = StateNode.create();
        ParametersAllDiff parameters = ParametersAllDiff.create(V, variables);
        constraint.setState(StateAllDiff.create(parameters));

        result.getRoot().associate(mdd.getRoot(), constraint);

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and an alldifferent constraint with memory
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param memory The maximum memory capacity
     * @param V The set of constrained values
     * @param variables The set of constrained variables
     * @return the MDD resulting from the intersection between mdd and the alldiff constraint
     */
    static public DecisionDiagram allDiff(DecisionDiagram result, DecisionDiagram mdd, int memory, SetOf<Integer> V, SetOf<Integer> variables){


        StateNode constraint = StateNode.create();
        ParametersAllDiffMem parameters = ParametersAllDiffMem.create(memory, V, variables);
        constraint.setState(StateAllDiffMem.create(parameters));

        result.getRoot().associate(mdd.getRoot(), constraint);

        intersection(result, mdd, constraint);
        result.reduce();
/*
        DecisionDiagram tmp_res = null;
        DecisionDiagram tmp_mdd = mdd;
        int max_iter = mdd.size() - memory;
        for(int i = 0; i < max_iter + 1; i++) {
            variables.clear();
            for(int v = 0; v < memory; v++) variables.add(v+i);
            System.out.println(variables);
            StateNode constraint = StateNode.create();
            ParametersAllDiff parameters = ParametersAllDiff.create(V, variables);
            constraint.setState(StateAllDiff.create(parameters));

            if(i == max_iter) {
                result.getRoot().associate(tmp_mdd.getRoot(), constraint);
                intersection(result, tmp_mdd, constraint);
                result.reduce();
                Memory.free(tmp_mdd);
            } else {
                tmp_res = result.DD();
                tmp_res.getRoot().associate(tmp_mdd.getRoot(), constraint);
                intersection(tmp_res, tmp_mdd, constraint);
                if(tmp_mdd != mdd) Memory.free(tmp_mdd);
                tmp_mdd = tmp_res;
                tmp_res.reduce();
            }

            Memory.free(parameters);
            Memory.free(constraint);
        }*/

        return result;
    }

    /**
     * Perform the intersection operation between mdd and a sum constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param variables The set of all constrained variables
     * @return the MDD resulting from the intersection between mdd and the sum constraint
     */
    static public DecisionDiagram sum(DecisionDiagram result, DecisionDiagram mdd, int min, int max, SetOf<Integer> variables){

        ArrayOfInt minValues = ArrayOfInt.create(mdd.size()-1);
        ArrayOfInt maxValues = ArrayOfInt.create(mdd.size()-1);

        for(int i = mdd.size() - 3; i >= 0; i--){
            int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            for(int v : mdd.iterateOnDomain(i+1)) {
                if(v < vMin) vMin = v;
                if(v > vMax) vMax = v;
            }
            if(i < mdd.size() - 2) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        StateNode constraint = StateNode.create();
        ParametersSum parameters = ParametersSum.create(min, max, minValues, maxValues, variables);
        constraint.setState(StateSum.create(parameters));

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a sum constraint.
     * The map makes the link between a label and its sum value.
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param min The minimum value of the sum
     * @param max The maximum value of the sum
     * @param map The map associating label → value
     * @param variables The set of all constrained variables
     * @return the MDD resulting from the intersection between mdd and the sum constraint
     */
    static public DecisionDiagram sum(DecisionDiagram result, DecisionDiagram mdd, int min, int max, MapOf<Integer, Integer> map, SetOf<Integer> variables){

        ArrayOfInt minValues = ArrayOfInt.create(mdd.size()-1);
        ArrayOfInt maxValues = ArrayOfInt.create(mdd.size()-1);

        for(int i = mdd.size() - 3; i >= 0; i--){
            int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            for(int v : mdd.iterateOnDomain(i+1)) {
                v = map.get(v);
                if(v < vMin) vMin = v;
                if(v > vMax) vMax = v;
            }
            if(i < mdd.size() - 2) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        StateNode constraint = StateNode.create();
        ParametersMapSum parameters = ParametersMapSum.create(min, max, minValues, maxValues, map, variables);
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
     * @param maxValues The GCC values
     * @param variables The set of all constrained variables
     * @return the MDD resulting from the intersection between mdd and the gcc constraint
     */
    static public DecisionDiagram gcc(DecisionDiagram result, DecisionDiagram mdd, MapOf<Integer, TupleOfInt> maxValues, SetOf<Integer> variables){
        StateNode constraint = StateNode.create();
        ParametersGCC parameters = ParametersGCC.create(maxValues, variables);
        StateGCC state = StateGCC.create(parameters);
        state.initV();
        constraint.setState(state);

        intersection(result, mdd, constraint);

        Memory.free(constraint);
        Memory.free(parameters);
        result.reduce();
        return result;
    }
    static public DecisionDiagram gcc(DecisionDiagram result, DecisionDiagram mdd, MapOf<Integer, TupleOfInt> maxValues){
        return gcc(result, mdd, maxValues, null);
    }

    /**
     * Perform the intersection operation between mdd and a sequence constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param q The size of the sequence
     * @param min The minimum number of appearances of a value in the sequence
     * @param max The maximum number of appearances of a value in the sequence
     * @param V The set of constrained values
     * @param variables The set of all constrained variables
     * @return the MDD resulting from the intersection between mdd and the sequence constraint
     */
    static public DecisionDiagram sequence(DecisionDiagram result, DecisionDiagram mdd, int q, int min, int max, SetOf<Integer> V, SetOf<Integer> variables){
        StateNode constraint = StateNode.create();
        ParametersAmong parameters = ParametersAmong.create(q, min, max, V, variables);
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
    static protected void intersection(DecisionDiagram result, DecisionDiagram mdd, StateNode constraint){
        try {
            intersectionWrite(result, mdd, constraint, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform the intersection operation between the given mdd and the given constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @param constraint The PNode containing the constraint (= root node of the constraint)
     * @param relaxation True if you perform a relaxation on the constraint, false otherwise
     */
    public static void intersection(DecisionDiagram result, DecisionDiagram mdd, IStateNode constraint, boolean relaxation){
        result.setSize(mdd.size());
        result.getRoot().associate(mdd.getRoot(), constraint);

        Binder binder = Binder.create();
        HashMap<String, IStateNode> bindings = new HashMap<>();
        SetOfNode<IStateNode> currentNodesConstraint = Memory.SetOfStateNode(),
                nextNodesConstraint = Memory.SetOfStateNode(),
                tmp;

        int node_constraint = 0;

        for(int i = 1; i < mdd.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(INode node : result.iterateOnLayer(i-1)){
                IStateNode x2 = (IStateNode) node.getX2();
                INode x1 = node.getX1();
                for(int value : x1.iterateOnChildLabels()) {
                    NodeState state = x2.getState();
                    if(state.isValid(value, i, mdd.size())) {
                        if(!x2.containsLabel(value)) {
                            String hash = state.signature(value, i, mdd.size());
                            IStateNode y2 = bindings.get(hash);
                            if (y2 == null) {
                                y2 = x2.Node();
                                node_constraint++;
                                y2.setState(state.createState(value, i, mdd.size()));
                                bindings.put(hash, y2);
                                nextNodesConstraint.add(y2);
                            } else if(relaxation) y2.getState().merge(state, value, i, result.size());
                            x2.addChild(value, y2);
                        }
                        Operation.addArcAndNode(result, node, x1.getChild(value), x2.getChild(value), value, i, binder);
                    }
                }
            }
            for(INode node : currentNodesConstraint) Memory.free(node);
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

        Logger.out.information("\rNodes constructed : " + node_constraint + "\n");
    }

    public static void intersectionWrite(DecisionDiagram result, DecisionDiagram mdd, IStateNode constraint, boolean relaxation) throws IOException {
        MDDFileWriter file = new MDDFileWriter(new FileOutputStream("tmp_dd_intersection.dd"), 4096);
        DDReaderTopDown reader = new DDReaderTopDown();

        result.setSize(mdd.size());
        result.getRoot().associate(mdd.getRoot(), constraint);

        Binder binder = Binder.create();
        HashMap<String, IStateNode> bindings = new HashMap<>();
        SetOfNode<IStateNode> currentNodesConstraint = Memory.SetOfStateNode(),
                nextNodesConstraint = Memory.SetOfStateNode(),
                tmp;

        int node_constraint = 0;

        for(int i = 1; i < mdd.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(INode node : result.iterateOnLayer(i-1)){
                IStateNode x2 = (IStateNode) node.getX2();
                INode x1 = node.getX1();
                for(int value : x1.iterateOnChildLabels()) {
                    NodeState state = x2.getState();
                    if(state.isValid(value, i, mdd.size())) {
                        if(!x2.containsLabel(value)) {
                            String hash = state.signature(value, i, mdd.size());
                            IStateNode y2 = bindings.get(hash);
                            if (y2 == null) {
                                y2 = x2.Node();
                                node_constraint++;
                                y2.setState(state.createState(value, i, mdd.size()));
                                bindings.put(hash, y2);
                                nextNodesConstraint.add(y2);
                            } else if(relaxation) y2.getState().merge(state, value, i, result.size());
                            x2.addChild(value, y2);
                        }
                        Operation.addArcAndNode(result, node, x1.getChild(value), x2.getChild(value), value, i, binder);
                    }
                }
            }
            // Save the layer and free the nodes
            reader.saveAndFree(result, file, i - 1);
            for(INode node : result.iterateOnLayer(i-1)) Memory.free(node);
            // ---------
            for(INode node : currentNodesConstraint) Memory.free(node);
            currentNodesConstraint.clear();
            tmp = currentNodesConstraint;
            currentNodesConstraint = nextNodesConstraint;
            nextNodesConstraint = tmp;
            binder.clear();
            bindings.clear();
        }
        file.close();
        Memory.free(currentNodesConstraint);
        Memory.free(nextNodesConstraint);
        Memory.free(binder);

        Logger.out.information("\rNodes constructed : " + node_constraint + "\n");
    }

    public static void stateIntersection(DecisionDiagram result, DecisionDiagram dd, IStateNode constraint, boolean relaxation){
        result.setSize(dd.size());
        result.setRoot(constraint);
        result.getRoot().setX1(dd.getRoot());

        HashMap<String, IStateNode> bindings = new HashMap<>();

        int node_constraint = 0;

        for(int i = 1; i < dd.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(INode node : result.iterateOnLayer(i-1)){
                IStateNode stateNode = (IStateNode) node;
                INode x1 = node.getX1();
                for(int value : x1.iterateOnChildLabels()) {
                    NodeState state = stateNode.getState();
                    if(state.isValid(value, i, dd.size())) {
                        IStateNode child = (IStateNode) node.getChild(value);
                        if(child == null) {
                            String hash = state.signature(value, i, dd.size());
                            child = bindings.get(hash);
                            if (child == null) {
                                child = stateNode.Node();
                                node_constraint++;
                                child.setState(state.createState(value, i, dd.size()));
                                child.setX1(x1.getChild(value));
                                bindings.put(hash, child);
                                result.addNode(child, i);
                            } else if(relaxation) child.getState().merge(state, value, i, result.size());
                        }
                        result.addArc(node, value, child, i-1);
                    }
                }
            }
            bindings.clear();
        }
        Logger.out.information("\rNodes constructed : " + node_constraint + "\n");
    }

    //*************************//
    //      CONFIDENCE         //
    //*************************//

    public static MDD confidenceMulRelaxed(MDD result, MDD mdd, int gamma, int precision, int epsilon, int n, Domains D) {
        double maxProbaDomains = Math.pow(10, precision);
        double maxProbaEpsilon = Math.pow(10, epsilon);

        if (mdd == null)
            return MDDBuilder.mulRelaxed(result, gamma, maxProbaDomains, maxProbaDomains, maxProbaEpsilon, n, D);
        return mulRelaxed(result, mdd, D, gamma, maxProbaDomains, maxProbaDomains, maxProbaEpsilon, n);
    }

    public static strictfp MDD confidenceULP(MDD result, MDD mdd, int gamma, int precision, int epsilon, int n, Domains D) {
        MapOf<Integer, Double> mapLog = Memory.MapOfIntegerDouble();
        for (int i = 0; i < n; i++) {
            for (int v : D.get(i)) {
                mapLog.put(v, -1 * Math.nextUp(Math.log10(Math.nextUp(v * Math.pow(10, -precision)))));

            }
        }
        double s_max = -1 * Math.nextDown(Math.log10(Math.nextDown(gamma * Math.pow(10, -precision))));

        if (mdd == null) return MDDBuilder.sumDoubleULP(result, 0, s_max, mapLog, epsilon, n, D);
        return sumDoubleULP(result, mdd, D, 0, s_max, mapLog, epsilon, n);
    }

    public static strictfp MDD confidence(MDD result, MDD mdd, double gamma, int precision, int epsilon, int n, Domains D) {
        MapOf<Integer, Double> mapLog = Memory.MapOfIntegerDouble();
        for (int i = 0; i < n; i++) {
            for (int v : D.get(i)) {
                mapLog.put(v, -1 * SmallMath.log(v * Math.pow(10, -precision), 10, 15, true));

            }
        }
        double s_max = -1 * SmallMath.log(gamma, 10, 15, false);

        if (mdd == null) return MDDBuilder.sumDouble(result, 0, s_max, mapLog, epsilon, n, D);
        return sumDouble(result, mdd, D, 0, s_max, mapLog, epsilon, n);
    }

    public static MDD confidence(MDD result, MDD mdd, int gamma, int precision, int epsilon, int n, int logPrecision, Domains D) {
        MapOf<Integer, Long> map = Memory.MapOfIntegerLong();
        for (int i = 0; i < n; i++) {
            for (int v : D.get(i)) {
                map.put(v, -1 * SmallMath.log(v, precision, 10, logPrecision, true));
            }
        }
        long s_max = -1 * SmallMath.log(gamma, precision, 10, logPrecision, false);

        if (mdd == null) return MDDBuilder.sumRelaxed(result, 0, s_max, map, epsilon, logPrecision, n, D);
        return sumRelaxed(result, mdd, D, 0, s_max, map, epsilon, logPrecision, n);
    }

    // ****** //

    public static strictfp MDD mulRelaxed(MDD result, MDD mdd, Domains D, double min, double max, double maxProbaDomains, double maxProbaEpsilon, int size) {
        // CHECK MyConstraintBuilder mulRelaxed IF MAKING CHANGE TO THIS FUNCTION !
        StateNode snode = StateNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à maxProbaEpsilon
        minValues.set(size - 1, maxProbaEpsilon);
        maxValues.set(size - 1, maxProbaEpsilon);

        for (int i = size - 2; i >= 0; i--) {
            //On initialise pour ne pas avoir de souci avec les conditions dans la boucle
            double vMin = 0;
            double vMax = 0;
            boolean firstIteration = true;
            for (int v : D.get(i + 1)) {

                if (firstIteration) {
                    vMin = v;
                    vMax = v;
                    firstIteration = false;
                } else {
                    if (v < vMin) vMin = v;
                    if (v > vMax) vMax = v;
                }

            }

            if (i < size - 1) {
                vMin = SmallMath.multiplyFloor(vMin, minValues.get(i + 1), maxProbaDomains);
                vMax = SmallMath.multiplyCeil(vMax, maxValues.get(i + 1), maxProbaDomains);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        min = SmallMath.multiplyFloor(min, maxProbaEpsilon, maxProbaDomains);
        max = SmallMath.multiplyCeil(max, maxProbaEpsilon, maxProbaDomains);
        ParametersProductRelaxed parameters = ParametersProductRelaxed.create(min, max, minValues, maxValues, maxProbaDomains, maxProbaEpsilon, null);
        snode.setState(StateProductRelaxed.create(parameters));

        intersection(result, mdd, snode, false);

        Memory.free(snode);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static strictfp MDD sumDouble(MDD result, MDD mdd, Domains D, double min, double max, MapOf<Integer, Double> mapDouble, int epsilon, int size) {
        // CHECK MyConstraintBuilder sumDouble IF MAKING CHANGE TO THIS FUNCTION !
        StateNode snode = StateNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à 0
        minValues.set(size - 1, 0.0);
        maxValues.set(size - 1, 0.0);

        for (int i = size - 2; i >= 0; i--) {
            double vMin = Double.MAX_VALUE, vMax = -Double.MAX_VALUE;
            for (int v : D.get(i + 1)) {
                double doubleV = mapDouble.get(v);
                if (doubleV < vMin) vMin = doubleV;
                if (doubleV > vMax) vMax = doubleV;
            }
            if (i < size - 1) {
                vMin += minValues.get(i + 1);
                vMax += maxValues.get(i + 1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSumDouble parameters = ParametersSumDouble.create(min, max, minValues, maxValues, mapDouble, epsilon, null);
        snode.setState(StateSumDouble.create(parameters));

        intersection(result, mdd, snode, true);

        Memory.free(snode);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static strictfp MDD sumDoubleULP(MDD result, MDD mdd, Domains D, double min, double max, MapOf<Integer, Double> mapDouble, int epsilon, int size) {
        // CHECK MyConstraintBuilder sumDouble IF MAKING CHANGE TO THIS FUNCTION !
        StateNode snode = StateNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à 0
        minValues.set(size - 1, 0.0);
        maxValues.set(size - 1, 0.0);

        for (int i = size - 2; i >= 0; i--) {
            double vMin = Double.MAX_VALUE, vMax = -Double.MAX_VALUE;
            for (int v : D.get(i + 1)) {
                double doubleV = mapDouble.get(v);
                if (doubleV < vMin) vMin = doubleV;
                if (doubleV > vMax) vMax = doubleV;
            }
            if (i < size - 1) {
                vMin = Math.nextDown(vMin + minValues.get(i+1));
                vMax = Math.nextUp(vMax + maxValues.get(i+1));
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSumDouble parameters = ParametersSumDouble.create(min, max, minValues, maxValues, mapDouble, epsilon, null);
        snode.setState(StateSumDoubleULP.create(parameters));

        intersection(result, mdd, snode, true);

        Memory.free(snode);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static MDD sumRelaxed(MDD result, MDD mdd, Domains D, long min, long max, MapOf<Integer, Long> map, int epsilon, int precision, int size) {
        // CHECK MyConstraintBuilder sumDouble IF MAKING CHANGE TO THIS FUNCTION !
        StateNode snode = StateNode.create();
        ArrayOfLong minValues = ArrayOfLong.create(size);
        ArrayOfLong maxValues = ArrayOfLong.create(size);

        //Important d'initialiser la dernière valeur du tableau à 0
        minValues.set(size - 1, 0);
        maxValues.set(size - 1, 0);

        for (int i = size - 2; i >= 0; i--) {
            long vMin = Long.MAX_VALUE, vMax = Long.MIN_VALUE;
            for (int v : D.get(i + 1)) {
                long value = map.get(v);
                if (value < vMin) vMin = value;
                if (value > vMax) vMax = value;
            }
            if (i < size - 1) {
                vMin += minValues.get(i + 1);
                vMax += maxValues.get(i + 1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSumRelaxed parameters = ParametersSumRelaxed.create(min, max, minValues, maxValues, map, epsilon, precision, null);
        snode.setState(StateSumRelaxed.create(parameters));

        intersection(result, mdd, snode, true);

        Memory.free(snode);
        Memory.free(parameters);
        result.reduce();
        return result;
    }


}
