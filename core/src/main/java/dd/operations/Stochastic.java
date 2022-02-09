package dd.operations;

import dd.mdd.MDD;
import dd.mdd.components.Node;
import memory.Memory;
import structures.StochasticVariable;
import structures.arrays.ArrayOfBoolean;
import structures.arrays.ArrayOfLong;
import structures.generics.MapOf;
import structures.tuples.TupleOfInt;
import utils.SmallMath;

public class Stochastic {

    /**
     * Given a MDD and a map associating labels to probabilities (by layer), compute
     * the probability of the whole MDD.
     * @param mdd The MDD
     * @param P The associations labels → probabilities
     * @param precision The precision of the probabilities
     * @param ceil true if the rounding must be ceil, false if floor
     * @return The probability of the MDD
     */
    public static double probability(MDD mdd, MapOf<Integer, Double>[] P, int precision, boolean ceil){
        if(mdd.getTt() == null) return 0;
        if(mdd.getTt() == mdd.getRoot()) return 0;
        MapOf<Node, Double> currentLayer = Memory.MapOfNodeDouble();
        MapOf<Node, Double> next = Memory.MapOfNodeDouble(), tmp;

        double divisor = Math.pow(10, precision);
        // First case -> multiplication (so init at *1* NOT 0 !)
        currentLayer.put(mdd.getTt(), divisor);
        if(ceil) {
            for (int i = mdd.size() - 2; i >= 0; i--) {
                for (Node x : mdd.getLayer(i)) {
                    double sum = 0;
                    for (int arc : x.getChildren())
                        sum += SmallMath.multiplyCeil(currentLayer.get(x.getChild(arc)), P[i].get(arc), divisor);
                    next.put(x, sum);
                }
                currentLayer.clear();
                tmp = currentLayer;
                currentLayer = next;
                next = tmp;
            }
        } else {
            for (int i = mdd.size() - 2; i >= 0; i--) {
                for (Node x : mdd.getLayer(i)) {
                    double sum = 0;
                    for (int arc : x.getChildren())
                        sum += SmallMath.multiplyFloor(currentLayer.get(x.getChild(arc)), P[i].get(arc), divisor);
                    next.put(x, sum);
                }
                currentLayer.clear();
                tmp = currentLayer;
                currentLayer = next;
                next = tmp;
            }
        }
        double result = currentLayer.get(mdd.getRoot()) / divisor;
        Memory.free(currentLayer);
        Memory.free(next);
        return result;
    }

    /**
     * Return the maximum quantity of flow that can be put in pivot without going below the threshold.
     * The array X should be ordered by non increasing cost.
     * @param X The array of StochasticVariable ordered by non increasing cost
     * @param pivotPos The position of the pivot in the array X.
     * @param threshold The threshold we want to be ABOVE
     * @param maxQuantity The maximum quantity we can put
     * @param precision The precision used for the variables
     * @return The maximum quantity of flow that can be put in pivot without going below the threshold.
     */
    public static long upperbound(StochasticVariable[] X, int pivotPos, long threshold, long maxQuantity, int precision){
        // The array of distribution
        ArrayOfLong quantities = ArrayOfLong.create(X.length);
        double one = Math.pow(10, precision);
        // The current value of the distribution
        long currentValue = 0;

        StochasticVariable pivot = X[pivotPos];

        // Fill from largest to smallest value
        for(int i = 0; i < X.length; i++){
            if(maxQuantity <= 0) break;
            quantities.set(i, Math.min(maxQuantity, X[i].getMaxQuantity()));
            currentValue += quantities.get(i) * X[i].getMaxValue();
            // Remove from what is left what was added
            maxQuantity -= X[i].getMaxQuantity();
        }

        // The current value associated with the distribution
        currentValue = (long) Math.floor(currentValue / one);

        long quantity = quantities.get(pivotPos);
        // If we maxxed up the pivot and previous value
        if(quantity == pivot.getMaxQuantity()) {
            // If the current value is over the threshold, then it is okay
            if (currentValue >= threshold) return quantity;
            // Otherwise we can't get above because all values that are greater are already full
            else return -1;
        }

        // The amount that can be swapped
        long swappable = 0;
        // The value that will be swapped
        long swapValue = 0;

        for(int i = X.length - 1; i >= 0; i--){
            // Empty
            if(quantities.get(i) <= 0) continue;
            if(i == pivotPos) continue;

            // If we try to go up but we can only go down, stop
            if (currentValue <= threshold && pivot.getMaxValue() <= X[i].getMaxValue()) break;

            // Min between what I can take and what I can receive
            swappable = Math.min(quantities.get(i), pivot.getMaxQuantity() - quantity);
            if(swappable == 0) continue;

            // The value that will be removed (and possibly transfered)
            swapValue = (long) Math.floor((swappable * X[i].getMaxValue()) / one);

            long reserved = (currentValue - swapValue); // The value that is locked
            long minSwapValue = threshold - reserved;   // What must be reached

            // Compute the amount that we can swap between X[i] and the pivot
            long swap = pivot.maxSwappingQuantityMax(X[i], minSwapValue, swappable, precision);

            // Update the quantity distribution and the current value associated with the new distribution
            quantity += swap;
            currentValue += Math.floor(((pivot.getMaxValue() - X[i].getMaxValue()) * swap) / one);
            quantities.set(i, quantities.get(i) - swap);
            quantities.set(pivotPos, quantity);
        }

        // If the pivot is empty
        if(quantity < 0) return 0;

        return quantity;
    }

    /**
     * Return the maximum quantity of flow that can be put in pivot without exceeding the threshold.
     * The array X should be ordered by non increasing cost
     * @param X The array of StochasticVariable order by non increasing cost
     * @param pivotPos The position of the pivot in the array
     * @param threshold The threshold we want to be BELOW
     * @param maxQuantity The maximum quantity we can put
     * @param precision The precision used for the variables
     * @return The maximum quantity of flow that can be put in pivot without exceeding the threshold.
     */
    public static long upperboundMin(StochasticVariable[] X, int pivotPos, long threshold, long maxQuantity, int precision){
        // The array of distribution
        ArrayOfLong quantities = ArrayOfLong.create(X.length);
        double one = Math.pow(10, precision);

        return (long) Math.min(Math.floor((threshold * one) / X[pivotPos].getMinValue()), maxQuantity);
    }

