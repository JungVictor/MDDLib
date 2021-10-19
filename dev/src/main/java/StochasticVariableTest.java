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
        beta.setValue(0, 0);
        gamma.setValue(4000, 4000);
        delta.setValue(3000, 3000);

        StochasticVariable[] X = {alpha, beta};

        long res = Stochastic.upperboundMin(X, 0, 5000, 10000, 4);
        System.out.println(res);
    }

}
