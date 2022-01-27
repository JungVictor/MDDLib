package utils.expressions;

import java.util.HashMap;
import static utils.expressions.Expression.*;

/**
 * <b>ExpressionParser</b><br>
 * Create an Expression object from a String representing the expression.
 */
public class ExpressionParser {

    // Used to parse the beginning and the end of an absolute value
    private char OPEN_ABS = '&', CLOSE_ABS = ';';

    // Binding between variables' name and their index.
    private HashMap<String, Integer> binders;

    private String variable_name = "priority_var_";
    private int variable_count;
    private HashMap<String, Expression> parenthesisExpressions;

    private int firstVariable = Integer.MAX_VALUE, lastVariable = -1;

    // Last operator parsed
    private String lastOperator;
    // Comparison operator (unique)
    private String comp;

    public ExpressionParser(){}

    /**
     * Bind a variable's name to the index, if it's not already binded.
     * @param name Variable's name
     */
    private void bind(String name){
        if(this.binders.containsKey(name)) return;
        int variable = Integer.parseInt(name.substring(1, name.length() - 1));
        this.binders.put(name, variable);
        if(variable < firstVariable) firstVariable = variable;
        if(variable > lastVariable) lastVariable = variable;
    }

    /**
     * Split the expression in two part according to the comparison operator.
     * @param expr Mathematical expression
     * @return Two sub-expression (left and right)
     */
    private String[] splitCompare(String expr){
        if(expr.contains(LEQ)) {
            comp = LEQ;
            return expr.split("\\"+LEQ);
        }
        if(expr.contains(GEQ)) {
            comp = GEQ;
            return expr.split("\\"+GEQ);
        }
        if(expr.contains(LT)) {
            comp = LT;
            return expr.split("\\"+LT);
        }
        if(expr.contains(GT)) {
            comp = GT;
            return expr.split("\\"+GT);
        }
        if(expr.contains(NEQ)){
            comp = NEQ;
            return expr.split("\\"+NEQ);
        }
        comp = EQ;
        return expr.split("\\"+EQ);
    }

    /**
     * Parse all priorities operators
     * @param expression The expression as a String
     * @return The same expression with priorities parsed
     */
    private String priority_parse(String expression){
        int i = 0;
        expression = absolute_parse(expression);
        while(i < expression.length()) {
            if (expression.charAt(i) == OPEN_ABS){
                expression = absolute_replace(expression);
                i = 0;
            } else if (expression.charAt(i) == OPEN_PAR){
                expression = parenthesis_replace(expression);
                i = 0;
            }
            else i++;
        }
        return expression;
    }

    /**
     * Parse the absolute from the expression
     * @param expression The expression as a String
     * @return The same expression with absolute parsed
     */
    private String absolute_parse(String expression){
        StringBuilder builder = new StringBuilder(expression);
        int first_index = builder.indexOf(ABS);
        int last_index = builder.lastIndexOf(ABS);

        while(last_index != first_index && last_index > 0 && first_index >= 0){
            builder.setCharAt(last_index, CLOSE_ABS);
            builder.setCharAt(first_index, OPEN_ABS);
            last_index = builder.lastIndexOf(ABS);
            first_index = builder.indexOf(OPEN_ABS +"");
        }
        expression = builder.toString();
        return expression;
    }

    /**
     * Replace the absolute once they are treated
     * @param expression The expression as a String
     * @return The same expression with absolute replaced
     */
    private String absolute_replace(String expression){
        // First index of "|" (close)
        int close = expression.indexOf(CLOSE_ABS);
        int open = close;
        while (open > 0){
            open--;
            if(expression.charAt(open) == OPEN_ABS) break;
        }
        String absolute = expression.substring(open+1, close);
        String absolute_replaced = absolute;
        if(absolute.contains(OPEN_PAR+"")) absolute_replaced = parenthesis_replace(absolute_replaced);
        Expression absoluteExpression = new Expression(createExpr(absolute_replaced), ABS);
        String name = this.variable_name+(variable_count++);
        this.parenthesisExpressions.put(name, absoluteExpression);
        return expression.replace(OPEN_ABS + absolute + CLOSE_ABS, name);
    }