    /**
     * Return the minimum quantity of flow that can be put in pivot without going below the threshold.
     * The array X should be ordered by non increasing cost.
     * @param X The array of StochasticVariable ordered by non increasing cost
     * @param pivotPos The position of the pivot in the array X.
     * @param threshold The threshold we want to be ABOVE
     * @param maxQuantity The maximum quantity we can put
     * @param precision The precision used for the variables
     * @return The minimum quantity of flow that can be put in pivot without going below the threshold.
     */
    public static long lowerbound(StochasticVariable[] X, int pivotPos, long threshold, long maxQuantity, int precision){
        // The array of distribution
        ArrayOfLong quantities = ArrayOfLong.create(X.length);
        double one = Math.pow(10, precision-1);
        threshold *= 10;
        // The current value of the distribution
        long currentValue = 0;
        // The position of the pivot in the array X
        StochasticVariable pivot = X[pivotPos];

        // Fill from largest to smallest value
        for(int i = 0; i < X.length; i++){
            if(maxQuantity <= 0) break;

            // Already a min value
            if(quantities.get(i) != 0) {
                // Compute what is added at most
                // We empty what is left OR put the quantity to the maximum
                quantities.set(i, Math.min(maxQuantity, X[i].getMaxQuantity()));
                currentValue += quantities.get(i) * X[i].getMaxValue();
            } else {
                quantities.set(i, Math.min(maxQuantity, X[i].getMaxQuantity()));
                currentValue += quantities.get(i) * X[i].getMaxValue();
            }
            maxQuantity -= X[i].getMaxQuantity();
        }

        currentValue = (long) Math.floor(currentValue / one);
        long quantity = quantities.get(pivotPos);
        // If we maxxed up until the pivot
        if(quantity == 0) {
            // If the smallest quantity is enough to go above the threshold
            if (currentValue >= threshold) return quantity;
                // Otherwise we can't possibly get higher, so impossible
            else return -1;
        }

        // The amount that can be swapped
        long swappable = 0;
        // The value that will be swapped
        long pivotValue = 0;

        for(int i = 0; i < X.length; i++){

            // Full
            if(quantities.get(i) >= X[i].getMaxQuantity()) continue;
            if(i == pivotPos) continue;
            if(quantity <= 0) break;

            // If we try to go up but we can only go down, stop
            if (currentValue <= threshold && pivot.getMaxValue() >= X[i].getMaxValue()) break;

            // The amount that we can swap is the min between what we can take and what we can receive
            swappable = Math.min(quantity, X[i].getMaxQuantity() - quantities.get(i));
            if(swappable == 0) continue;

            // The value that will be swapped
            pivotValue = (long) Math.floor((swappable * pivot.getMaxValue()) / one);

            // The value that is locked
            long reserved = (currentValue - pivotValue);
            // The value that we must reach by doing the swap
            long minSwapValue = threshold - reserved;

            long swap = X[i].maxSwappingQuantityMax(pivot, minSwapValue, swappable, precision-1);

            // Update the quantity distribution and the current value associated with the new distribution
            quantity -= swap;
            currentValue += Math.floor(((X[i].getMaxValue() - pivot.getMaxValue()) * swap) / one);
            quantities.set(i, quantities.get(i) + swap);
            quantities.set(pivotPos, quantity);
        }

        // Can remove some quantity
        if(currentValue > threshold && quantity > 0) {
            long exceed = currentValue - threshold;
            quantity = (long) Math.floor(quantity - (exceed * one) / pivot.getMaxValue());
        }

        return quantity;
    }

    /**
     * Compute all bounds of the StochasticVariables in X in a way that we respect the two thresholds.<br>
     * <b> /!\ The precision should be lower or equal to 8 /!\</b>
     * @param X The array of StochasticVariable ordered by non increasing cost
     * @param minThreshold The minimum threshold value
     * @param maxThreshold The maximum threshold value
     * @param precision The precision of the variables
     * @return The bounds respecting both thresholds.
     */
    public static long[][] computeBounds(StochasticVariable[] X, long minThreshold, long maxThreshold, int precision){
        long[][] bounds = new long[X.length][2];

        long one = (long) Math.pow(10, precision);

        minThreshold = (long) (minThreshold * one);
        maxThreshold = (long) (maxThreshold * one);

        // The amount we can distribute among the variables
        long maxQuantity = one;
        // The current value of the distribution
        long minValueMin = 0;
        long minValueMax = 0;
        // The position of the pivot in the array X
        int pivotPos = -1;

        // Set first all minimum quantity
        for(int i = 0; i < X.length; i++){
            if(maxQuantity <= 0) continue;
            if(X[i].getMinQuantity() == 0) continue;

            minValueMin += X[i].getMinQuantity() * X[i].getMinValue();
            minValueMax += X[i].getMinQuantity() * X[i].getMaxValue();
            maxQuantity -= X[i].getMinQuantity();
            X[i].setMaxQuantity(X[i].getMaxQuantity() - X[i].getMinQuantity());
        }

        // If we can't reach all minimum or we go over the max with min values : impossible
        if(maxQuantity < 0 || minValueMin >= maxThreshold) {
            for(int i = 0; i < X.length; i++){
                bounds[i][0] = -1;
                bounds[i][1] = -1;
            }
            return bounds;
        }

        long lb, ub;

        // Upperbound
        // lastFilled is the last variable that was filled
        // it means that all variable AFTER lastFilled already have their lowerbound (= 0), so no need to
        // compute it again
        long lastFilled = upperboundAll(X, bounds, minThreshold - minValueMax, maxQuantity, precision);
        lastFilled = lowerboundAll(X, bounds, minThreshold - minValueMax, maxQuantity, precision);

        // Compute the lowerbound of biggest value, up to lastFilled
        //for(int i = 0; i <= lastFilled; i++) bounds[i][0] = lowerbound(X, i, minThreshold - minValueMax, maxQuantity, precision);

        // Perform the upperbound with the <= threshold
        // The final upperbound is the lowest of both <= threshold and >= threshold
        for(int i = 0; i < X.length; i++) {
            ub = Math.min(bounds[i][1], upperboundMin(X, i, maxThreshold - minValueMin, maxQuantity, precision));
            bounds[i][1] = Math.min(ub, X[i].getMaxQuantity());
        }

        // Add the lowerbound that was removed
        for(int i = 0; i < X.length; i++) {
            bounds[i][0] += X[i].getMinQuantity();
            bounds[i][1] += X[i].getMinQuantity();
            X[i].setMaxQuantity(X[i].getMaxQuantity()+X[i].getMinQuantity());
        }

        return bounds;
    }

