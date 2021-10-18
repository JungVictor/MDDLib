package mdd.operations;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import structures.StochasticVariable;
import structures.arrays.ArrayOf;
import structures.arrays.ArrayOfLong;
import structures.generics.MapOf;
import utils.SmallMath;

public class Stochastic {

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
     * Return the maximum quantity of flow that can be put in pivot.
     * The array X should be ordered by non increasing cost.
     * @param X The array of StochasticVariable ordered by non increasing cost
     * @param pivot The StochasticVariable we use as pivot
     * @param threshold The threshold we want to be ABOVE
     * @param precision The precision used for the variables
     */
    public static long upperbound(StochasticVariable[] X, StochasticVariable pivot, long threshold, int precision){
        ArrayOfLong quantities = ArrayOfLong.create(X.length);
        long one = (long) Math.pow(10, precision);
        long maxQuantity = one;
        long currentValue = 0;
        int pivotPos = -1;
        long lowerboundSum = 0;

        // Set first all minimum quantity
        for(int i = 0; i < X.length; i++){
            if(X[i] == pivot) pivotPos = i;
            if(maxQuantity <= 0) continue;
            if(X[i].getMinQuantity() == 0) continue;

            quantities.set(i, Math.min(maxQuantity, X[i].getMinQuantity()));
            currentValue += (quantities.get(i) * X[i].getMaxValue()) / one;
            maxQuantity -= X[i].getMinQuantity();
            lowerboundSum += X[i].getMinQuantity();
        }

        // Fill from largest to smallest value
        for(int i = 0; i < X.length; i++){
            if(X[i] == pivot) pivotPos = i;
            if(maxQuantity <= 0) continue;
            // Already a min value
            if(quantities.get(i) != 0) {
                quantities.set(i, Math.min(maxQuantity+X[i].getMinQuantity(), X[i].getMaxQuantity()));
                currentValue += ((quantities.get(i) - X[i].getMinQuantity()) * X[i].getMaxValue()) / one;
            } else {
                quantities.set(i, Math.min(maxQuantity, X[i].getMaxQuantity()));
                currentValue += (quantities.get(i) * X[i].getMaxValue()) / one;
            }
            maxQuantity -= X[i].getMaxQuantity() - X[i].getMinQuantity();
        }

        long quantity = quantities.get(pivotPos);
        // If we maxxed up until the pivot
        if(quantity == pivot.getMaxQuantity() - lowerboundSum + pivot.getMinQuantity()) {
            if (currentValue >= threshold) return quantity;
            else return -1;
        }

        long swappable = pivot.getMaxQuantity() - quantity;
        long pivotValue = 0;
        long swapValue = 0;


        for(int i = X.length - 1; i >= 0; i--){
            // Empty
            if(quantities.get(i) <= 0) continue;
            if(i == pivotPos) continue;

            // If we try to go up but we can only go down, stop
            if (currentValue <= threshold && pivot.getMaxValue() <= X[i].getMaxValue()) break;

            // Min between what I can take and what I can receive
            swappable = Math.min(quantities.get(i) - X[i].getMinQuantity(), pivot.getMaxQuantity() - quantity);
            if(swappable == 0) continue;

            pivotValue = ((quantity - pivot.getMinQuantity()) * pivot.getMaxValue()) / one;
            swapValue = (swappable * X[i].getMaxValue()) / one;

            long reserved = (currentValue - pivotValue - swapValue);
            long minSwapValue = threshold - reserved;

            long swap = pivot.maxSwappingQuantity(X[i], minSwapValue, swappable, precision);
            if(swap < 0) {
                if (X[i].getMaxValue() >= pivot.getMaxValue()) swap = swappable - (one + swap);
                else break;
                if(swap > swappable) break;
            }
            swap = Math.min(swap, pivot.getMaxQuantity());
            quantity += swap;
            currentValue += ((pivot.getMaxValue() - X[i].getMaxValue()) * swap) / one;
            quantities.set(i, quantities.get(i) - swap);
            quantities.set(pivotPos, quantity);

        }
        if(quantity < 0) return 0;

        return quantity;

    }

    public static long lowerbound(StochasticVariable[] X, StochasticVariable pivot, long threshold, int precision){
        ArrayOfLong quantities = ArrayOfLong.create(X.length);
        long one = (long) Math.pow(10, precision);
        long maxQuantity = one;
        long currentValue = 0;
        int pivotPos = -1;

        // Set first all minimum quantity
        for(int i = 0; i < X.length; i++){
            if(X[i] == pivot) pivotPos = i;
            if(maxQuantity <= 0) continue;
            if(X[i].getMinQuantity() == 0) continue;

            quantities.set(i, Math.min(maxQuantity, X[i].getMinQuantity()));
            currentValue += (quantities.get(i) * X[i].getMaxValue()) / one;
            maxQuantity -= X[i].getMinQuantity();
        }

        // Fill from largest to smallest value
        for(int i = 0; i < X.length; i++){
            if(X[i] == pivot) pivotPos = i;
            if(maxQuantity <= 0) continue;

            // Already a min value
            if(quantities.get(i) != 0) {
                quantities.set(i, Math.min(maxQuantity+X[i].getMinQuantity(), X[i].getMaxQuantity()));
                currentValue += ((quantities.get(i) - X[i].getMinQuantity()) * X[i].getMaxValue()) / one;
            } else {
                quantities.set(i, Math.min(maxQuantity, X[i].getMaxQuantity()));
                currentValue += (quantities.get(i) * X[i].getMaxValue()) / one;
            }
            maxQuantity -= X[i].getMaxQuantity() - X[i].getMinQuantity();
        }

        long quantity = quantities.get(pivotPos);
        // If we maxxed up until the pivot
        if(quantity == pivot.getMinQuantity()) {
            if (currentValue >= threshold) return quantity;
            else return -1;
        }

        long swappable = 0;
        long pivotValue = 0;
        long swapValue = 0;

        for(int i = 0; i < X.length; i++){

            // Empty
            if(quantities.get(i) >= X[i].getMaxQuantity()) continue;
            if(i == pivotPos) continue;
            if(quantity <= 0) continue;

            // If we try to go up but we can only go down, stop
            if (currentValue <= threshold && pivot.getMaxValue() >= X[i].getMaxValue()) break;

            swappable = Math.min(quantity - pivot.getMinQuantity(), X[i].getMaxQuantity() - quantities.get(i));
            pivotValue = (swappable * pivot.getMaxValue()) / one;

            long reserved = (currentValue - pivotValue);
            //long locked = (quantities.get(i) * X[i].getMaxValue()) / one;
            long minSwapValue = threshold - reserved;

            long swap = X[i].maxSwappingQuantity(pivot, minSwapValue, swappable, precision);
            if(swap > swappable) swap = swappable;

            quantity -= swap;
            currentValue += ((X[i].getMaxValue() - pivot.getMaxValue()) * swap) / one;
            quantities.set(i, quantities.get(i) + swap);
            quantities.set(pivotPos, quantity);
        }

        // Can remove some quantity
        if(currentValue > threshold) {
            long exceed = currentValue - threshold;
            quantity = quantity - (exceed * one) / pivot.getMaxValue();
        }
        return quantity;
    }

}
