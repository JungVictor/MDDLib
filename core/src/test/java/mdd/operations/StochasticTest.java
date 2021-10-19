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
        long minThreshold = 6000;
        long maxThreshold = 10000;

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

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }

    @Test
    void instance2(){
        long minThreshold = 6000;
        long maxThreshold = 10000;

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

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }

    @Test
    void instance3(){
        long minThreshold = 6000;
        long maxThreshold = 10000;

        alpha.setQuantity(0, 10000);
        beta.setQuantity(0, 8000);
        gamma.setQuantity(0, 10000);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {5000, 10000},
                {0, 5000},
                {0, 3333}
        };


        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }

    @Test
    void instance4(){
        long minThreshold = 6000;
        long maxThreshold = 10000;

        alpha.setQuantity(5000, 10000);
        beta.setQuantity(0, 1000);
        gamma.setQuantity(0, 3333);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {6333, 10000},
                {0, 1000},
                {0, 3333}
        };

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }

    @Test
    void instance5(){
        long minThreshold = 6000;
        long maxThreshold = 10000;

        alpha.setQuantity(5000, 10000);
        beta.setQuantity(0, 4000);
        gamma.setQuantity(1000, 3333);

        StochasticVariable[] X = {alpha, beta, gamma};

        long[][] expected = {
                {5500, 9000},
                {0, 3500},
                {1000, 3333}
        };

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }


    /********************
     THRESHOLD = 5000
     *********************/

    @Test
    void instance6(){
        long minThreshold = 5000;
        long maxThreshold = 10000;

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

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }

    @Test
        // Rounding errors...
    void instance7(){
        long minThreshold = 5000;
        long maxThreshold = 10000;

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

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }
    @Test
        // Rounding errors...
    void instance7b(){
        long minThreshold = 5000;
        long maxThreshold = 10000;

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

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }

    @Test
    void instance8(){
        long minThreshold = 5000;
        long maxThreshold = 10000;

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

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }

    @Test
    void instance9(){
        long minThreshold = 5000;
        long maxThreshold = 10000;

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

        long[][] actual = Stochastic.computeBounds(X, minThreshold, maxThreshold, 4);

        for(int i = 0; i < expected.length; i++) {
            for(int j = 0; j < expected[i].length; j++) assertEquals(actual[i][j], expected[i][j]);
        }
    }
}