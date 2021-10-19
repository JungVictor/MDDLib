import mdd.operations.Stochastic;
import structures.StochasticVariable;

public class StochasticVariableTest {

    public static void main(String[] args) {

        StochasticVariable alpha = StochasticVariable.create(4);
        StochasticVariable beta = StochasticVariable.create(4);
        StochasticVariable gamma = StochasticVariable.create(4);
        StochasticVariable delta = StochasticVariable.create(4);


        alpha.setQuantity(0, 1000);
        beta.setQuantity(0, 5000);
        gamma.setQuantity(0, 10000);

        alpha.setValue(7000, 7000);
        beta.setValue(6000, 6000);
        gamma.setValue(4000, 4000);
        delta.setValue(3000, 3000);

        long res = alpha.maxSwappingQuantity(beta, 6000, 10000, 4);
        System.out.println(res);
        System.out.println(gamma.maxSwappingQuantity(alpha, 8000, 10000, 4));

        long threshold = 5000;

        StochasticVariable[] X = {alpha, beta, gamma};
        StochasticVariable pivot;

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            System.out.println("["+lb + ", "+ub+"]");
        }
    }

}
