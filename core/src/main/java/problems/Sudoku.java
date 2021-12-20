package problems;

import builder.MDDBuilder;
import builder.constraints.ConstraintBuilder;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import mdd.operations.Operation;
import memory.Memory;
import structures.Domains;
import structures.generics.SetOf;

public class Sudoku {

    public static MDD solve(int x, int y){
        int n = x * y;
        SetOf<Integer> V = Memory.SetOfInteger();
        for(int i = 0; i < n; i++) V.add(i+1);

        MDD result = lines(V, n);
        //MDD result = MDDBuilder.universal(MDD.create(), createDomains(n));
        result = columns(result, V, n);
        result = cases(result, V, x, y);

        return result;
    }

    public static MDD lines(SetOf<Integer> V, int n){
        Domains D = Domains.create();
        for(int i = 0; i < n; i++) {
            D.add(i);
            D.get(i).add(i+1);
        }

        MDD fL = MDDBuilder.universal(MDD.create(), D);

        for(int i = 0; i < n; i++){
            D.get(i).clear();
            for(int j = 0; j < n; j++) if(j != i) D.get(i).add(j+1);
        }

        MDD oL = MDDBuilder.alldiff(MDD.create(), D, V, null, n);

        MDD result = Operation.concatenate(fL, oL);
        Memory.free(fL);
        MDD tmp = result;
        for(int i = 2; i < n; i++) {
            result = Operation.concatenate(result, oL);
            Memory.free(tmp);
            tmp = result;
        }
        Memory.free(oL);
        return result;
    }

    private static MDD columns(MDD lines, SetOf<Integer> V, int n){
        SetOf<Integer> variables = Memory.SetOfInteger();
        MDD result = lines, tmp = result;
        for(int i = 0; i < n; i++) {
            variables = columnConstraint(variables, i, n);
            result = ConstraintOperation.allDiff(MDD.create(), result, V, variables);
            Memory.free(tmp);
            tmp = result;
            variables.clear();
        }
        Memory.free(variables);
        return result;
    }

    private static MDD cases(MDD columns, SetOf<Integer> V, int x, int y){
        SetOf<Integer> variables = Memory.SetOfInteger();
        MDD result = columns, tmp = result;
        for(int ci = 0; ci < x; ci++) {
            for(int cj = 0; cj < y; cj++) {
                variables = caseConstraint(variables, ci, cj, x, y);
                result = ConstraintOperation.allDiff(MDD.create(), result, V, variables);
                Memory.free(tmp);
                tmp = result;
                variables.clear();
            }
        }
        Memory.free(variables);
        return result;
    }

    private static SetOf<Integer> caseConstraint(SetOf<Integer> variables, int ci, int cj, int x, int y){
        int n = x * y;
        int start = ci*n*x+cj*y;
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++) variables.add(start+i*n+j);
        }
        return variables;
    }

    private static SetOf<Integer> columnConstraint(SetOf<Integer> variables, int i, int n){
        for(int j = 0; j < n; j++) variables.add(j*n+i);
        return variables;
    }

    private static Domains createDomains(int n){
        Domains D = Domains.create();
        // Fix the first line to 123 456 789 (when n = 9)
        for(int i = 0; i < n; i++) {
            D.add(i);
            D.get(i).add(i+1);
        }

        int sudokuCase;
        for(int i = 1; i < n; i++) {    // lines
            for(int j = 0; j < n; j++) {    // columns
                sudokuCase = i*n+j;
                D.add(sudokuCase);
                for(int v = 1; v <= n; v++){
                    if(v != j+1) D.get(sudokuCase).add(v);
                }
            }
        }
        return D;
    }

}