    /**
     * Compute all upperbounds of the variables in X in linear time.
     * This algorithm doesn't use a pivot.<br>
     * <b> /!\ The precision should be lower or equal to 8 /!\</b>
     * @param X The array of StochasticVariable ordered by non increasing cost
     * @param bounds The array containing all current bounds of variables in X (that will be updated)
     * @param threshold The minimum threshold value
     * @param totalQuantity The total amount of quantity we can put
     * @param precision The precision of the variables
     * @return The upperbounds respecting the given threshold.
     */
    public static long upperboundAll(StochasticVariable[] X, long[][] bounds, long threshold, long totalQuantity, int precision){
        int acceptableRoundingDifference = 2;
        // The array of distribution
        ArrayOfLong quantities = ArrayOfLong.create(X.length);
        double one = Math.pow(10, precision);
        // The current value of the distribution
        long currentValue = 0;

        long maxQuantity = totalQuantity;

        int lastFilled = -1;

        // Fill from largest to smallest value
        for(int i = 0; i < X.length; i++){
            if(maxQuantity <= 0) break;
            quantities.set(i, Math.min(maxQuantity, X[i].getMaxQuantity()));
            currentValue += quantities.get(i) * X[i].getMaxValue();
            // Remove from what is left what was added
            maxQuantity -= X[i].getMaxQuantity();
            lastFilled = i;
            if(quantities.get(i) == X[i].getMaxQuantity()) bounds[i][1] = X[i].getMaxQuantity();
            if(quantities.get(i) == totalQuantity) bounds[i][1] = totalQuantity;
        }


        // If the current value is below the threshold, then no solution
        if (currentValue < threshold){
            throw new IllegalArgumentException("The constraint is impossible to satisfy");
        }

        // The amount that can be swapped
        long swappable = 0;
        // The value that will be swapped
        long swapValue = 0;

        int lastComplete = lastFilled - 1;
        if (lastComplete == -1) lastComplete = lastFilled;

        for(int i = X.length - 1; i > lastComplete; i--){
            // Break
            if(lastFilled == -1) {
                // It means that we put everything we could in pivot
                for(int j = i; j > lastComplete; j--) bounds[j][1] = X[j].getMaxQuantity();
                break;
            }
            if(i==lastFilled) lastFilled--;
            // If we try to go up but we can only go down, stop
            //if (currentValue < threshold) continue;

            // While we didn't filled the variable or got to the lowerbound
            while(currentValue > threshold && X[i].getMaxQuantity() > quantities.get(i)) {
                // Min between what I can take and what I can receive
                swappable = Math.min(quantities.get(lastFilled), X[i].getMaxQuantity()- quantities.get(i));
                // if(swappable == 0) continue;
                // The value that will be removed (and possibly transfered)
                swapValue = swappable * X[lastFilled].getMaxValue();

                long reserved = (currentValue - swapValue); // The value that is locked
                long minSwapValue = threshold - reserved;   // What must be reached

                // Compute the amount that we can swap between X[i] and the pivot
                long swap = X[i].maxSwappingQuantityMax(X[lastFilled], minSwapValue, swappable, precision);

                if(swap == 0) break;

                // Update the quantity distribution and the current value associated with the new distribution
                currentValue += (X[i].getMaxValue() - X[lastFilled].getMaxValue()) * swap;
                quantities.set(i, quantities.get(i) + swap);
                quantities.set(lastFilled, quantities.get(lastFilled) - swap);
                if (quantities.get(lastFilled) == 0) lastFilled--;
                if (lastFilled == -1) break;
            }

            bounds[i][1] += quantities.get(i);
            int current = i;
            while (i-1 > lastComplete && X[i-1].getMaxQuantity() - quantities.get(i-1) <= bounds[current][1]) bounds[--i][1] = X[i].getMaxQuantity();
            if(i == 0) break;
            currentValue += (X[i-1].getMaxValue() - X[current].getMaxValue()) * quantities.get(current);
            quantities.set(i-1, quantities.get(current) + quantities.get(i-1));
            quantities.set(current, 0);
        }
        return lastComplete+1;
    }

    public static long lowerboundAll(StochasticVariable[] X, long[][] bounds, long threshold, long totalQuantity, int precision){
        int acceptableRoundingDifference = 2;
        // The array of distribution
        ArrayOfLong quantities = ArrayOfLong.create(X.length + 1);
        long one = (long) Math.pow(10, precision);
        // The current value of the distribution
        long currentValue = 0;

        long maxQuantity = totalQuantity;

        int lastFilled = -1;

        // Fill from largest to smallest value
        for(int i = 0; i < X.length; i++){
            if(maxQuantity <= 0) break;
            quantities.set(i, Math.min(maxQuantity, X[i].getMaxQuantity()));
            currentValue += quantities.get(i) * X[i].getMaxValue();
            // Remove from what is left what was added
            maxQuantity -= X[i].getMaxQuantity();
            lastFilled = i;
        }

        // If the current value is below the threshold, then no solution
        if (currentValue < threshold) {
            throw new IllegalArgumentException("The constraint is impossible to satisfy");
        }

        // The amount that can be swapped
        long swappable = 0;
        // The value that will be swapped
        long swapValue = 0;

        int lastComplete = lastFilled;
        if (lastComplete == -1) lastComplete = lastFilled;

        // Update max bounds ?
        for(int i = X.length - 1; i > lastFilled; i--) bounds[i][0] = 0;

        StochasticVariable pivot = X[lastFilled];
        StochasticVariable fake = StochasticVariable.create(0, (long) one, 0, 0);

        for(int i = 0; i <= lastComplete; i++){
            // Break
            if(i == lastFilled) lastFilled++;
            if(lastFilled == X.length) pivot = fake;
            else if(lastFilled > X.length) break;
            else pivot = X[lastFilled];

            // While we didn't filled the variable or got to the lowerbound
            while(currentValue > threshold && quantities.get(i) > 0) {
                // Min between what I can take and what I can receive
                swappable = Math.min(quantities.get(i), pivot.getMaxQuantity() - quantities.get(lastFilled));
                // if(swappable == 0) continue;
                // The value that will be removed (and possibly transfered)
                swapValue = swappable * X[i].getMaxValue();

                long reserved = (currentValue - swapValue); // The value that is locked
                long minSwapValue = threshold - reserved;   // What must be reached

                // Compute the amount that we can swap between X[i] and the pivot
                long swap = pivot.maxSwappingQuantityMax(X[i], minSwapValue, swappable, precision);

                if(swap == 0) break;

                // Update the quantity distribution and the current value associated with the new distribution
                currentValue += (pivot.getMaxValue() - X[i].getMaxValue()) * swap;
                quantities.set(lastFilled, quantities.get(lastFilled) + swap);
                quantities.set(i, quantities.get(i) - swap);
                if (quantities.get(lastFilled) == pivot.getMaxQuantity()) {
                    lastFilled++;
                    if(lastFilled == X.length) pivot = fake;
                    else if(lastFilled > X.length) break;
                    else pivot = X[lastFilled];
                }
            }

            bounds[i][0] = quantities.get(i);
            int current = i;
            while (i+1 <= lastComplete && quantities.get(i+1) <= X[current].getMaxQuantity() - bounds[current][0]) i++;
            if(i == X.length - 1) break;

            long preswap = X[current].getMaxQuantity() - quantities.get(current);
            currentValue += (X[current].getMaxValue() - X[i+1].getMaxValue()) * preswap;

            quantities.set(i+1, quantities.get(i+1) - preswap);
            quantities.set(current, X[current].getMaxQuantity());
        }
        return lastComplete+1;
    }

