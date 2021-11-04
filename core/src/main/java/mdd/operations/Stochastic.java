package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import structures.StochasticVariable;
import structures.arrays.ArrayOfLong;
import structures.generics.MapOf;
import utils.SmallMath;

import java.util.Arrays;

public class Stochastic {

    /**
     * Given a MDD and a map associating labels to probabilities (by layer), compute
     * the probability of the whole MDD.
     * @param mdd The MDD
     * @param P The associations labels -> probabilities
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
     * Compute all bounds of the StochasticVariables in X in a way that we respect the two thresholds.
     * @param X The array of StochasticVariable ordered by non increasing cost
     * @param minThreshold The minimum threshold value >=
     * @param maxThreshold The maximum threshold value <=
     * @param precision The precision of the variables
     * @return The bounds respecting both thresholds.
     */
    public static long[][] computeBounds(StochasticVariable[] X, long minThreshold, long maxThreshold, int precision){
        long[][] bounds = new long[X.length][2];

        double one = Math.pow(10, precision);
        // The amount we can distribute among the variables
        long maxQuantity = (long) one;
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


        minValueMin = (long) Math.floor(minValueMin / one);
        minValueMax = (long) Math.floor(minValueMax / one);

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
            bounds[i][1] = ub;
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
     * This algorithm doesn't use a pivot.
     * @param X The array of StochasticVariable ordered by non increasing cost
     * @param bounds The array containing all current bounds of variables in X (that will be updated)
     * @param threshold The minimum threshold value >=
     * @param totalQuantity The total amount of quantity we can put
     * @param precision The precision of the variables
     * @return The upperbounds respecting the given threshold.
     */
    public static long upperboundAll(StochasticVariable[] X, long[][] bounds, long threshold, long totalQuantity, int precision){
        int acceptableRoundingDifference = 2;
        // The array of distribution
        ArrayOfLong quantities = ArrayOfLong.create(X.length);
        double one = Math.pow(10, precision - 1);
        threshold *= 10;
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

        // The current value associated with the distribution
        currentValue = (long) Math.floor(currentValue / one);

        // If the current value is below the threshold, then no solution
        if (currentValue < threshold) return -1;

        // The amount that can be swapped
        long swappable = 0;
        // The value that will be swapped
        long swapValue = 0;

        int lastComplete = lastFilled - 1;
        if (lastComplete == -1) lastComplete = lastFilled;

        for(int i = X.length - 1; i > lastComplete; i--){
            // Break
            if(lastFilled == -1) break;
            if(i==lastFilled) lastFilled--;
            // If we try to go up but we can only go down, stop
            //if (currentValue < threshold) continue;

            // While we didn't filled the variable or got to the lowerbound
            while(currentValue > threshold + acceptableRoundingDifference && X[i].getMaxQuantity() > quantities.get(i)) {
                // Min between what I can take and what I can receive
                swappable = Math.min(quantities.get(lastFilled), X[i].getMaxQuantity()- quantities.get(i));
                // if(swappable == 0) continue;
                // The value that will be removed (and possibly transfered)
                swapValue = (long) Math.floor((swappable * X[lastFilled].getMaxValue()) / one);

                long reserved = (currentValue - swapValue); // The value that is locked
                long minSwapValue = threshold - reserved;   // What must be reached

                // Compute the amount that we can swap between X[i] and the pivot
                long swap = X[i].maxSwappingQuantityMax(X[lastFilled], minSwapValue, swappable, precision-1);

                // Update the quantity distribution and the current value associated with the new distribution
                currentValue += Math.floor(((X[i].getMaxValue() - X[lastFilled].getMaxValue()) * swap) / one);
                quantities.set(i, quantities.get(i) + swap);
                quantities.set(lastFilled, quantities.get(lastFilled) - swap);
                if (quantities.get(lastFilled) == 0) lastFilled--;

            }

            bounds[i][1] += quantities.get(i);
            int current = i;
            while (i-1 > lastComplete && X[i-1].getMaxQuantity() - quantities.get(i-1) <= bounds[current][1]) bounds[--i][1] = X[i].getMaxQuantity();
            if(i == 0) break;
            currentValue += Math.floor(((X[i-1].getMaxValue() - X[current].getMaxValue()) * quantities.get(current)) / one);
            quantities.set(i-1, quantities.get(current) + quantities.get(i-1));
            quantities.set(current, 0);
        }
        return lastComplete+1;
    }


    public static long lowerboundAll(StochasticVariable[] X, long[][] bounds, long threshold, long totalQuantity, int precision){
        int acceptableRoundingDifference = 2;
        // The array of distribution
        ArrayOfLong quantities = ArrayOfLong.create(X.length + 1);
        double one = Math.pow(10, precision - 1);
        threshold *= 10;
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

        // The current value associated with the distribution
        currentValue = (long) Math.floor(currentValue / one);

        // If the current value is below the threshold, then no solution
        if (currentValue < threshold) return -1;

        // The amount that can be swapped
        long swappable = 0;
        // The value that will be swapped
        long swapValue = 0;

        int lastComplete = lastFilled;
        if (lastComplete == -1) lastComplete = lastFilled;

        // Update max bounds ?
        for(int i = X.length - 1; i > lastFilled; i--) bounds[i][0] = 0;

        StochasticVariable pivot = X[lastFilled];
        StochasticVariable fake = StochasticVariable.create(0, (long) one * 10, 0, 0);

        for(int i = 0; i <= lastComplete; i++){
            // Break
            if(i == lastFilled) lastFilled++;
            if(lastFilled == X.length) pivot = fake;
            else if(lastFilled > X.length) break;
            else pivot = X[lastFilled];

            // While we didn't filled the variable or got to the lowerbound
            while(currentValue > threshold + acceptableRoundingDifference && quantities.get(i) > 0) {
                // Min between what I can take and what I can receive
                swappable = Math.min(quantities.get(i), pivot.getMaxQuantity() - quantities.get(lastFilled));
                // if(swappable == 0) continue;
                // The value that will be removed (and possibly transfered)
                swapValue = (long) Math.floor((swappable * X[i].getMaxValue()) / one);

                long reserved = (currentValue - swapValue); // The value that is locked
                long minSwapValue = threshold - reserved;   // What must be reached

                // Compute the amount that we can swap between X[i] and the pivot
                long swap = pivot.maxSwappingQuantityMax(X[i], minSwapValue, swappable, precision-1);

                // Update the quantity distribution and the current value associated with the new distribution
                currentValue += Math.floor(((pivot.getMaxValue() - X[i].getMaxValue()) * swap) / one);
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
            currentValue += Math.floor(((X[current].getMaxValue() - X[i+1].getMaxValue()) * preswap) / one);

            quantities.set(i+1, quantities.get(i+1) - preswap);
            quantities.set(current, X[current].getMaxQuantity());
        }
        return lastComplete+1;
    }

}
