package builder.constraints;

import builder.constraints.parameters.ParametersExpression;
import builder.constraints.states.StateExpression;
import dd.DecisionDiagram;
import dd.mdd.MDD;
import dd.mdd.components.SNode;
import memory.Memory;
import structures.Domains;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOf;
import utils.expressions.Expression;
import utils.expressions.ExpressionParser;

import java.util.HashMap;
import java.util.HashSet;

public class ExpressionModel {

    private final HashMap<Integer, HashSet<Expression>> expressions = new HashMap<>();
    private final SetOf<Integer> variables = Memory.SetOfInteger();

    public Expression addExpression(String expression){
        Expression expr = new ExpressionParser().parse(expression);
        bindExpressionToVariables(expr);
        return expr;
    }

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

        SNode constraint = SNode.create();
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

    private void bindExpressionToVariables(Expression expression){
        for(int layer : expression.getBinding().values()) {
            variables.add(layer);
            if(!expressions.containsKey(layer)) expressions.put(layer, new HashSet<>());
            expressions.get(layer).add(expression);
        }
    }

}