    /**
     * Compute the lower bounds of all variables' cost in linear time.<br>
     * /!\ The array X is sorted during the algorithm !
     * @param X The array of StochasticVariable to filter
     * @param threshold The minimum threshold
     * @param totalQuantity The maximum amount of quantity to give (default = 1)
     * @param precision The precision of the StochasticVariables
     * @return The array of filtered costs
     */
    public static ArrayOfLong minCostFiltering(StochasticVariable[] X, long threshold, long totalQuantity, int precision){
        long q, qt;

        threshold *= (long) Math.pow(10, precision);
        ArrayOfLong p = ArrayOfLong.create(X.length);
        ArrayOfLong bounds = ArrayOfLong.create(X.length);
        ArrayOfLong knapsack = ArrayOfLong.create(X.length);

        ArrayOfLong maxPack = maxPacking(X, knapsack, totalQuantity);
        long K = maxPack.get(0);
        int lastNonFull = (int) maxPack.get(1);
        Memory.free(maxPack);

        TupleOfInt idx = TupleOfInt.create();
        idx.setFirst(lastNonFull+1);

        ArrayOfBoolean indices = ArrayOfBoolean.create(lastNonFull+1);
        for(int i = 0; i < indices.length; i++) indices.set(i, true);

        // Sort by ci*pi
        sortByMaxProduct(X, knapsack, 0, lastNonFull);

        // While we did not filter all variables, we filter
        boolean stop = false;
        int cpt = 0;

        // Filtering variable that are empty in Knapsack
        for(int i = lastNonFull; i < X.length; i++){
            if(X[i].getMinQuantity() > 0) bounds.set(i, (threshold - K + X[i].getMaxValue() * X[i].getMinQuantity()) / X[i].getMinQuantity());
        }

        // Filtering non-empty
        while(!stop){
            // Reset the knapsack to each iteration
            p.copy(knapsack);
            stop = minCostFilteringStep(X, indices, p, bounds, lastNonFull, threshold, K, precision, idx);
        }



        // Filtering variable that can be empty but are full on knapsack
        if(idx.getFirst() < lastNonFull) {
            sortByQuantity(X, knapsack, idx.getFirst(), lastNonFull-1);
            int target = lastNonFull;
            long swap, qi = 0;
            for (int i = lastNonFull-1; i >= idx.getFirst(); i--) {
                // Remove the value of X[i] in K
                K -= knapsack.get(i) * X[i].getMaxValue();
                while (knapsack.get(i) > X[i].getMinQuantity() && target < X.length) {
                    // Swap between X[i] and variable with highest cost possible
                    swap = maxSwapping(X, knapsack, i, target);
                    knapsack.set(target, knapsack.get(target) + swap);
                    knapsack.set(i, knapsack.get(i) - swap);

                    qi += swap;

                    // Add the swapped value to K
                    K += X[target].getMaxValue() * swap;
                    // If the target is full, we move to the next one
                    if(knapsack.get(target) >= X[target].getMaxQuantity()) target++;
                }
                // Set the new lower bound
                if(knapsack.get(i) > 0) bounds.set(i, (threshold - K) / knapsack.get(i));
                if(i > 0) {
                    K += X[i].getMaxValue() * (qi + X[i].getMinQuantity());
                    K -= X[i-1].getMaxValue() * qi;
                    knapsack.set(i-1, knapsack.get(i-1)-qi);
                }
            }
        }

        sortByMaxCost(X, bounds, 0, lastNonFull);


        for(int i = 0; i < X.length; i++) {
            if(X[i].getMinValue() > bounds.get(i)) bounds.set(i, X[i].getMinValue());
            if(X[i].getMaxValue() < bounds.get(i)) bounds.set(i, X[i].getMinValue());
        }

        Memory.free(p);
        Memory.free(knapsack);
        Memory.free(idx);
        return bounds;
    }

    /**
     * One step of the minCostFiltering algorithm.
     * Filter all possible min cost such that the algorithm stays linear.
     * @param X The array of StochasticVariable to filter
     * @param indices The boolean array of variables that need to be filtered. indices[i] = true if the variable must be filtered.
     * @param p The quantity distribution (= knapsack)
     * @param lastNonFull The last non full variable (first variable to swap with)
     * @param threshold The minimum threshold
     * @param precision The precision of the variable
     * @return True if we filtered every variable in indices, false otherwise
     */
    private static boolean minCostFilteringStep(
            StochasticVariable[] X, ArrayOfBoolean indices, ArrayOfLong p, ArrayOfLong result, int lastNonFull,
            long threshold, long V, int precision, TupleOfInt idx) {
        long swap = 0;

        int target = lastNonFull;
        int i = 0;
        boolean skipped = false;
        while(i <= lastNonFull){
            // If the variable is already filtered
            if(!indices.get(i)) {
                i++;
                continue;
            }

            V -= (X[i].getMaxValue() * p.get(i));

            if(i == target) target++;
            // If above the threshold without the value, skip everything else ?
            if(V > threshold) {
                idx.setFirst(i);
                while (i <= lastNonFull) indices.set(i++, false);
                return !skipped;
            }
            while(p.get(i) > X[i].getMinQuantity() && target < X.length && X[i].worthSwappingWith(X[target], threshold, V, p.get(i), 1)){
                swap = maxSwapping(X, p, i, target);

                V += (swap * X[target].getMaxValue());
                p.set(target, p.get(target) + swap);
                p.set(i, p.get(i) - swap);

                // If glass is full, move to the next glass
                if(p.get(target) == X[target].getMaxQuantity()) target++;
            }

            if(p.get(i) > 0) result.set(i, (threshold - V) / p.get(i));

            indices.set(i, false);

            int current = i;
            i++;
            swap = maxSwapping(X, p, i, current);

            // Search the next variable to filter
            while (i <= lastNonFull) {
                if(!indices.get(i)) i++;
                else if(mustResetKnapsack(X, V, threshold, p.get(current), swap, p.get(i), current, i, lastNonFull)) {
                    swap = maxSwapping(X, p, i, current);
                    i++;
                    skipped = true;
                } else break;
            }

            p.set(current, p.get(current) + swap);
            p.set(i, p.get(i) - swap);
            V += (X[current].getMaxValue() * p.get(current));
        }
        Memory.free(p);
        return !skipped;
    }

    /**
     *
     * @param X The array of StochasticVariables to filter
     * @param V The value of the knapsack of X[current]
     * @param K The threshold
     * @param pi The quantity of X[current]
     * @param pnext The quantity of X[next]
     * @param swap The quantity swapped from next to current
     * @param one Representation of 1 in adapted precision
     * @param current The index of the current variable
     * @param next The index of the next variable
     * @param lastNonEmpty The last non empty glass filled by the knapsack
     * @return True if we must reset the knapsack to compute the min cost, false otherwise
     */
    private static boolean mustResetKnapsack(StochasticVariable[] X,
                                             long V, long K, long pi, long swap, long pnext,
                                             int current, int next, int lastNonEmpty){
        V = V + (X[current].getMaxValue() * (pi+swap) - X[next].getMaxValue() * pnext);
        return  X[next].getMaxQuantity() - X[next].getMinQuantity() < X[current].getMaxQuantity() - pi &&
                V < K &&
                X[next].worthSwappingWith(X[lastNonEmpty], K, V, pnext, 1);

    }

    /**
     * Compute the lower bounds of all variables' cost in polynomial time.<br>
     * <b> /!\ The precision should be lower or equal to 8 /!\</b>
     * @param X The array of StochasticVariable to filter
     * @param threshold The minimum threshold
     * @param totalQuantity The maximum amount of quantity to give
     * @param precision The precision of StochasticVariable
     */
    public static ArrayOfLong minCostFilteringPolynomial(StochasticVariable[] X, long threshold, long totalQuantity, int precision){
        ArrayOfLong maxPackingQuantities = ArrayOfLong.create(X.length);
        ArrayOfLong tmp = maxPacking(X, maxPackingQuantities, totalQuantity);
        ArrayOfLong bounds = ArrayOfLong.create(X.length);
        long totalCost = tmp.get(0);
        int firstNonFull = (int) tmp.get(1);
        threshold = (long) (threshold * Math.pow(10 , precision));

        long filteredActualQuantity;
        long newCost;
        int current;

        long newMinCost;

        for(int i = 0; i < X.length; i++) {
            filteredActualQuantity = maxPackingQuantities.get(i);
            //Total cost of max packing without X[i] and its given quantity
            newCost = totalCost - filteredActualQuantity * X[i].getMaxValue();
            current = firstNonFull;

            //If it is not possible to satisfy the threshold without the i-th StochasticVariable
            if (newCost < threshold) {
                //If the StochasticVariable can give quantity to another
                if (X[i].getMinQuantity() != maxPackingQuantities.get(i)) {
                    long swappableQuantity;

                    //If the current is the firstNonFull
                    if (i == current) current++;

                    //While it is worth to exchange with the current-th StochasticVariable
                    while (current < X.length && newCost + filteredActualQuantity * X[current].getMaxValue() > threshold) {
                        swappableQuantity = Math.min((filteredActualQuantity - X[i].getMinQuantity()), (X[current].getMaxQuantity() - maxPackingQuantities.get(current)));
                        newCost += swappableQuantity * X[current].getMaxValue();
                        filteredActualQuantity -= swappableQuantity;
                        current++;
                    }
                }
            }
            newMinCost = 0;
            if (filteredActualQuantity > 0) newMinCost = (long) Math.floor(((threshold  - newCost)) / filteredActualQuantity);
            if (newMinCost > X[i].getMaxValue()) throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");

            //Filtering
            if (newMinCost > X[i].getMinValue()) bounds.set(i, newMinCost);
            else bounds.set(i, X[i].getMinValue());
        }
        Memory.free(maxPackingQuantities);
        Memory.free(tmp);
        return bounds;
    }

