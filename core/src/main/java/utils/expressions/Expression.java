package utils.expressions;

import structures.generics.MapOf;

import java.util.HashMap;

/**
 * <b>Expression</b><br>
 * Represents an evaluable arithmetic expression.
 */
public class Expression {

    // All possible operations
    public static String LT = "<", LEQ = "<=", GT = ">",  GEQ = ">=", EQ = "=", NEQ = "!=";
    public static String PLUS = "+", MINUS = "-", MUL = "*", DIV = "/", POW = "^";
    public static String ABS = "|", MODULO = "%";
    public static char OPEN_PAR = '(', CLOSE_PAR = ')';

    // Bindings of variables names => int layer
    private HashMap<String, Integer> binding;
    private int firstVariable, lastVariable;

    // Expression is a binary tree
    private Expression left;
    private Expression right;
    private String operator;

    // Leaf values
    private String variable;
    private int constant;

    // Number of variable in the tree
    private int nVar = 0;

    /**
     * Turn the expression into a readable mathematical expression.
     * @return
     */
    public String toString(){
        if(isLeaf()){
            if(nVar == 0) return constant+"";
            return variable;
        }
        String left = this.left.toString();
        if(this.right != null) return "(" + left + " " + operator  + " " + this.right.toString() + ")";
        return operator + left + operator;
    }

    /**
     * Set the bindings between variables names and their index.
     * @param binding
     */
    public void setBinding(HashMap<String, Integer> binding, int first, int last){
        this.binding = new HashMap<>(binding);
        this.firstVariable = first;
        this.lastVariable = last;
    }

    /**
     * Create a new expression that is a constant (leaf).
     * @param constant Value of the constant
     */
    public Expression(int constant){
        this.constant = constant;
    }

    /**
     * Create a new expression that is a variable (leaf).
     * @param variable Name of the variable
     */
    public Expression(String variable){
        this.variable = variable;
        this.nVar = 1;
    }

    /**
     * Create a new expression that is an expression + a unary operator.
     * (basically, absolute value)
     * @param left Expression
     * @param op_unary Unary operator
     */
    public Expression(Expression left, String op_unary){
        this.left = left;
        this.nVar = left.nVar;
        this.operator = op_unary;
    }

    /**
     * Given an expression, simplify it. This operation is called in the construction phase,
     * so by construction all expressions will be simplified to the maximum.
     * @param expression Expression
     * @return Simplified expression.
     */
    private Expression simplify(Expression expression){
        if(expression.nVar == 0 && !expression.isLeaf()) return new Expression(expression.eval_int(this.binding, null));
        expression.simplify();
        return expression;
    }

    /**
     * Simplify an expression (sub function of simplify(Expression expr))
     */
    private void simplify(){
        simple();
        if(right == null) return;
        int value;
        // One of them is constant, and the other is constant and variable.
        if(left.nVar == 0) {
            if(right.left == null || right.right == null) return;
            if(right.left.nVar == 0) {
                if((operator.equals(PLUS) || operator.equals(MINUS)) && (right.operator.equals(MUL) || right.operator.equals(DIV))) return;
                if((right.operator.equals(MODULO)) || right.operator.equals(POW)) return;
                value = localEvaluation(left.constant, operator, right.left.constant);
                this.left = new Expression(value);
                this.right = right.right;
            } else if(right.right.nVar == 0){
                if((operator.equals(PLUS) || operator.equals(MINUS)) && (right.operator.equals(MUL) || right.operator.equals(DIV))) return;
                if((right.operator.equals(MODULO)) || right.operator.equals(POW)) return;
                value = localEvaluation(left.constant, operator, localEvaluation(right.neutralValue(), right.operator, right.right.constant));
                this.left = new Expression(value);
                this.right = right.left;
            }

        } else if(right.nVar == 0){
            if(left.left == null || left.right == null) return;

            if(left.left.nVar == 0) {
                if((operator.equals(PLUS) || operator.equals(MINUS)) && (left.operator.equals(MUL) || left.operator.equals(DIV))) return;
                if((left.operator.equals(MODULO)) || left.operator.equals(POW)) return;
                value = localEvaluation(left.left.constant, operator, right.constant);
                this.operator = left.operator;
                this.right = left.right;
                this.left = new Expression(value);
            } else if(left.right.nVar == 0){
                if((operator.equals(PLUS) || operator.equals(MINUS)) && (left.operator.equals(MUL) || left.operator.equals(DIV))) return;
                if((left.operator.equals(MODULO)) || left.operator.equals(POW)) return;
                value = localEvaluation(localEvaluation(left.neutralValue(), left.operator, left.right.constant), operator, right.constant);
                this.left = left.left;
                this.right = new Expression(value);
            }
        }
    }

    /**
     * Simplify the cases :
     * (x - 0) = x, (x + 0) = x, (x * 1) = x, (x / 1) = x and
     * (0 + x) = x, (1 * x) = x
     */
    public void simple(){
        if(left == null || right == null || operator == null) return;
        left.simple();
        right.simple();

        if(operator.equals(POW)){
            if(right.nVar == 0 && right.constant == 0){
                this.constant = 1;
                this.nVar = 0;
                this.variable = null;
                this.left = null;
                this.right = null;
                this.operator = null;
                return;
            } if(left.nVar == 0 && left.constant == 0) {
                this.constant = 0;
                this.nVar = 0;
                this.variable = null;
                this.left = null;
                this.right = null;
                this.operator = null;
                return;
            }
        }

        if((right.nVar == 0 && right.constant == 0 && operator.equals(MUL)) ||
                (left.nVar == 0 && left.constant == 0 && (operator.equals(MUL) || operator.equals(DIV)))){
            this.constant = 0;
            this.nVar = 0;
            this.variable = null;
            this.left = null;
            this.right = null;
            this.operator = null;
            return;
        }

        if(left.nVar == 0 && left.constant == neutralValue() && (operator.equals(PLUS) || operator.equals(MUL))){
            this.left = this.right;
            this.operator = null;
            this.right = null;

            if(left.isLeaf()) {
                variable = left.variable;
                constant = left.constant;
                left = null;
            } else {
                this.right = this.left.right;
                this.operator = this.left.operator;
                this.left = this.left.left;
            }

        } else if(right.nVar == 0 && right.constant == neutralValue() && !operator.equals(POW)){
            this.operator = null;
            this.right = null;

            if(left.isLeaf()) {
                variable = left.variable;
                constant = left.constant;
                left = null;
            } else {
                this.right = this.left.right;
                this.operator = this.left.operator;
                this.left = this.left.left;
            }
        }
    }

