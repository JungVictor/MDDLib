package ui;

import builder.constraints.ConstraintBuilder;
import dd.mdd.MDD;
import dd.operations.ConstraintOperation;
import dd.operations.Operation;
import memory.Memory;
import structures.Domains;

import java.util.ArrayList;
import java.util.HashSet;

public class Model {

    private static final int INT = 0, UNI = 1, DIA = 2, MIN = 3;
    private Variable[] variables;
    private Domains domains;

    // Type of operation for each instruction
    private ArrayList<Integer> type = new ArrayList<>();
    // Instructions
    private final ArrayList<Constraint[]> instructions = new ArrayList<>();
    private final ArrayList<Constraint> results = new ArrayList<>();

    // tmp arrays
    private final HashSet<Constraint> mdds = new HashSet<>(),
            toBuild = new HashSet<>(),
            otf = new HashSet<>();



    // ---------------------
    // Array creation
    // ---------------------

    /**
     * Create an array of n Variables
     * @param n Number of variables
     * @return An array of Variable of given size n
     */
    public Variable[] variables(int n){
        Variable[] array = new Variable[n];
        for(int i = 0; i < array.length; i++) array[i] = new Variable(i);
        this.variables = array;
        return array;
    }

    /**
     * Create an array of Variable of specified capacity. <br>
     * All Variables have the same domain.
     * @param n Number of variables
     * @param domain Domain of the variables
     * @return An array of Variable of given size n
     */
    public Variable[] variables(int n, int[] domain){
        Variable[] array = new Variable[n];
        for(int i = 0; i < array.length; i++) array[i] = new Variable(i, domain);
        this.variables = array;
        domains = Domains.create(variables.length);
        for(int i = 0; i < domains.size(); i++){
            for(int v : variables[i].getDomain()) domains.put(i, v);
        }
        return array;
    }

    /**
     * Create an array of Variable of specified capacity. <br>
     * All Variables have the same domain [start, stop].<br>
     * @param n Number of variables
     * @param start First value of the domain
     * @param stop Last value of the domain is at most stop
     * @return An array of Variable of given size n
     */
    public Variable[] variables(int n, int start, int stop){
        return variables(n, start, stop, 1);
    }

    /**
     * Create an array of Variable of specified capacity. <br>
     * All Variables have the same domain.<br>
     * The domain is created [start, start + step...] until reaching max value stop.
     * @param n Number of variables
     * @param start First value of the domain
     * @param stop Last value of the domain is at most stop
     * @param step The value of the step while building the domain
     * @return An array of Variable of given size n
     */
    public Variable[] variables(int n, int start, int stop, int step){
        int[] domain = new int[(stop-start)/step+1];
        int i = 0;
        for(int v = start; v <= stop; v+=step) domain[i++] = v;
        return variables(n, domain);
    }

    /**
     * Get the number of variables in the model
     * @return The number of variables in the model
     */
    public int numberOfVariables(){
        return variables.length;
    }

    /**
     * Get the domains of the variables in the model
     * @return The domains of the variables in the model
     */
    public Domains getDomains(){
        return domains;
    }

    // ---------------------
    // Operations
    // ---------------------

    /**
     * Add the given constraints to the model, using the given operator.
     * @param OP The operator
     * @param constraints The constraints
     * @return The constraint that is the result of the operation between given constraints
     */
    private Constraint addToModel(int OP, Constraint... constraints){
        instructions.add(constraints);
        type.add(OP);
        Constraint result = new Constraint();
        results.add(result);
        return result;
    }

    /**
     * Perform the intersection between multiple constraints
     * @param constraints The constraints
     * @return The constraint that is the result of the intersection
     */
    public Constraint intersection(Constraint... constraints){
        return addToModel(INT, constraints);
    }

    /**
     * Perform the union between multiple constraints
     * @param constraints The constraints
     * @return The constraint that is the result of the union
     */
    public Constraint union(Constraint... constraints){
        return addToModel(UNI, constraints);
    }

    /**
     * Perform the diamond operation between multiple constraints
     * @param constraints The constraints
     * @return The constraint that is the result of the diamond operation
     */
    public Constraint diamond(Constraint... constraints){
        return addToModel(DIA, constraints);
    }

    /**
     * Perform the difference between multiple constraints
     * @param constraints The constraints
     * @return The constraint that is the result of the difference
     */
    public Constraint minus(Constraint... constraints){
        return addToModel(MIN, constraints);
    }

    /**
     * Execute the given instruction
     * @param instruction Index of the instruction in the instruction list
     */
    private void execute(int instruction){
        Constraint[] constraints = instructions.get(instruction);

        mdds.clear();
        toBuild.clear();
        otf.clear();

        int OP = type.get(instruction);

        for(int i = 0; i < constraints.length; i++){
            if(constraints[i].isMDD()) mdds.add(constraints[i]);
            else if(constraints[i].isToBuild() || OP != INT) toBuild.add(constraints[i]);
            else otf.add(constraints[i]);
        }

        MDD base = null;
        for(int i = 0; i < constraints.length; i++) {
            if(mdds.contains(constraints[i]) || toBuild.contains(constraints[i])) {
                base = constraints[i].getMDD();
                constraints[i] = null;
                break;
            }
        }
        if(base == null) {
            base = MDD.create();
            int i = 0;
            while (constraints[i].isOTF()) i++;
            ConstraintBuilder.build(base, constraints[i].getRoot(), domains, variables.length);
            constraints[i] = null;
            base.reduce();
        }

        MDD result = base, tmp;
        for(int i = 0; i < constraints.length; i++){
            if(constraints[i] == null) i++;
            if(i >= constraints.length) break;

            tmp = result;
            result = null;

            // On the fly intersection
            if(otf.contains(constraints[i])) {
                result = MDD.create();
                ConstraintOperation.intersection(result, tmp, constraints[i].getRoot(), false);
            } else {
                if(toBuild.contains(constraints[i])){
                    MDD build = MDD.create();
                    ConstraintBuilder.build(build, constraints[i].getRoot(), domains, variables.length);
                    constraints[i].setMDD(build);
                }
                if(OP == INT) {
                    result = MDD.create();
                    Operation.intersection(result, tmp, constraints[i].getMDD());
                }
                else if(OP == UNI) result = Operation.union(tmp, constraints[i].getMDD());
                else if(OP == DIA) result = Operation.diamond(tmp, constraints[i].getMDD());
                else if(OP == MIN) result = Operation.minus(tmp, constraints[i].getMDD());
                Memory.free(constraints[i].getMDD());
                constraints[i].setMDD(null);
            }
            Memory.free(tmp);
        }
        Constraint cResult = results.get(instruction);
        cResult.setMDD(result);
    }

    /**
     * Execute all instructions given to the model.
     * @return The MDD that is the result of the instructions
     */
    public MDD execute(){
        domains = Domains.create(variables.length);
        for(int i = 0; i < domains.size(); i++){
            for(int v : variables[i].getDomain()) domains.put(i, v);
        }

        for(int i = 0; i < instructions.size(); i++) execute(i);

        return results.get(results.size() - 1).getMDD();
    }

}