    /**
     * Compute the lower bounds of all variables' cost in polynomial time.<br>
     * This version can be faster than the other.
     * <b> /!\ The precision should be lower or equal to 8 /!\</b>
     * @param X The array of StochasticVariable to filter
     * @param threshold The minimum threshold
     * @param totalQuantity The maximum amount of quantity to give
     * @param precision The precision of StochasticVariable
     */
    public static ArrayOfLong minCostFilteringPolynomialV2(StochasticVariable[] X, long threshold, long totalQuantity, int precision){
        ArrayOfLong maxPackingQuantities = ArrayOfLong.fastCreate(X.length);
        ArrayOfLong tmp = maxPacking(X, maxPackingQuantities, totalQuantity);
        ArrayOfLong bounds = ArrayOfLong.fastCreate(X.length);
        long totalCost = tmp.get(0);
        int firstNonFull = (int) tmp.get(1);
        threshold = (long) (threshold * Math.pow(10 , precision));

        long filteredActualQuantity;
        long newCost;
        int current;

        long swappableQuantity;

        long newMinCost;


        /*==================================================================
        The first step consists in filtering all the full StochasticVariable
        ==================================================================*/
        for (int i = 0; i < firstNonFull; i++) {
            filteredActualQuantity = maxPackingQuantities.get(i);
            //Total cost of max packing without X[i] and its given quantity
            newCost = totalCost - filteredActualQuantity * X[i].getMaxValue();
            current = firstNonFull;

            //If it is not possible to satisfy the threshold without the i-th StochasticVariable
            //and it is possible to exchange
            if (newCost < threshold && X[i].getMinQuantity() != maxPackingQuantities.get(i)) {
                //While it is worth to exchange with the current-th StochasticVariable
                while (current < X.length && newCost + filteredActualQuantity * X[current].getMaxValue() > threshold) {
                    swappableQuantity = Math.min((filteredActualQuantity - X[i].getMinQuantity()), (X[current].getMaxQuantity() - maxPackingQuantities.get(current)));
                    newCost += swappableQuantity * X[current].getMaxValue();
                    filteredActualQuantity -= swappableQuantity;
                    current++;
                }
            }

            newMinCost = 0;
            if (filteredActualQuantity > 0) newMinCost = (long) Math.floor(((threshold  - newCost)) / filteredActualQuantity);
            if (newMinCost > X[i].getMaxValue()) throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");

            //Filtering
            if (newMinCost > X[i].getMinValue()) bounds.set(i, newMinCost);
            else bounds.set(i, X[i].getMinValue());
        }

        /*=======================================================================
        The second step consists in filtering the firstNonFull StochasticVariable
        =======================================================================*/
        filteredActualQuantity = maxPackingQuantities.get(firstNonFull);
        //Total cost of max packing without X[i] and its given quantity
        newCost = totalCost - filteredActualQuantity * X[firstNonFull].getMaxValue();
        current = firstNonFull+1;

        //If it is not possible to satisfy the threshold without the firstNonFull StochasticVariable
        //and it is possible to exchange
        if (newCost < threshold && X[firstNonFull].getMinQuantity() != maxPackingQuantities.get(firstNonFull)) {
            //While it is worth to exchange with the current-th StochasticVariable
            while (current < X.length && newCost + filteredActualQuantity * X[current].getMaxValue() > threshold) {
                swappableQuantity = Math.min((filteredActualQuantity - X[firstNonFull].getMinQuantity()), (X[current].getMaxQuantity() - maxPackingQuantities.get(current)));
                newCost += swappableQuantity * X[current].getMaxValue();
                filteredActualQuantity -= swappableQuantity;
                current++;
            }
        }

        newMinCost = 0;
        if (filteredActualQuantity > 0) newMinCost = (long) Math.floor(((threshold  - newCost)) / filteredActualQuantity);
        if (newMinCost > X[firstNonFull].getMaxValue()) throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");

        //Filtering
        if (newMinCost > X[firstNonFull].getMinValue()) bounds.set(firstNonFull, newMinCost);
        else bounds.set(firstNonFull, X[firstNonFull].getMinValue());

        /*======================================================================================
        The third step consists in filtering the empty (fill until their min) StochasticVariable
        ======================================================================================*/
        for (int i = firstNonFull+1; i < X.length; i++) {
            filteredActualQuantity = maxPackingQuantities.get(i);
            //Total cost of max packing without X[i] and its given quantity
            newCost = totalCost - filteredActualQuantity * X[i].getMaxValue();

            //If it is not possible to satisfy the threshold without the i-th StochasticVariable
            //and there is quantity in it
            if (newCost < threshold && filteredActualQuantity > 0) {
                newMinCost = (long) Math.floor(((threshold  - newCost)) / filteredActualQuantity);
            }
            else newMinCost = 0;

            if (newMinCost > X[i].getMaxValue()) throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");


            //Filtering
            if (newMinCost > X[i].getMinValue()) bounds.set(i, newMinCost);
            else bounds.set(i, X[i].getMinValue());

        }

        Memory.free(maxPackingQuantities);
        Memory.free(tmp);
        return bounds;
    }

