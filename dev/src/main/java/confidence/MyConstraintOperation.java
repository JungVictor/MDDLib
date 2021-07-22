package confidence;

import confidence.parameters.ParametersSumDouble;
import confidence.states.StateSumDouble;
import mdd.MDD;
import mdd.components.SNode;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import structures.Domains;
import structures.arrays.ArrayOfDouble;
import structures.generics.MapOf;

public class MyConstraintOperation extends ConstraintOperation {


    static public MDD confidence(MDD result, MDD mdd, double gamma, int precision, int epsilon, int n, Domains D){
        MapOf<Integer, Double> mapLog = MyMemory.MapOfIntegerDouble();
        for(int i = 0; i < n; i++){
            for(int v : D.get(i)){
                mapLog.put(v, -1 * Math.log(v * Math.pow(10, -precision)));
            }
        }
        double s_max = -1 * Math.log(gamma);

        return sumDouble(result, mdd, D, 0, s_max, mapLog, precision + epsilon, n);
    }

    /**
     * Perform the intersection operation between mdd and a sum constraint
     * @param result The MDD that will store the result
     * @param mdd The MDD on which to perform the operation
     * @return the MDD resulting from the intersection between mdd and the sum constraint
     */
    static public MDD sumDouble(MDD result, MDD mdd, Domains D, double min, double max, MapOf<Integer, Double> mapDouble, int precision, int size){
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
