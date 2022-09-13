package ui;

import builder.constraints.ArithmeticModel;
import builder.constraints.parameters.*;
import builder.constraints.states.*;
import dd.DecisionDiagram;
import dd.bdd.BDD;
import dd.bdd.components.BinaryStateNode;
import dd.interfaces.IStateNode;
import dd.mdd.MDD;
import dd.mdd.components.StateNode;
import memory.Memory;
import structures.arrays.ArrayOfInt;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.lists.ListOfInt;
import structures.tuples.TupleOfInt;

public class Constraint {

    private MDD mdd = null;
    private boolean toBuild = false;
    private IStateNode node = null;
    private Variable[] scope = null;

    public Constraint(){}

    public static Constraint sum(Model model, int min, int max, Variable... scope) {
        Constraint constraint = new Constraint();

        ArrayOfInt minValues = ArrayOfInt.create(model.numberOfVariables());
        ArrayOfInt maxValues = ArrayOfInt.create(model.numberOfVariables());

        for(int i = model.numberOfVariables() - 2; i >= 0; i--){
            int vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            for(int v : model.getDomains().get(i+1)) {
                if(v < vMin) vMin = v;
                if(v > vMax) vMax = v;
            }
            if(i < model.numberOfVariables() - 1) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        StateNode node = StateNode.create();
        ParametersSum parameters = ParametersSum.create(min, max, minValues, maxValues, scopeToSet(scope));
        node.setState(StateSum.create(parameters));

        constraint.setRoot(node);

        return constraint;
    }

    public static Constraint allDifferent(Model model){
        return allDifferent(model, new Variable[0]);
    }

    public static Constraint allDifferent(Model model, Variable... scope){
        SetOf<Integer> V = Memory.SetOfInteger();
        for(int i = 0; i < model.numberOfVariables(); i++) V.add(model.getDomains().get(i));

        StateNode state = StateNode.create();
        ParametersAllDiff parameters = ParametersAllDiff.create(V, scopeToSet(scope));
        state.setState(StateAllDiff.create(parameters));

        Constraint constraint = new Constraint();
        constraint.setRoot(state);
        return constraint;
    }

    public static Constraint allDifferent(Model model, int... constrained){
        return allDifferent(model, constrained, null);
    }

    public static Constraint allDifferent(Model model, int[] constrained, Variable... scope) {
        Constraint constraint = new Constraint();

        SetOf<Integer> V = Memory.SetOfInteger();
        for(int v : constrained) V.add(v);

        StateNode state = StateNode.create();
        ParametersAllDiff parameters = ParametersAllDiff.create(V, scopeToSet(scope));
        state.setState(StateAllDiff.create(parameters));

        constraint.setRoot(state);
        return constraint;
    }

    public static Constraint expression(Model model, String... expressions){
        Constraint constraint = new Constraint();

        ArithmeticModel c = new ArithmeticModel();
        for(String expression : expressions) c.addExpression(expression);

        constraint.setRoot(c.state(model.getDomains(), model.numberOfVariables()));

        return constraint;
    }

    public static Constraint sequence(Model model, int q, int min, int max, int... constrained){
        return sequence(model, q, min, max, constrained, null);
    }

    public static Constraint sequence(Model model, int q, int min, int max, int[] constrained, Variable... scope) {
        Constraint constraint = new Constraint();

        SetOf<Integer> V = Memory.SetOfInteger();
        for(int v : constrained) V.add(v);

        StateNode node = StateNode.create();
        ParametersAmong parameters = ParametersAmong.create(q, min, max, V, scopeToSet(scope));
        node.setState(StateAmong.create(parameters));

        constraint.setRoot(node);
        return constraint;
    }

    public static Constraint custom(NodeState state){
        Constraint constraint = new Constraint();
        StateNode node = StateNode.create();

        node.setState(state);

        constraint.setRoot(node);
        return constraint;
    }

    public static Constraint gcc(Model model, int[][] tuples){
        return gcc(model, tuples, null);
    }

    public static Constraint gcc(Model model, int[][] tuples, Variable... scope) {
        Constraint constraint = new Constraint();
        StateNode node = StateNode.create();

        MapOf<Integer, TupleOfInt> maxValues = Memory.MapOfIntegerTupleOfInt();
        for(int i = 0; i < tuples.length; i++){
            maxValues.put(tuples[i][0], TupleOfInt.create(tuples[i][1], tuples[i][2]));
        }

        ParametersGCC parameters = ParametersGCC.create(maxValues, scopeToSet(scope));
        StateGCC state = StateGCC.create(parameters);
        state.initV();
        node.setState(state);

        constraint.setRoot(node);
        return constraint;
    }

    private static SetOf<Integer> scopeToSet(Variable[] scope){
        if(scope == null) return null;
        if(scope.length == 0) return null;
        SetOf<Integer> variables = Memory.SetOfInteger();
        for(Variable v : scope) variables.add(v.getOrder());
        return variables;
    }

    public boolean isMDD(){
        return mdd != null;
    }

    public MDD getMDD(){
        return mdd;
    }

    public IStateNode getRoot(){
        return node;
    }

    public void setRoot(IStateNode node){
        this.node = node;
    }

    public Variable[] getScope(){
        return scope;
    }

    public boolean isToBuild(){
        return toBuild;
    }

    public void setMDD(MDD mdd){
        this.mdd = mdd;
    }

}