    /**
     * Compute the lower bounds of all variables' cost with a complexity n log(n).<br>
     * <b> /!\ The precision should be lower or equal to 8 /!\</b>
     * @param X The array of StochasticVariable to filter
     * @param threshold The minimum threshold
     * @param totalQuantity The maximum amount of quantity to give
     * @param precision The precision of StochasticVariable
     */
    public static ArrayOfLong minCostFilteringDichotomous(StochasticVariable[] X, long threshold, long totalQuantity, int precision){
        ArrayOfLong minBounds = ArrayOfLong.create(X.length);
        ArrayOfLong maxPackingQuantities = ArrayOfLong.create(X.length);
        ArrayOfLong tmp = maxPacking(X, maxPackingQuantities, totalQuantity);
        long totalCost = tmp.get(0);
        int firstNonFull = (int) tmp.get(1);
        threshold = (long) (threshold * Math.pow(10 , precision));

        if(totalCost < threshold){
            throw new IllegalArgumentException("The constraint is impossible to satisfy (threshold : "+((long) (threshold / Math.pow(10, precision)))+", max packing total cost : "+((long) (totalCost / Math.pow(10, precision)))+")");
        }


        //If there is no swapping possible
        if(firstNonFull >= X.length){
            long newMinCost;
            for(int i = 0; i < X.length; i++) {
                newMinCost = (long) Math.floor((threshold - (totalCost - X[i].getMaxQuantity() * X[i].getMaxValue())) / (X[i].getMaxQuantity()));
                if (newMinCost > X[i].getMinValue()) minBounds.set(i, newMinCost);
                else minBounds.set(i, X[i].getMinValue());
            }
        }
        else {
            /*First we create two arrays in order to do the dichotomous search.
            The two arrays only concern the non-full StochasticVariable (from firstNonFull to the end).
            The first array contains the quantity needed if we fill all the StochasticVariable
            from firstNonFull to the i-th (not included).
            The other one contains the cost reached if we fill all the StochasticVariable
            from firstNonFull to the i-th (not included).
            */
            int dichotomousLength = X.length - firstNonFull;
            ArrayOfLong quantityNeeded = ArrayOfLong.create(dichotomousLength);
            ArrayOfLong costReached = ArrayOfLong.create(dichotomousLength);

            quantityNeeded.set(0, 0);
            costReached.set(0, 0);
            long addedQuantity;

            for (int i = 1; i < quantityNeeded.length(); i++) {
                addedQuantity = X[firstNonFull+i-1].getMaxQuantity() - maxPackingQuantities.get(firstNonFull+i-1);
                quantityNeeded.set(i, quantityNeeded.get(i-1) + addedQuantity);
                costReached.set(i, costReached.get(i-1) + addedQuantity * X[firstNonFull+i-1].getMaxValue());
            }

            //Then we filter the StochasticVariable
            long filteredActualQuantity;
            long newCost;
            int indexMin;
            int indexMax;
            int newIndex;

            long newMinCost;

            for(int i = 0; i < X.length; i++) {
                filteredActualQuantity = maxPackingQuantities.get(i);
                //Total cost of max packing without X[i] and its given quantity
                newCost = totalCost - filteredActualQuantity * X[i].getMaxValue();
                indexMin = 0;
                indexMax = quantityNeeded.length() - 1;
                newIndex = (indexMin + indexMax + 1) / 2;

                //If it is not possible to satisfy the threshold without the i-th StochasticVariable
                if (newCost < threshold){
                    //If the StochasticVariable can give quantity to another
                    if (X[i].getMinQuantity() != filteredActualQuantity) {
                        //NonFullNonEmpty
                        long nfneQuantity = 0;
                        long nfneCost = 0;
                        //If we filter the first non-full StochasticVariable, then we don't take
                        //it in account in the arrays of the dichotomous search
                        if (i == firstNonFull){
                            nfneQuantity  = X[i].getMaxQuantity() - filteredActualQuantity;
                            nfneCost = nfneQuantity * X[i].getMaxValue();
                            indexMin++;
                        }

                        //If it is worthless to do exchange
                        if( i == firstNonFull && (i + 1 >= X.length || threshold >= newCost + maxPackingQuantities.get(i) * X[i+1].getMaxValue())
                                || threshold >= newCost + (X[i].getMaxQuantity()) * X[firstNonFull].getMaxValue()){
                            newMinCost = (long) Math.floor(((threshold - newCost)) / filteredActualQuantity);
                        }
                        else {
                            while (indexMin < indexMax) {
                                if (quantityNeeded.get(newIndex) - nfneQuantity >= filteredActualQuantity - X[i].getMinQuantity()) {
                                    indexMax = newIndex - 1;
                                } else {
                                /*If it is possible to satisfy the threshold by giving the quantity of the
                                i-th StochasticVariable to all StochasticVariable until newIndex (and to put
                                all quantity in newIndex, without respecting the max quantity of newIndex
                                and the min quantity of the i-th StochasticVariable
                                 */
                                    if (threshold <= newCost + (costReached.get(newIndex) - nfneCost) + (filteredActualQuantity - (quantityNeeded.get(newIndex) - nfneQuantity)) * X[firstNonFull + newIndex].getMaxValue()) {
                                        indexMin = newIndex;
                                    } else {
                                        indexMax = newIndex - 1;
                                    }
                                }
                                newIndex = (indexMin + indexMax + 1) / 2;
                            }

                            long swappableQuantity;
                            swappableQuantity = Math.min(filteredActualQuantity - X[i].getMinQuantity() - (quantityNeeded.get(indexMin) - nfneQuantity), X[firstNonFull + indexMin].getMaxQuantity() - maxPackingQuantities.get(firstNonFull + indexMin));
                            if ((filteredActualQuantity - (quantityNeeded.get(indexMin) - nfneQuantity) - swappableQuantity) != 0) {
                                newMinCost = (long) Math.floor((threshold - (newCost + (costReached.get(indexMin) - nfneCost) + swappableQuantity * X[firstNonFull + indexMin].getMaxValue())) / (filteredActualQuantity - (quantityNeeded.get(indexMin) - nfneQuantity) - swappableQuantity));
                            } else {
                                newMinCost = 0;
                            }
                        }
                    }
                    //If there is no quantity to give
                    else {newMinCost = (long) Math.floor(((threshold - newCost)) / filteredActualQuantity);
                    }
                }
                //If it is possible to satisfy the threshold without the i-th StochasticVariable
                else {newMinCost = 0;}
                //Filtering
                if (newMinCost > X[i].getMinValue()) minBounds.set(i, newMinCost);
                else minBounds.set(i, X[i].getMinValue());
                if (newMinCost > X[i].getMaxValue()){
                    throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");
                }
            }
            Memory.free(costReached);
            Memory.free(quantityNeeded);
        }
        Memory.free(maxPackingQuantities);
        Memory.free(tmp);
        return minBounds;
    }

