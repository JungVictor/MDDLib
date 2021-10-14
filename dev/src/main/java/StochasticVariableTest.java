import mdd.operations.Stochastic;
import structures.StochasticVariable;

public class StochasticVariableTest {

    public static void main(String[] args) {

        StochasticVariable alpha = StochasticVariable.create(4);
        StochasticVariable beta = StochasticVariable.create(4);
        StochasticVariable gamma = StochasticVariable.create(4);
        StochasticVariable delta = StochasticVariable.create(4);

        alpha.setQuantity(0, 3000);
        beta.setQuantity(0, 5000);
        gamma.setQuantity(0, 10000);

        alpha.setValue(9000, 9000);
        beta.setValue(6000, 6000);
        gamma.setValue(1000, 1000);

        StochasticVariable[] X = {alpha, beta, gamma};

        long res = Stochastic.lowerbound(X, alpha, 5000, 4);
        System.out.println(res);
        res = Stochastic.upperbound(X, alpha, 5000, 4);
        System.out.println(res);
    }

}
