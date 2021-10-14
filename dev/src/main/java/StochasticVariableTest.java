import mdd.operations.Stochastic;
import structures.StochasticVariable;

public class StochasticVariableTest {

    public static void main(String[] args) {

        StochasticVariable alpha = StochasticVariable.create(4);
        StochasticVariable beta = StochasticVariable.create(4);
        StochasticVariable gamma = StochasticVariable.create(4);
        StochasticVariable delta = StochasticVariable.create(4);

        alpha.setQuantity(2000, 10000);

        alpha.setValue(6000, 6000);
        beta.setValue(3000, 3000);


        StochasticVariable[] X = {alpha, beta};

        long res = Stochastic.packByNonDecreasingCost(X, alpha, 5000, 4);
        System.out.println(res);
    }

}