    /**
     * Compute the lower bounds of all variables' cost with a complexity n log(n).<br>
     * This version can be faster than the other.
     * <b> /!\ The precision should be lower or equal to 8 /!\</b>
     * @param X The array of StochasticVariable to filter
     * @param threshold The minimum threshold
     * @param totalQuantity The maximum amount of quantity to give
     * @param precision The precision of StochasticVariable
     */
    public static ArrayOfLong minCostFilteringDichotomousV2(StochasticVariable[] X, long threshold, long totalQuantity, int precision){
        ArrayOfLong minBounds = ArrayOfLong.fastCreate(X.length);
        ArrayOfLong maxPackingQuantities = ArrayOfLong.fastCreate(X.length);
        ArrayOfLong tmp = maxPacking(X, maxPackingQuantities, totalQuantity);
        long totalCost = tmp.get(0);
        int firstNonFull = (int) tmp.get(1);
        threshold = (long) (threshold * Math.pow(10 , precision));

        if(totalCost < threshold){
            throw new IllegalArgumentException("The constraint is impossible to satisfy (threshold : "+((long) (threshold / Math.pow(10, precision)))+", max packing total cost : "+((long) (totalCost / Math.pow(10, precision)))+")");
        }

        //If there is no swapping possible
        if(firstNonFull >= X.length){
            long newMinCost;
            for(int i = 0; i < X.length; i++) {
                newMinCost = (long) Math.floor((threshold - (totalCost - X[i].getMaxQuantity() * X[i].getMaxValue())) / (X[i].getMaxQuantity()));
                if (newMinCost > X[i].getMinValue()) minBounds.set(i, newMinCost);
                else minBounds.set(i, X[i].getMinValue());
            }
        }

        else {
            /*First we create two arrays in order to do the dichotomous search.
            The two arrays only concern the non-full StochasticVariable (from firstNonFull to the end).
            The first array contains the quantity needed if we fill all the StochasticVariable
            from firstNonFull to the i-th (not included).
            The other one contains the cost reached if we fill all the StochasticVariable
            from firstNonFull to the i-th (not included).
            */
            int dichotomousLength = X.length - firstNonFull;
            ArrayOfLong quantityNeeded = ArrayOfLong.fastCreate(dichotomousLength);
            ArrayOfLong costReached = ArrayOfLong.fastCreate(dichotomousLength);

            quantityNeeded.set(0, 0);
            costReached.set(0, 0);
            long addedQuantity;

            for (int i = 1; i < quantityNeeded.length(); i++) {
                addedQuantity = X[firstNonFull+i-1].getMaxQuantity() - maxPackingQuantities.get(firstNonFull+i-1);
                quantityNeeded.set(i, quantityNeeded.get(i-1) + addedQuantity);
                costReached.set(i, costReached.get(i-1) + addedQuantity * X[firstNonFull+i-1].getMaxValue());
            }

            //Then we filter the StochasticVariable
            long filteredActualQuantity;
            long newCost;
            int indexMin;
            int indexMax;
            int newIndex;

            long swappableQuantity;

            long newMinCost;

            /*==================================================================
            The first step consists in filtering all the full StochasticVariable
            ==================================================================*/
            for(int i = 0; i < firstNonFull; i++) {
                filteredActualQuantity = maxPackingQuantities.get(i);
                //Total cost of max packing without X[i] and its given quantity
                newCost = totalCost - filteredActualQuantity * X[i].getMaxValue();
                indexMin = 0;
                indexMax = quantityNeeded.length() - 1;
                newIndex = (indexMin + indexMax + 1) / 2;

                //If it is not possible to satisfy the threshold without the i-th StochasticVariable
                if (newCost < threshold){

                    //If it is worthless to do exchange or it is not possible
                    if (threshold >= newCost + (X[i].getMaxQuantity()) * X[firstNonFull].getMaxValue()
                            || X[i].getMinQuantity() == filteredActualQuantity){
                        newMinCost = (long) Math.floor(((threshold - newCost)) / filteredActualQuantity);
                    }

                    //If it is worth to do the exchange, we have to search until where
                    else {

                        //Dichotomous search
                        while (indexMin < indexMax) {
                            if (quantityNeeded.get(newIndex) >= filteredActualQuantity - X[i].getMinQuantity()) {
                                indexMax = newIndex - 1;
                            } else {
                                /* If it is possible to satisfy the threshold by giving the quantity of the
                                i-th StochasticVariable to all StochasticVariable until newIndex (and to put
                                all quantity in newIndex, without respecting the max quantity of newIndex
                                and the min quantity of the i-th StochasticVariable
                                 */
                                if (threshold <= newCost + (costReached.get(newIndex)) + (filteredActualQuantity - (quantityNeeded.get(newIndex))) * X[firstNonFull + newIndex].getMaxValue()) {
                                    indexMin = newIndex;
                                } else {
                                    indexMax = newIndex - 1;
                                }
                            }
                            newIndex = (indexMin + indexMax + 1) / 2;
                        }
                        //End of the dichotomous search

                        swappableQuantity = Math.min(filteredActualQuantity - X[i].getMinQuantity() - (quantityNeeded.get(indexMin)), X[firstNonFull + indexMin].getMaxQuantity() - maxPackingQuantities.get(firstNonFull + indexMin));
                        if ((filteredActualQuantity - (quantityNeeded.get(indexMin)) - swappableQuantity) != 0) {
                            newMinCost = (long) Math.floor((threshold - (newCost + (costReached.get(indexMin)) + swappableQuantity * X[firstNonFull + indexMin].getMaxValue())) / (filteredActualQuantity - (quantityNeeded.get(indexMin)) - swappableQuantity));
                        } else {
                            newMinCost = 0;
                        }
                    }
                }

                //If it is possible to satisfy the threshold without the i-th StochasticVariable
                else {newMinCost = 0;}

                //Filtering
                if (newMinCost > X[i].getMinValue()) minBounds.set(i, newMinCost);
                else minBounds.set(i, X[i].getMinValue());
                if (newMinCost > X[i].getMaxValue()){
                    throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");
                }
            }

            /*=======================================================================
            The second step consists in filtering the firstNonFull StochasticVariable
            =======================================================================*/
            filteredActualQuantity = maxPackingQuantities.get(firstNonFull);
            //Total cost of max packing without X[i] and its given quantity
            newCost = totalCost - filteredActualQuantity * X[firstNonFull].getMaxValue();
            indexMin = 0;
            indexMax = quantityNeeded.length() - 1;
            newIndex = (indexMin + indexMax + 1) / 2;

            //If it is not possible to satisfy the threshold without the i-th StochasticVariable
            if (newCost < threshold){

                //If it is worthless to do exchange or it is not possible
                if (firstNonFull + 1 >= X.length
                        || threshold >= newCost + maxPackingQuantities.get(firstNonFull) * X[firstNonFull+1].getMaxValue()
                        || X[firstNonFull].getMinQuantity() == filteredActualQuantity){
                    newMinCost = (long) Math.floor(((threshold - newCost)) / filteredActualQuantity);
                }

                //If it is worth to do the exchange, we have to search until where
                else {

                    long nfneQuantity  = X[firstNonFull].getMaxQuantity() - filteredActualQuantity;
                    long nfneCost = nfneQuantity * X[firstNonFull].getMaxValue();
                    indexMin++;

                    //Dichotomous search
                    while (indexMin < indexMax) {
                        if (quantityNeeded.get(newIndex) - nfneQuantity >= filteredActualQuantity - X[firstNonFull].getMinQuantity()) {
                            indexMax = newIndex - 1;
                        } else {
                                /*If it is possible to satisfy the threshold by giving the quantity of the
                                i-th StochasticVariable to all StochasticVariable until newIndex (and to put
                                all quantity in newIndex, without respecting the max quantity of newIndex
                                and the min quantity of the i-th StochasticVariable
                                 */
                            if (threshold <= newCost + (costReached.get(newIndex) - nfneCost) + (filteredActualQuantity - (quantityNeeded.get(newIndex) - nfneQuantity)) * X[firstNonFull + newIndex].getMaxValue()) {
                                indexMin = newIndex;
                            } else {
                                indexMax = newIndex - 1;
                            }
                        }
                        newIndex = (indexMin + indexMax + 1) / 2;
                    }
                    //End of the dichotomous search

                    swappableQuantity = Math.min(filteredActualQuantity - X[firstNonFull].getMinQuantity() - (quantityNeeded.get(indexMin) - nfneQuantity), X[firstNonFull + indexMin].getMaxQuantity() - maxPackingQuantities.get(firstNonFull + indexMin));
                    if ((filteredActualQuantity - (quantityNeeded.get(indexMin) - nfneQuantity) - swappableQuantity) != 0) {
                        newMinCost = (long) Math.floor((threshold - (newCost + (costReached.get(indexMin) - nfneCost) + swappableQuantity * X[firstNonFull + indexMin].getMaxValue())) / (filteredActualQuantity - (quantityNeeded.get(indexMin) - nfneQuantity) - swappableQuantity));
                    } else {
                        newMinCost = 0;
                    }
                }
            }

            //If it is possible to satisfy the threshold without the i-th StochasticVariable
            else {newMinCost = 0;}

            //Filtering
            if (newMinCost > X[firstNonFull].getMinValue()) minBounds.set(firstNonFull, newMinCost);
            else minBounds.set(firstNonFull, X[firstNonFull].getMinValue());
            if (newMinCost > X[firstNonFull].getMaxValue()){
                throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");
            }

            /*======================================================================================
            The third step consists in filtering the empty (fill until their min) StochasticVariable
            ======================================================================================*/
            for (int i = firstNonFull+1; i < X.length; i++) {
                filteredActualQuantity = maxPackingQuantities.get(i);
                //Total cost of max packing without X[i] and its given quantity
                newCost = totalCost - filteredActualQuantity * X[i].getMaxValue();

                //If it is not possible to satisfy the threshold without the i-th StochasticVariable
                if (newCost < threshold){
                    newMinCost = (long) Math.floor(((threshold - newCost)) / filteredActualQuantity);
                }

                //If it is possible to satisfy the threshold without the i-th StochasticVariable
                else {newMinCost = 0;}

                //Filtering
                if (newMinCost > X[i].getMinValue()) minBounds.set(i, newMinCost);
                else minBounds.set(i, X[i].getMinValue());
                if (newMinCost > X[i].getMaxValue()){
                    throw new IllegalArgumentException("The minimal cost is greater than the maximal cost");
                }
            }

            Memory.free(costReached);
            Memory.free(quantityNeeded);
        }
        Memory.free(maxPackingQuantities);
        Memory.free(tmp);
        return minBounds;
    }

