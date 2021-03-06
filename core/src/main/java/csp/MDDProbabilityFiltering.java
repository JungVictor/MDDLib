package csp;

import csp.constraints.IntervalConstraintKnapsack;
import csp.constraints.IntervalConstraintSum;
import dd.mdd.MDD;
import dd.mdd.components.Layer;
import dd.mdd.components.Node;
import dd.mdd.components.OutArcs;
import dd.operations.Stochastic;
import representation.MDDPrinter;
import structures.StochasticVariable;
import structures.arrays.ArrayOfIntervalVariable;
import structures.arrays.ArrayOfLong;
import structures.lists.ListOfIntervalVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MDDProbabilityFiltering {
    /**
     * Propagate the intervals of probability through the nodes from the bottom to
     * the top of an MDD.<br>
     * The IntervalVariable linked to a node represents the interval of probability
     * of all the path from the node to tt. <br>
     * If a node is already linked to an IntervalVariable with <b>intervalCosts</b>,
     * it will intersect this intervalVariable with the computed IntervalVariable.<br>
     * <b>The upper bound of this interval can be sur-estimated.</b>
     * @param mdd The MDD.
     * @param intervalProbabilities The intervals of the probability of each label
     *                              for each layer. <b>intervalProbabilities[i]</b>
     *                              corresponds to the layer <b>i</b>.
     * @param intervalCosts The links between each node and its interval of probability.
     * @param minProbabilities The sum of minimal probabilities of all the labels of a layer.
     *                         <b>minProbabilities[i]</b> corresponds to the layer <b>i</b>.
     * @param precision The number of decimal digit (after the decimal separator) represented by the values of intervals.
     */
    public static void propagationBottomUp(MDD mdd, HashMap<Integer, IntervalVariable>[] intervalProbabilities, HashMap<Node, IntervalVariable> intervalCosts, ArrayOfLong minProbabilities, int precision){
        long one = (long) Math.pow(10, precision);
        if (!intervalCosts.containsKey(mdd.getTt())) {
            intervalCosts.put(mdd.getTt(), IntervalVariable.create(one, one));
        }
        else {
            intervalCosts.get(mdd.getTt()).intersect(one, one);
        }
        Node currentNode;
        Iterator<Node> iterator;
        OutArcs outArcs;
        StochasticVariable[] stochasticVariables;
        int size;
        StochasticVariable stochasticVariable;
        IntervalVariable intervalProbability;
        IntervalVariable intervalCost;
        long minCost;
        long maxCost;
        long sumOfMinProbabilities;

        //For each layer from the bottom to the top
        for (int i = mdd.size()-2; i >= 0; i--) {
            iterator = mdd.getLayer(i).iterator();
            //For each node in the layer
            while (iterator.hasNext()){
                currentNode = iterator.next();
                currentNode.sortChildren(intervalCosts);
                outArcs = currentNode.getChildren();
                size = outArcs.size();
                stochasticVariables = new StochasticVariable[size];

                //Creation of the array of StochasticVariable
                for (int j = 0; j < size; j++) {
                    intervalProbability = intervalProbabilities[i].get(outArcs.getValue(j));
                    intervalCost = intervalCosts.get(currentNode.getChild(outArcs.getValue(j)));
                    stochasticVariable = StochasticVariable.create(precision);
                    stochasticVariable.setQuantity(intervalProbability.getMin(), intervalProbability.getMax());
                    stochasticVariable.setValue(intervalCost.getMin(), intervalCost.getMax());
                    stochasticVariables[j] = stochasticVariable;
                }

                //Computation of the minimal cost
                minCost = 0;
                sumOfMinProbabilities = 0;
                for (int j = 0; j < size; j++) {
                    stochasticVariable = stochasticVariables[j];
                    minCost += stochasticVariable.getMinQuantity() * stochasticVariable.getMinValue();
                    sumOfMinProbabilities += stochasticVariable.getMinQuantity();
                }
                minCost = (long) Math.floor(minCost * Math.pow(10, -precision));

                //Computation of the maximal cost
                maxCost = Stochastic.maxPacking(stochasticVariables, one - (minProbabilities.get(i) - sumOfMinProbabilities));
                maxCost = (long) Math.ceil(maxCost * Math.pow(10, -precision));

                //Initialisation/intersection of the interval cost of the node
                if (intervalCosts.containsKey(currentNode)){
                    intervalCosts.get(currentNode).intersect(minCost, maxCost);
                }
                else {
                    intervalCosts.put(currentNode, IntervalVariable.create(minCost, maxCost));
                }
            }
        }
    }

    /**
     * Create the constraints network to filter the probability of all the labels of a layer of an MDD
     * @param layer The layer to filter.
     * @param intervalProbabilities The interval probability of each label of the layer.
     * @param intervalCosts The cost associated to the node (must run <b>propagationBottomUp</b> before)
     * @param precision The number of decimal digit (after the decimal separator) represented by the values of intervals.
     * @return A constraint network that represents the layer.
     */
    public static IntervalConstraintsNetwork layerModelling(Layer layer, HashMap<Integer, IntervalVariable> intervalProbabilities, HashMap<Node, IntervalVariable> intervalCosts, int precision){
        IntervalConstraintsNetwork constraintsNetwork = IntervalConstraintsNetwork.create();
        long one = (long) Math.pow(10, precision);
        IntervalVariable zero = IntervalVariable.create(0, 0);

        Node currentNode;
        Iterator<Node> iterator = layer.iterator();
        OutArcs children;
        ListOfIntervalVariable probabilities;
        ListOfIntervalVariable costs;
        ArrayOfIntervalVariable finalProbabilities;
        ArrayOfIntervalVariable finalCosts;
        IntervalVariable oldProbability;
        IntervalVariable currentProbability;
        IntervalVariable oldCost;
        IntervalVariable currentCost;
        HashSet<Integer> probabilitiesInConstraint = new HashSet<>();

        //For each node of the layer
        while (iterator.hasNext()) {
            oldProbability = null;
            currentProbability = null;
            oldCost = null;
            currentCost = null;

            probabilitiesInConstraint.clear();

            currentNode = iterator.next();
            children = currentNode.getChildren();

            probabilities = ListOfIntervalVariable.create();
            costs = ListOfIntervalVariable.create();

            //For each arc from this node
            for (int i = 0; i < children.size(); i++) {
                oldProbability = currentProbability;
                oldCost = currentCost;
                currentProbability = intervalProbabilities.get(children.getValue(i));
                currentCost = intervalCosts.get(currentNode.getChildByIndex(i));

                probabilitiesInConstraint.add(children.getValue(i));

                //If the old arc and the current arc point to the same node
                if (oldCost == currentCost) {
                    //We merge the arcs
                    IntervalVariable link = IntervalVariable.create(0, one);
                    constraintsNetwork.addConstraint(IntervalConstraintSum.create(link, oldProbability, currentProbability));
                    //link is the arc that will be added (or merge) in the next step
                    currentProbability = link;
                }
                //If the old arc and the current arc do not point to the same node
                else if (oldCost != null) {
                    //We add the probability of the old arc and the cost of
                    //the node that is pointed by it in the array for the constraint
                    probabilities.add(oldProbability);
                    costs.add(oldCost);
                }
            }
            //We add the last tuple probability/cost in the array for the constraint
            probabilities.add(currentProbability);
            costs.add(currentCost);

            //For each label of the layer
            for (Integer key : intervalProbabilities.keySet()){
                //If the interval probability of the label is not in the array for the constraint
                if (!probabilitiesInConstraint.contains(key)){
                    //We add it and a zero interval cost
                    probabilities.add(intervalProbabilities.get(key));
                    costs.add(zero);
                }
            }

            //Creation of the array for the constraint
            finalProbabilities = ArrayOfIntervalVariable.create(probabilities.size());
            finalCosts = ArrayOfIntervalVariable.create(costs.size());
            for (int i = 0; i < finalProbabilities.length(); i++) {
                finalProbabilities.set(i, probabilities.get(i));
                finalCosts.set(i, costs.get(i));
            }

            //Adding the Knapsack constraint to the constraints network
            constraintsNetwork.addConstraint(IntervalConstraintKnapsack.create(finalProbabilities, finalCosts, intervalCosts.get(currentNode), one, precision, false));
        }

        return constraintsNetwork;
    }

    /**
     * Filter the interval probabilities of the labels of an MDD to remove probability for which
     * it is impossible to satisfy the following constraint : <br>
     * (sum of probabilities of all path of the MDD) >= <b>threshold</b>
     * @param mdd The MDD to filter.
     * @param intervalProbabilities The interval probabilities of each label depending on the layer
     * @param threshold The threshold to respect.
     * @param precision The number of decimal digit (after the decimal separator) represented by the values of intervals.
     */
    public static void filter(MDD mdd, HashMap<Integer, IntervalVariable>[] intervalProbabilities, long threshold, int precision){
        //Preparation
        ArrayOfLong minProbabilities = ArrayOfLong.create(intervalProbabilities.length);
        long currentMin;
        long one = (long) Math.pow(10, precision);

        IntervalConstraintsNetwork[] constraintsNetworks = new IntervalConstraintsNetwork[mdd.size() - 1];

        //Initialisation of the costs of the nodes
        HashMap<Node, IntervalVariable> intervalCosts = new HashMap<>();
        intervalCosts.put(mdd.getRoot(), IntervalVariable.create(threshold, one));
        for (int i = 1; i < mdd.size(); i++) {
            for(Node node : mdd.getLayer(i).getNodes()){
                intervalCosts.put(node, IntervalVariable.create(0, one));
            }
        }

        boolean isFiltered = true;
        boolean change;

        //Creation of the constraints networks for each layer
        for (int i = 0; i < constraintsNetworks.length; i++) {
            constraintsNetworks[i] = layerModelling(mdd.getLayer(i), intervalProbabilities[i], intervalCosts, precision);
        }

        int count = 1;
        //While there is a IntervalVariable that get filtered
        while (isFiltered) {
            isFiltered = false;

            //Compute the minimal probability for each layer
            for (int i = 0; i < minProbabilities.length(); i++) {
                currentMin = 0;
                for (Integer key : intervalProbabilities[i].keySet()) {
                    currentMin += intervalProbabilities[i].get(key).getMin();
                }
                minProbabilities.set(i, currentMin);
            }

            //Propagation of the costs of the nodes from the bottom to the top
            propagationBottomUp(mdd, intervalProbabilities, intervalCosts, minProbabilities, precision);

            System.out.println("====================================================================");
            System.out.println("====================================================================");
            System.out.println("LOOP "+count);
            count++;

            MDDPrinter printer = new MDDPrinter();

            //For each layer of the mdd
            for (int i = 0; i < mdd.size(); i++) {
                System.out.println("--------------------------------");
                System.out.println("LAYER " + i + " :");
                for (Node node : mdd.getLayer(i).getNodes()) {
                    node.accept(printer);
                    System.out.println("Cost : " + intervalCosts.get(node));
                    System.out.println("--");
                }
                if (i < mdd.size() - 1) {
                    for (Integer key : intervalProbabilities[i].keySet()) {
                        change = constraintsNetworks[i].resolve();
                        if (change) {
                            isFiltered = true;
                        }
                        System.out.println("Label " + key + " : " + intervalProbabilities[i].get(key));
                    }
                }
            }

        }
    }
}
