package confidence;

import confidence.parameters.ParametersMulRelaxed;
import confidence.parameters.ParametersSumDouble;
import confidence.states.StateMulRelaxed;
import confidence.states.StateSumDouble;
import confidence.utils.SpecialOperations;
import mdd.MDD;
import mdd.components.SNode;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import structures.Domains;
import structures.arrays.ArrayOfDouble;
import structures.generics.MapOf;
import utils.SmallMath;

public class MyConstraintOperation extends ConstraintOperation {

    public static MDD confidenceMulRelaxed(MDD result, MDD mdd, int gamma, int precision, int epsilon, int n, Domains D){
        double maxProbaDomains = Math.pow(10, precision);
        double maxProbaEpsilon = Math.pow(10, epsilon);

        if(mdd == null) return MyMDDBuilder.mulRelaxed(result, gamma, maxProbaDomains, maxProbaDomains, maxProbaEpsilon, n, D);
        return mulRelaxed(result, mdd, D, gamma, maxProbaDomains, maxProbaDomains, maxProbaEpsilon, n);
    }

    public static strictfp MDD confidence(MDD result, MDD mdd, double gamma, int precision, int epsilon, int n, Domains D){
        MapOf<Integer, Double> mapLog = MyMemory.MapOfIntegerDouble();
        for(int i = 0; i < n; i++){
            for(int v : D.get(i)){
                mapLog.put(v, -1 * SmallMath.log(v * Math.pow(10, -precision), 10, 10, true));
            }
        }
        double s_max = -1 * SmallMath.log(gamma, 10, 10, false);

        if(mdd == null) return MyMDDBuilder.sumDouble(result, 0, s_max, mapLog, epsilon, n, D);
        return sumDouble(result, mdd, D, 0, s_max, mapLog, epsilon, n);
    }

    public static strictfp MDD mulRelaxed(MDD result, MDD mdd, Domains D, double min, double max, double maxProbaDomains, double maxProbaEpsilon, int size){
        // CHECK MyConstraintBuilder mulRelaxed IF MAKING CHANGE TO THIS FUNCTION !
        SNode snode = SNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à maxProbaEpsilon
        minValues.set(size-1, maxProbaEpsilon);
        maxValues.set(size-1, maxProbaEpsilon);

        for(int i = size - 2; i >= 0; i--){
            //On initialise pour ne pas avoir de souci avec les conditions dans la boucle
            double vMin = 0;
            double vMax = 0;
            boolean firstIteration = true;
            for(int v : D.get(i+1)) {

                if (firstIteration){
                    vMin = v;
                    vMax = v;
                    firstIteration = false;
                } else {
                    if(v < vMin) vMin = v;
                    if(v > vMax) vMax = v;
                }

            }

            if(i < size - 1) {
                vMin = SpecialOperations.multiplyFloor(vMin, minValues.get(i+1), maxProbaDomains);
                vMax = SpecialOperations.multiplyCeil(vMax, maxValues.get(i+1), maxProbaDomains);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        min = SpecialOperations.multiplyFloor(min, maxProbaEpsilon, maxProbaDomains);
        max = SpecialOperations.multiplyCeil(max, maxProbaEpsilon, maxProbaDomains);
        ParametersMulRelaxed parameters = ParametersMulRelaxed.create(min, max, minValues, maxValues, maxProbaDomains, maxProbaEpsilon);
        snode.setState(StateMulRelaxed.create(parameters));

        intersection(result, mdd, snode, false);

        Memory.free(snode);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

    /**
     * Perform the intersection operation between mdd and a sum constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the sum constraint
     */
    public static strictfp MDD sumDouble(MDD result, MDD mdd, Domains D, double min, double max, MapOf<Integer, Double> mapDouble, int precision, int size){
        // CHECK MyConstraintBuilder sumDouble IF MAKING CHANGE TO THIS FUNCTION !
        SNode snode = SNode.create();
        ArrayOfDouble minValues = ArrayOfDouble.create(size);
        ArrayOfDouble maxValues = ArrayOfDouble.create(size);

        //Important d'initialiser la dernière valeur du tableau à 0
        minValues.set(size-1, 0.0);
        maxValues.set(size-1, 0.0);

        for(int i = size - 2; i >= 0; i--){
            double vMin = Integer.MAX_VALUE, vMax = Integer.MIN_VALUE;
            for(int v : D.get(i+1)) {
                double doubleV = mapDouble.get(v);
                if(doubleV < vMin) vMin = doubleV;
                if(doubleV > vMax) vMax = doubleV;
            }
            if(i < size - 1) {
                vMin += minValues.get(i+1);
                vMax += maxValues.get(i+1);
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }
        ParametersSumDouble parameters = ParametersSumDouble.create(min, max, minValues, maxValues, mapDouble, precision);
        snode.setState(StateSumDouble.create(parameters));

        intersection(result, mdd, snode, true);

        Memory.free(snode);
        Memory.free(parameters);
        result.reduce();
        return result;
    }

}
