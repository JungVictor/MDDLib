package builder.constraints;

import builder.constraints.parameters.ParametersExpression;
import builder.constraints.states.StateExpression;
import dd.DecisionDiagram;
import dd.bdd.BDD;
import dd.bdd.components.BinaryStateNode;
import dd.interfaces.IStateNode;
import dd.mdd.components.StateNode;
import memory.Memory;
import structures.Domains;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOf;
import utils.expressions.Expression;
import utils.expressions.ExpressionParser;

import java.util.HashMap;
import java.util.HashSet;

/**
 * <b>ArithmeticModel</b><br>
 * This class is used to build DDs satisfying all expressions added to the model.
 */
public class ArithmeticModel {

    private final HashMap<Integer, HashSet<Expression>> expressions = new HashMap<>();
    private final SetOf<Integer> variables = Memory.SetOfInteger();

    /**
     * Add an expression to the model.
     * @param expression The expression to add (as a String)
     * @return The expression as an object
     */
    public Expression addExpression(String expression){
        Expression expr = new ExpressionParser().parse(expression);
        bindExpressionToVariables(expr);
        return expr;
    }

    /**
     * Build the DD corresponding to the logical AND between all added expressions.
     * @param result The DDs to stock the result
     * @param D The domains of the variables
     * @param size The size of the DD
     * @return The DD corresponding to the logical AND between all added expressions.
     */
    public DecisionDiagram build(DecisionDiagram result, Domains D, int size){
        // Min and max values initialisation
        ArrayOfInt min = ArrayOfInt.create(size);
        ArrayOfInt max = ArrayOfInt.create(size);
        for(int i = 0; i < size; i++) {
            int vMin = Integer.MAX_VALUE;
            int vMax = Integer.MIN_VALUE;
            for(int v : D.get(i)) {
                if(v < vMin) vMin = v;
                if(v > vMax) vMax = v;
            }
            min.set(i, vMin);
            max.set(i, vMax);
        }

        ParametersExpression parameters = ParametersExpression.create(variables, expressions, min, max);
        StateExpression expression = StateExpression.create(parameters);

        IStateNode constraint;
        if(result instanceof BDD) constraint = BinaryStateNode.create();
        else constraint = StateNode.create();
        constraint.setState(expression);

        ConstraintBuilder.build(result, constraint, D, size);

        result.reduce();

        Memory.free(min);
        Memory.free(max);
        Memory.free(parameters);
        Memory.free(expression);

        expressions.clear();
        variables.clear();

        return null;
    }

    /**
     * Perform the bind between the expression and the variables in the expression.
     * That way, you know all expressions containing a certain variable.
     * @param expression The expression
     */
    private void bindExpressionToVariables(Expression expression){
        for(int layer : expression.getBinding().values()) {
            variables.add(layer);
            if(!expressions.containsKey(layer)) expressions.put(layer, new HashSet<>());
            expressions.get(layer).add(expression);
        }
    }

}
