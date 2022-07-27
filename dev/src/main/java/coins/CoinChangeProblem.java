package coins;

import builder.MDDBuilder;
import builder.constraints.ConstraintBuilder;
import dd.mdd.MDD;
import dd.operations.Operation;
import structures.Domains;
import structures.generics.SetOf;

public class CoinChangeProblem {

    public static MDD solve(MDD result, int T, Domains domains){
        MDDBuilder.sum(result, T, T, domains);
        return result;
    }

    public static MDD solveNoSym(MDD result, int T, Domains domains){
        return solveNoSym(result, T, domains, null);
    }

    public static MDD solveNoSym(MDD result, int T, Domains domains, SetOf<Integer> jumps){
        ConstraintBuilder.sumOrdered(result, domains, T, T, T, jumps, null);
        return result;
    }

}