    /**
     * Replace the parenthesis once they are treated
     * @param expression The expression as a String
     * @return The same expression with parenthesis replaced
     */
    private String parenthesis_replace(String expression){
        // First index of ")"
        int close = expression.indexOf(CLOSE_PAR);
        int open = close;
        while (open > 0){
            open--;
            if(expression.charAt(open) == OPEN_PAR) break;
        }
        String parenthesis = expression.substring(open+1, close);
        String parenthesis_replaced = parenthesis;
        if(parenthesis.contains(OPEN_ABS +"")) parenthesis_replaced = absolute_replace(parenthesis_replaced);
        Expression parenthesisExpression = createExpr(parenthesis_replaced);
        String name = this.variable_name+(variable_count++);
        this.parenthesisExpressions.put(name, parenthesisExpression);
        expression = expression.replace(OPEN_PAR+parenthesis+CLOSE_PAR, name);
        return expression;
    }

    /**
     * Split the expression according to the most important operator in it.
     * @param expr Mathematical expression
     * @return Two sub-expressions (left and right) or one sub-expression (left --- unary operator case)
     */
    private String[] splitArith(String expr) {
        if (expr.contains(MODULO)){
            lastOperator = MODULO;
            return expr.split("\\"+MODULO, 2);
        }
        if (expr.contains(PLUS)) {
            lastOperator = PLUS;
            return expr.split("\\"+PLUS, 2);
        }
        if (expr.contains(MINUS)) {
            lastOperator = MINUS;
            String[] splited = expr.split("\\"+MINUS, 2);
            // -x
            if(splited[0].isEmpty()) return new String[]{"0", splited[1]};
            return splited;
        }
        if (expr.contains(MUL)) {
            lastOperator = MUL;
            return expr.split("\\"+ MUL, 2);
        }
        if (expr.contains(DIV)) {
            lastOperator = DIV;
            return expr.split("\\"+DIV, 2);
        }
        if (expr.contains(POW)){
            lastOperator = POW;
            return expr.split("\\"+POW, 2);
        }
        lastOperator = null;
        return new String[]{expr};
    }

    /**
     * Given a mathematical expression, output the real Expression (arithmetical).
     * @param expr Mathematical expression
     * @return Expression
     */
    private Expression createExpr(String expr){
        String[] split = splitArith(expr);
        String current_op = lastOperator;
        if(split.length == 1) {
            if(current_op != null && current_op.equals(ABS)) return new Expression(createExpr(split[0]), current_op);
            try {
                return new Expression(Integer.parseInt(split[0]));
            } catch (NumberFormatException e){
                if(parenthesisExpressions.containsKey(split[0])) return parenthesisExpressions.get(split[0]);
                bind(split[0]);
                return new Expression(split[0]);
            }
        } else {
            Expression left = createExpr(split[0]);
            Expression right = createExpr(split[1]);
            if(current_op.equals(MINUS)) right.changeSign();
            return new Expression(left, current_op, right);
        }
    }

    /**
     * Given a mathematical expression (comparison), output the real Expression.
     * @param expr Mathematical expression
     * @return Expression
     */
    public Expression parse(String expr){
        // Init
        this.binders = new HashMap<>();
        this.parenthesisExpressions = new HashMap<>();
        this.variable_count = 0;

        // Delete useless spaces
        expr = expr.replace(" ", "");
        expr = priority_parse(expr);

        // Split in two subexpression to compare
        String[] split = splitCompare(expr);
        String left = split[0];
        String right = split[1];

        // Create the expression for the two subexpressions.
        Expression lExpr = createExpr(left);
        Expression rExpr = createExpr(right);
        lExpr.simple(); rExpr.simple();
        Expression expression = new Expression(lExpr, comp, rExpr);
        expression.setBinding(this.binders, firstVariable, lastVariable);
        return expression;
    }

    /**
     * Given a mathematical expression, output the real Expression.
     * @param expr Mathematical expression
     * @return Expression
     */
    public Expression parse_arith(String expr){
        // Init
        this.binders = new HashMap<>();
        this.parenthesisExpressions = new HashMap<>();
        this.variable_count = 0;

        // Delete useless spaces
        expr = expr.replace(" ", "");
        expr = priority_parse(expr);

        // Create the expression for the two subexpressions.
        Expression expression = createExpr(expr);
        expression.simple();
        expression.setBinding(this.binders, firstVariable, lastVariable);
        return expression;
    }

}