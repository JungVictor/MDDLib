package utils;

import dd.operations.Stochastic;
import structures.StochasticVariable;
import structures.arrays.ArrayOfLong;

public class StochasticVariablesGenerator {

    public static StochasticVariable[] generateRandomWorstCase(int n, int precision){
        long max = (long) Math.pow(10, precision);
        StochasticVariable[] stochasticVariables = new StochasticVariable[n];
        long averageQuantity = max / (n/2);
        long randomQuantityMax;
        long randomQuantityMin;
        long maxValue;

        long firstNonFull = 0;

        while (firstNonFull >= n || firstNonFull < n/2 - 1) {
            for (int i = 0; i < n / 2; i++) {
                randomQuantityMax = Math.max(0, averageQuantity / 2 + (long) (Math.random() * averageQuantity));
                if (randomQuantityMax == 0) {
                    throw new IllegalArgumentException("The parameter n is too big for the given precicion. n = " + n + ", precision = " + precision);
                }

                randomQuantityMin = Math.min(Math.max(0, randomQuantityMax / 10 + (long) (Math.random() * (randomQuantityMax - randomQuantityMax / 10))), randomQuantityMax);
                maxValue = Math.max((long) Math.ceil(max - i * (max / n)), 1);

                stochasticVariables[i] = StochasticVariable.create(8);
                stochasticVariables[i].setQuantity(randomQuantityMin, randomQuantityMax);
                stochasticVariables[i].setValue(0, maxValue);
            }

            long quantityMax = Math.max(averageQuantity / (n / 2), 1);
            long quantityMin = 0;

            for (int i = n / 2; i < n; i++) {
                maxValue = Math.max((long) Math.ceil(max - i * (max / n)), 1);

                stochasticVariables[i] = StochasticVariable.create(8);
                stochasticVariables[i].setQuantity(quantityMin, quantityMax);
                stochasticVariables[i].setValue(0, maxValue);
            }
            ArrayOfLong maxPackingQuantities = ArrayOfLong.create(stochasticVariables.length);
            ArrayOfLong tmp = Stochastic.maxPacking(stochasticVariables, maxPackingQuantities, (long) Math.pow(10, precision));
            firstNonFull = tmp.get(1);
        }

        return stochasticVariables;
    }
}
