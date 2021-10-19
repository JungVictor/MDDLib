package mdd.operations;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structures.StochasticVariable;

import static org.junit.jupiter.api.Assertions.*;

class StochasticTest {

    StochasticVariable alpha, beta, gamma, delta, omega;
    StochasticVariable pivot;

    @BeforeEach
    void init(){

        alpha = StochasticVariable.create(4);
        beta = StochasticVariable.create(4);
        gamma = StochasticVariable.create(4);
        delta = StochasticVariable.create(4);
        omega = StochasticVariable.create(4);

        alpha.setValue(7000, 7000);
        beta.setValue(5000, 5000);
        gamma.setValue(4000, 4000);
        delta.setValue(3000, 3000);
        omega.setValue(0,0);
        omega.setQuantity(0, 10000);

    }

    /********************
        THRESHOLD = 6000
    *********************/
    @Test
    void instance1(){
        long threshold = 6000;

        alpha.setQuantity(5000, 6000);
        beta.setQuantity(0, 2000);
        gamma.setQuantity(0, 3333);
        delta.setQuantity(0, 2500);

        StochasticVariable[] X = {alpha, beta, gamma, delta};

        long[][] expected = {
                {6000, 6000},
                {2000, 2000},
                {2000, 2000},
                {0, 0}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }

    @Test
    void instance2(){
        long threshold = 6000;

        alpha.setQuantity(0, 10000);
        beta.setQuantity(0, 10000);
        gamma.setQuantity(0, 10000);
        delta.setQuantity(0, 10000);

        StochasticVariable[] X = {alpha, beta, gamma, delta};

        long[][] expected = {
                {5000, 10000},
                {0, 5000},
                {0, 3333},
                {0, 2500}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }

    @Test
    void instance3(){
        long threshold = 6000;

        alpha.setQuantity(0, 10000);
        beta.setQuantity(0, 8000);
        gamma.setQuantity(0, 10000);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {5000, 10000},
                {0, 5000},
                {0, 3333}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }

    @Test
    void instance4(){
        long threshold = 6000;

        alpha.setQuantity(5000, 10000);
        beta.setQuantity(0, 1000);
        gamma.setQuantity(0, 3333);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {6333, 10000},
                {0, 1000},
                {0, 3333}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }

    @Test
    void instance5(){
        long threshold = 6000;

        alpha.setQuantity(5000, 10000);
        beta.setQuantity(0, 4000);
        gamma.setQuantity(1000, 3333);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {5500, 9000},
                {0, 3500},
                {1000, 3333}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }


    /********************
     THRESHOLD = 5000
     *********************/

    @Test
    void instance6(){
        long threshold = 5000;

        alpha.setQuantity(0, 10000);
        beta.setQuantity(0, 10000);
        gamma.setQuantity(0, 10000);
        delta.setQuantity(0, 10000);

        StochasticVariable[] X = {alpha, beta, gamma, delta};

        long[][] expected = {
                {0, 10000},
                {0, 10000},
                {0, 6666},
                {0, 5000}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }

    @Test
        // Rounding errors...
    void instance7(){
        long threshold = 5000;

        alpha.setQuantity(0, 2000);
        beta.setQuantity(0, 10000);
        gamma.setQuantity(0, 6666);
        delta.setQuantity(0, 5000);

        beta.setValue(6000, 6000);

        StochasticVariable[] X = {alpha, beta, gamma, delta};

        long[][] expected = {
                {0, 2000},
                {1997, 10000},
                {0, 6003},
                {0, 4000}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }
    @Test
        // Rounding errors...
    void instance7b(){
        long threshold = 5000;

        alpha.setQuantity(0, 2000);
        beta.setQuantity(0, 10000);
        gamma.setQuantity(0, 6500);
        delta.setQuantity(0, 5000);

        beta.setValue(6000, 6000);

        StochasticVariable[] X = {alpha, beta, gamma, delta};

        long[][] expected = {
                {0, 2000},
                {2000, 10000},
                {0, 6000},
                {0, 4000}
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }

    @Test
    void instance8(){
        long threshold = 5000;

        alpha.setQuantity(0, 1000);
        beta.setQuantity(0, 5000);
        gamma.setQuantity(0, 10000);

        beta.setValue(6000, 6000);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {0, 1000},
                {3500, 5000},
                {3250, 5500},
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }

    @Test
    void instance9(){
        long threshold = 5000;

        alpha.setQuantity(0, 10000);
        beta.setQuantity(0, 5000);
        gamma.setQuantity(0, 1000);

        beta.setValue(6000, 6000);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {2285, 10000},
                {0, 5000},
                {0, 1000},
        };

        for(int i = 0; i < X.length; i++) {
            pivot = X[i];
            long lb = Stochastic.lowerbound(X, pivot, threshold, 4);
            assertEquals(expected[i][0], lb);

            long ub = Stochastic.upperbound(X, pivot, threshold, 4);
            assertEquals(expected[i][1], ub);
        }
    }
}