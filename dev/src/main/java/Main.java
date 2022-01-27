import dd.operations.Stochastic;
import structures.StochasticVariable;
import structures.arrays.ArrayOfLong;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        // Main class when compiling dev
        // ...

        long minThreshold = 5000;
        long maxThreshold = 10000;

        StochasticVariable X1 = StochasticVariable.create(4);
        StochasticVariable X2 = StochasticVariable.create(4);
        StochasticVariable X3 = StochasticVariable.create(4);
        StochasticVariable X4 = StochasticVariable.create(4);
        StochasticVariable X5 = StochasticVariable.create(4);

        X1.setQuantity(1000, 5000);
        X2.setQuantity(0, 5000);
        X3.setQuantity(0, 1000);
        X4.setQuantity(0, 1000);
        X5.setQuantity(0, 10000);

        X1.setValue(0, 10000);
        X2.setValue(0, 6000);
        X3.setValue(0, 5000);
        X4.setValue(0, 5000);
        X5.setValue(0, 3200);

        StochasticVariable[] X = {X1, X2, X3, X4, X5};

        long[][] qBounds = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        // Set the bounds (quantity)
        for(int i = 0; i < X.length; i++) X[i].setQuantity(qBounds[i][0], qBounds[i][1]);

        ArrayOfLong min = Stochastic.minCostFiltering(X, minThreshold, maxThreshold, 4);

        // Set the lower bound (value)
        for (int i = 0; i < X.length; i++) {
            X[i].setMinValue(min.get(i));
            System.out.println(X[i]);
        }


    }

}