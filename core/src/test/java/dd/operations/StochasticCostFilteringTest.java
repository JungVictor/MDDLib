package dd.operations;

import memory.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structures.StochasticVariable;
import structures.arrays.ArrayOfLong;
import utils.StochasticVariablesManagements;

import static org.junit.jupiter.api.Assertions.*;

class StochasticCostFilteringTest {

    String[] testFiles = {"test1.txt", "test2.txt"};
    double[] minThresholds = {0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, -1, -1, -1};

    private void testEquality(StochasticVariable[] X, long threshold, long totalQuantity, int precision){
        ArrayOfLong pseudolinear = Stochastic.minCostFiltering(X, threshold, totalQuantity, precision);
        ArrayOfLong polynomial = Stochastic.minCostFilteringPolynomial(X, threshold, totalQuantity, precision);
        ArrayOfLong dichotomous = Stochastic.minCostFilteringDichotomous(X, threshold, totalQuantity, precision);

        for(int i = 0; i < X.length; i++) {
            assertEquals(pseudolinear.get(i), polynomial.get(i));
            assertEquals(polynomial.get(i), dichotomous.get(i));
            assertEquals(pseudolinear.get(i), dichotomous.get(i));
        }

        Memory.free(polynomial);
        Memory.free(pseudolinear);
        Memory.free(dichotomous);
    }

    @Test
    void minCostFilteringTest(){
        int precision;
        long one, minThreshold, maxThreshold;
        long maxPack;

        for(String filename : testFiles) {

            // Load the data
            StochasticVariable[] X = StochasticVariablesManagements.getStochasticVariables(filename);

            // Set the precision fitting the data
            precision = X[0].getPrecision();
            one = (long) Math.pow(10, precision);
            maxThreshold = one;

            // Compute the maximum amount achievable with the current data
            maxPack = Stochastic.maxPacking(X, ArrayOfLong.create(X.length), one).get(0) / one;
            minThresholds[minThresholds.length - 1] = maxPack;
            minThresholds[minThresholds.length - 2] = maxPack - (0.001) * one;
            minThresholds[minThresholds.length - 3] = maxPack - (0.01) * one;

            // Test all min thresholds
            for (double minK : minThresholds) {

                minThreshold = (long) (minK * one);

                // Impossible to satisfy
                if(minThreshold > maxPack) continue;

                // Test equality of the methods BEFORE quantity filtering
                testEquality(X, minThreshold, maxThreshold, precision);

                // Filter the quantity
                long[][] qBounds = Stochastic.computeBounds(X, minThreshold, maxThreshold, 8);
                for (int i = 0; i < X.length; i++) X[i].setQuantity(qBounds[i][0], qBounds[i][1]);

                // Test equality of the methods AFTER quantity filtering
                testEquality(X, minThreshold, maxThreshold, precision);
            }
        }

    }
}