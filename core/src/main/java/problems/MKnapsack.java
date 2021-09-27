package problems;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.properties.PropertySum;
import representation.MDDPrinter;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.TupleOfInt;
import utils.Logger;

public class MKnapsack {

    // 0 :  # items (if = 0, unlimited)
    // 1 :  Profit
    // 2+ : Weight(s)
    // Labels are corresponding to the index of the line in the matrix
    // The empty item's value is -1 and is automatically added.
    private int[][] data;

    // Capacity is the maximum number of elements that can be selected
    // At worst, it is the number of elements
    // At best, it is the optimal number
    // Can run some sort of greedy algorithm to figure out a simple upper bound that is < #elements.
    private int capacity;

    // The maximum weight for each dimension.
    private int[] W;


    private MapOf<Integer, TupleOfInt> GCC;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public MKnapsack(int[][] data, int[] W, int capacity){
        this.data = data;
        this.W = W;
        this.capacity = capacity;

        GCC = Memory.MapOfIntegerTupleOfInt();
        for(int i = 0; i < data.length; i++) if(data[i][0] > 0) GCC.put(i, TupleOfInt.create(0, data[i][0]));
    }


    //**************************************//
    //                SOLVER                //
    //**************************************//

    /**
     * Build the MDD satisfying the weights and GCC constraints.
     * It contains all the solutions of the problem.
     * @return The MDD containing all solutions satisfying the constraints
     */
    public MDD solve(){
        int offset = 2;
        int nDimension = data[0].length - offset;

        Logger.out.information("\rDimension 0\n");

        MDD weights = dimension(0, offset);
        MDD tmp = weights;

        // Intersection of the weights
        for(int d = 1; d < nDimension; d++) {
            Logger.out.information("\rDimension "+d+"\n");
            MDD dimension = dimension(d, offset);
            weights = Operation.intersection(weights, dimension);
            Memory.free(tmp);
            Memory.free(dimension);
            tmp = weights;
        }

        weights.clearAllAssociations();
        PMDD solutions = PMDD.create();
        weights.copy(solutions);

        Memory.free(weights);



        int profit = maximise(solutions);
        MDD solution = getMaxResult(solutions, profit);

        solution.accept(new MDDPrinter());
        Logger.out.information("\rMAX PROFIT = " + profit + "\n");

        return solution;
    }

    private MDD getMaxResult(MDD result, int max){
        MDD sum = MDD.create();

        Domains profits = Domains.create();
        for(int i = 0; i < capacity; i++) {
            profits.add(i);
            for(int j = 0; j < data.length; j++) profits.put(i, data[j][1]);
            profits.put(i, 0);
        }

        MapOf<Integer, SetOf<Integer>> mapping = Memory.MapOfIntegerSetOfInteger();
        for(int i = 0; i < data.length; i++) {
            int p = data[i][1];
            if(!mapping.contains(p)) mapping.put(p, Memory.SetOfInteger());
            mapping.get(p).add(i);
        }
        if(!mapping.contains(0)) mapping.put(0, Memory.SetOfInteger());
        mapping.get(0).add(-1);

        MDDBuilder.sum(sum, max, capacity, profits);
        sum.replace(mapping);

        MDD maximise = Operation.intersection(result, sum);
        Memory.free(sum);
        Memory.free(result);

        return maximise;
    }

    /**
     * Get the maximum profit that can be obtained in the set of solutions
     * @param result The MDD representing the set of all solutions
     * @return The maximum profit
     */
    @SuppressWarnings("unchecked")
    private int maximise(PMDD result){
        MapOf<Integer, Integer> profits = Memory.MapOfIntegerInteger();
        for(int i = 0; i < data.length; i++) profits.put(i, data[i][1]);
        profits.put(-1, 0);

        PropertySum profitProperty = PropertySum.create(0,0, profits);
        result.addRootProperty("profit", profitProperty);
        MapOf<Integer, Integer> maxProfit = (MapOf<Integer, Integer>) result.propagateProperties().get("profit").getResult().getData();

        int max = maxProfit.get(1);

        Memory.free(maxProfit);
        Memory.free(profits);

        return max;
    }

    /**
     * Return the MDD of weight associated with the ith dimension.
     * @param d The dimension of the weights
     * @param offset The offset on the data's array (default = 2)
     * @return The MDD corresponding to the given dimension.
     */
    private MDD dimension(int d, int offset){
        Domains domains = Domains.create();
        MapOf<Integer, SetOf<Integer>> mapping = Memory.MapOfIntegerSetOfInteger();

        int min = 0;
        for(int i = 0; i < capacity; i++) {
            int w = data[i][d+offset];
            domains.add(i);
            domains.put(i, w);
            domains.put(i, 0);
            if(w > min) min = w;
            if(!mapping.contains(w)) mapping.put(w, Memory.SetOfInteger());
            mapping.get(w).add(i);
        }
        if(!mapping.contains(0)) mapping.put(0, Memory.SetOfInteger());
        mapping.get(0).add(-1);

        MDD dimension = MDDBuilder.sum(MDD.create(), 0, W[d], capacity, domains);
        dimension.replace(mapping);

        Memory.free(domains);
        Memory.free(mapping);


        return dimension;
    }

}