    /**
     * Get the maximum quantity we can swap between X[i] and X[t].
     * @param X The array of StochasticVariables
     * @param p The quantity distribution
     * @param i The index of the giver variable
     * @param t The index of the receiver variable
     * @return The maximum quantity we can swap between X[i] and X[t].
     */
    private static long maxSwapping(StochasticVariable[] X, ArrayOfLong p, int i, int t){
        // What we can take
        long qi = p.get(i) - X[i].getMinQuantity();
        // What we can receive
        long qt = X[t].getMaxQuantity() - p.get(t);
        // If we can take more than we can receive, the take only what we can receive
        return qi > qt ? qt : qi;
    }

    /**
     * Sort the array X by non-increasing order of the value cmax[i] * p[i].
     * @param X The array of StochasticVariables
     * @param p The quantity distribution
     * @param k The index of the last variable to sort (included)
     */
    private static void sortByMaxProduct(StochasticVariable X[], ArrayOfLong p, int start, int end){
        ArrayOfLong cipi = ArrayOfLong.create(end+1-start);
        for(int i = start; i <= end; i++) cipi.set(i-start, X[i].getMaxValue() * X[i].getMaxQuantity());

        for(int i = start; i < cipi.length+start; i++){
            int j = i;
            StochasticVariable x = X[i];
            long pi = p.get(i);
            long v = cipi.get(i-start);
            while(j > 0 && cipi.get(j-1-start) < v) {
                cipi.set(j-start, cipi.get(j-1-start));
                X[j] = X[j-1];
                p.set(j, p.get(j-1));
                j -= 1;
            }
            cipi.set(j-start, v);
            X[j] = x;
            p.set(j, pi);
        }

        Memory.free(cipi);
    }

    /**
     * Sort the array X by non-increasing order of the value cmax[i].
     * @param X The array of StochasticVariables
     * @param p The quantity distribution
     * @param k The index of the last variable to sort (included)
     */
    private static void sortByMaxCost(StochasticVariable X[], ArrayOfLong p, int start, int end){
        for(int i = start; i <= end; i++){
            int j = i;
            StochasticVariable x = X[i];
            long pi = p.get(i);
            long v = X[i].getMaxValue();
            while(j > 0 && X[j-1].getMaxValue() < v) {
                X[j] = X[j-1];
                p.set(j, p.get(j-1));
                j -= 1;
            }
            X[j] = x;
            p.set(j, pi);
        }
    }

    /**
     * Sort the array X by non-increasing order of the value qmax[i] - qmin[i].
     * @param X The array of StochasticVariables
     * @param p The quantity distribution
     * @param k The index of the last variable to sort (included)
     */
    private static void sortByQuantity(StochasticVariable X[], ArrayOfLong p, int start, int end){
        for(int i = start; i <= end; i++){
            int j = i;
            StochasticVariable x = X[i];
            long qi = p.get(i);
            long v = X[i].getMaxQuantity() - X[i].getMinQuantity();
            while(j > 0 && X[j-1].getMaxQuantity() - X[j-1].getMinQuantity() < v) {
                X[j] = X[j-1];
                p.set(j, p.get(j-1));
                j -= 1;
            }
            X[j] = x;
            p.set(j, qi);
        }
    }

    /**
     * Distribute the quantity in order to get the maximal total cost of
     * an array of StochasticVariables, and put that distribution in the
     * the array quantities. <br>
     * <b> /!\ The total cost returned has a precision * 2 /!\</b>
     * @param X The array of StochasticVariables
     * @param quantities The array which will get the distribution
     * @param totalQuantity The quantity that can be distributed
     * @return An array containing : <br>
     * -Index 0 : the maximal total cost (<b> /!\ precision * 2 /!\ </b>) <br>
     * -Index 1 : the position of the first non-full SotchasticVariable
     */
    public static ArrayOfLong maxPacking(StochasticVariable[] X, ArrayOfLong quantities, long totalQuantity){
        if(X.length > quantities.length()){
            throw new ArrayIndexOutOfBoundsException("The array storing the results is too small for the given array of StochasticVariable");
        }

        long availableQuantity = totalQuantity;
        long totalCost = 0;

        //First we fill the minimal values
        for(int i = 0; i < X.length; i++){
            quantities.set(i, X[i].getMinQuantity());
            totalCost += X[i].getMinQuantity() * X[i].getMaxValue();
            availableQuantity -= X[i].getMinQuantity();
            if (availableQuantity < 0){
                throw new IllegalArgumentException("There is no enough quantity to fill the min");
            }
        }

        long tmp = totalCost;

        //Then we put the remaining quantity, starting by the highest values
        int current = 0;
        while(availableQuantity > 0 && current < X.length){
            long swappableQuantity = Math.min(availableQuantity, (X[current].getMaxQuantity() - X[current].getMinQuantity()));
            quantities.set(current, quantities.get(current) + swappableQuantity);
            totalCost += swappableQuantity * X[current].getMaxValue();
            availableQuantity -= swappableQuantity;
            current++;
        }

        //If the previous StochasticVariable is not full
        if(current > 0 && quantities.get(current - 1) < X[current - 1].getMaxQuantity()) current--;

        ArrayOfLong tuple = ArrayOfLong.create(2);
        tuple.set(0, totalCost);
        tuple.set(1, current);

        return tuple;
    }
}