    /**
     * Create a new expression that is an operation between two expression.
     * @param left Expression
     * @param op Binary operator
     * @param right Expression
     */
    public Expression(Expression left, String op, Expression right){
        this.left = simplify(left);
        this.operator = op;
        this.right = simplify(right);
        this.nVar = left.nVar + right.nVar;
    }

    /**
     * Check whether the expression if a leaf or not.
     * @return true if the expression is a leaf, false otherwise.
     */
    public boolean isLeaf(){
        return left == null && right == null;
    }

    /**
     * Change the sign of the operator. + => - and - => +.
     * This is used because of the binary property.
     * Basically, x - y + z is parsed to x - (y + z), which gives
     * x - y - z instead of x - y + z. The solution is to switch the sign.
     * x - y + z => x - (y - z).
     */
    public void changeSign(){
        if(operator == null) return;
        if(operator.equals(PLUS)) operator = MINUS;
        else if(operator.equals(MINUS)) operator = PLUS;
        if(operator.equals(MINUS)) {
            left.changeSign();
            if (right != null) right.changeSign();
        }
    }

    /**
     * Get the neutral value of the operator
     * @return 1 if operator is MULT or DIV, 0 otherwise.
     */
    private int neutralValue(){
        if(operator.equals(MUL) || operator.equals(DIV) || operator.equals(POW)) return 1;
        return 0;
    }

    /**
     * Get the number of variable is the tree
     * @return Number of variable in the tree
     */
    public int nVar(){
        return nVar;
    }

    /**
     * Get the last variable appearing in the expression.
     * That is, the variable with the greatest index.
     * @return The variable with the greatest index.
     */
    public int lastVariable(){
        return lastVariable;
    }

    /**
     * Check if the expression contains a greater operator (or greater or equal)
     * @return True if the operator is > or >=
     */
    public boolean greaterOperator(){
        return operator.equals(GT) || operator.equals(GEQ);
    }

    /**
     * Check if the expression contains a less operator (or less or equal)
     * @return True if the operator is < or <=
     */
    public boolean lessOperator(){
        return operator.equals(LT) || operator.equals(LEQ);
    }

    /**
     * Check if the expression contains an equality (or different)
     * @return True if the operator is = or !=.
     */
    public boolean equalityOperator(){
        return operator.equals(EQ) || operator.equals(NEQ);
    }

    /**
     * Get the bindings of variables
     * @return The bindings of variables
     */
    public HashMap<String, Integer> getBinding(){
        return binding;
    }

    /**
     * Evaluate the tree given the value of the subtrees.
     * @param left Value of the left subtree.
     * @param right Value of the right subtree.
     * @return Value of the tree.
     */
    private int localEvaluation(int left, String operator, int right){
        if(operator.equals(PLUS)) return left + right;
        else if(operator.equals(MINUS)) return left - right;
        else if(operator.equals(MUL)) return left * right;
        else if(operator.equals(DIV)) return left / right;
        else if(operator.equals(MODULO)) return left % right;
        else if(operator.equals(POW)) return (int) Math.pow(left, right);
        // ABS
        if(left < 0) return -left;
        return left;
    }

    /**
     * Evaluate the tree by recursion.
     * @param binding The bindings between variable's names and variable's index
     * @param values Values of the variables.
     * @return Value of the tree.
     */
    private int eval_int(HashMap<String, Integer> binding, MapOf<Integer, Integer> values){
        if(isLeaf()){
            if(variable != null) return values.get(binding.get(variable));
            else return constant;
        } else {
            int left_eval = left.eval_int(binding, values);
            int right_eval;
            if(right != null) right_eval = right.eval_int(binding, values);
            else right_eval = neutralValue();
            return localEvaluation(left_eval, operator, right_eval);
        }
    }

    /**
     * Check whether the comparison is true or false.
     * @param left Value of the left subtree
     * @param right Value of the right subtree
     * @return true if the comparison is satisfied, false otherwise.
     */
    private boolean check(int left, int right){
        if(operator.equals(LT)) return left < right;
        else if(operator.equals(LEQ)) return left <= right;
        else if(operator.equals(GT)) return left > right;
        else if(operator.equals(GEQ)) return left >= right;
        else if(operator.equals(EQ)) return left == right;
        return left != right;
    }

    /**
     * Evaluate if the condition is satisfied given the values of the variables.
     * @param values Values of the variables
     * @return true if the condition is satisfied, false otherwise.
     */
    public boolean eval(MapOf<Integer, Integer> values){
        int left_eval = left.eval_int(binding, values);
        int right_eval = right.eval_int(binding, values);

        return check(left_eval, right_eval);
    }

    public int eval_int(MapOf<Integer, Integer> values){
        return eval_int(binding, values);
    }

}