import dd.operations.Stochastic;
import structures.StochasticVariable;
import structures.arrays.ArrayOfLong;
import utils.ArgumentParser;
import utils.StochasticVariablesManagements;

public class FilteringTest {
    public static void minCostFiltering(String[] args){
        ArgumentParser argumentParser = new ArgumentParser("-K", "50000000",
                "-dataFile", "tmp.txt", "-method", "0", "-repeat", "1", "-showResult", "false");
        argumentParser.read(args);
        long minThreshold = Long.parseLong(argumentParser.get("-K"));
        String dataFile = argumentParser.get("-dataFile");
        int method = Integer.parseInt(argumentParser.get("-method"));
        int repeat = Integer.parseInt(argumentParser.get("-repeat"));
        boolean showResult = Boolean.parseBoolean(argumentParser.get("-showResult"));

        //StochasticVariable[] X = StochasticVariablesGenerator.generateRandomWorstCase(1000, 8);
        //StochasticVariablesManagements.saveStochasticVariables("tmp.txt", X, 8);

        StochasticVariable[] X = StochasticVariablesManagements.getStochasticVariables(dataFile);
        int precision = X[0].getPrecision();
        long maxThreshold = (long) Math.pow(10, precision);
        //for(int i = 0; i < X.length; i++) {System.out.println(i+" : "+X[i]);}

        ArrayOfLong maxPackingQuantities = ArrayOfLong.create(X.length);
        ArrayOfLong tmp = Stochastic.maxPacking(X, maxPackingQuantities, maxThreshold);

        long[][] qBounds = Stochastic.computeBounds(X, minThreshold, maxThreshold, precision);

        for(int i = 0; i < X.length; i++) X[i].setQuantity(qBounds[i][0], qBounds[i][1]);
        //for(int i = 0; i < X.length; i++) {System.out.println(i+" : "+X[i]);}

        long time1 = System.currentTimeMillis();
        long time2 = 0;
        ArrayOfLong minBounds;

        switch(method){
            case 1:
                System.out.println("Resolution with the dichotomous method V3...");
                for (int i = 0; i < repeat; i++) {
                    Stochastic.minCostFilteringDichotomousV3(X, minThreshold, maxThreshold, precision);
                }
                time2 = System.currentTimeMillis();
                minBounds =  Stochastic.minCostFilteringDichotomousV3(X, minThreshold, maxThreshold, precision);
                for(int i = 0; i < X.length; i++) X[i].setMinValue(minBounds.get(i));
                break;
            case 2:
                System.out.println("Resolution with the n*k method...");
                for (int i = 0; i < repeat; i++) {
                    Stochastic.minCostFiltering(X, minThreshold, maxThreshold, precision);
                }
                time2 = System.currentTimeMillis();
                minBounds =  Stochastic.minCostFiltering(X, minThreshold, maxThreshold, precision);
                for(int i = 0; i < X.length; i++) X[i].setMinValue(minBounds.get(i));
                break;
            default:
                System.out.println("Resolution with the polynomial method V2...");
                for (int i = 0; i < repeat; i++) {
                    Stochastic.minCostFilteringPolynomialV2(X, minThreshold, maxThreshold, precision);
                }
                time2 = System.currentTimeMillis();
                minBounds =  Stochastic.minCostFilteringPolynomialV2(X, minThreshold, maxThreshold, precision);
                for(int i = 0; i < X.length; i++) X[i].setMinValue(minBounds.get(i));
                break;
        }


        if(showResult){
            for(int i = 0; i < X.length; i++) {System.out.println(i+" : "+X[i]);}
        }

        System.out.println("\n========================================================");
        switch(method){
            case 1:
                System.out.println("Resolution with the dichotomous method :");
                break;
            case 2:
                System.out.println("Resolution with the n*k method :");
                break;
            default:
                System.out.println("Resolution with the polynomial method :");
                break;
        }
        System.out.println("--------------------------------------------------------");
        System.out.println("Data used : "+dataFile);
        System.out.println("Number of StochasticVariable : "+X.length);
        System.out.println("Precision : "+precision);
        System.out.println("--------------------------------------------------------");
        System.out.println("Total cost of max packing : "+tmp.get(0));
        System.out.println("Threshold :                 "+minThreshold*maxThreshold);
        System.out.println("--------------------------------------------------------");
        System.out.println("First non full : "+tmp.get(1));
        System.out.println("First non full quantity : "+maxPackingQuantities.get((int)tmp.get(1)));
        System.out.println("--------------------------------------------------------");
        System.out.println("Time for "+repeat+" iteration(s) : "+(time2-time1)+" ms");
        System.out.println("========================================================");
    }
}
