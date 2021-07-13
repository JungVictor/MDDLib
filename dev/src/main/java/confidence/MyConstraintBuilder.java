package confidence;

import builder.constraints.ConstraintBuilder;
import mdd.MDD;
import mdd.components.SNode;
import memory.Memory;
import confidence.parameters.ParametersMul;
import confidence.parameters.ParametersSumDouble;
import structures.Domains;
import structures.generics.ArrayOf;
import structures.generics.MapOf;

import java.math.BigInteger;

public class MyConstraintBuilder extends ConstraintBuilder {

    public static MDD mul(MDD result, Domains D, BigInteger min, BigInteger max, int size){
        SNode snode = MyMemory.SNode();
        ArrayOf<BigInteger> minValues = MyMemory.ArrayOfBigInteger(size);
        ArrayOf<BigInteger> maxValues = MyMemory.ArrayOfBigInteger(size);

        //Important d'initialiser la dernière valeur du tableau à 1
        minValues.set(size-1, BigInteger.valueOf(1));
        maxValues.set(size-1, BigInteger.valueOf(1));

        for(int i = size - 2; i >= 0; i--){
            //On initialise pour ne pas avoir de souci avec les conditions dans la boucle
            BigInteger vMin = BigInteger.ZERO;
            BigInteger vMax = BigInteger.ZERO;
            boolean firstIteration = true;
            for(int v : D.get(i+1)) {
                BigInteger bigIntV = BigInteger.valueOf(v);

                if (firstIteration){
                    vMin = bigIntV;
                    vMax = bigIntV;
                    firstIteration = false;
                } else {
                    if(bigIntV.compareTo(vMin) < 0) vMin = bigIntV;
                    if(bigIntV.compareTo(vMax) > 0) vMax = bigIntV;
                }

            }

            if(i < size - 1) {
                vMin = vMin.multiply(minValues.get(i+1));
                vMax = vMax.multiply(maxValues.get(i+1));
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }

        ParametersMul parameters = MyMemory.ParametersMul(min, max, minValues, maxValues);
        snode.setState(MyMemory.StateMul(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    static public MDD sumDouble(MDD result, Domains D, double min, double max, MapOf<Integer, Double> mapDouble,int precision, int size){
        SNode snode = MyMemory.SNode();
        ArrayOf<Double> minValues = MyMemory.ArrayOfDouble(size);
        ArrayOf<Double> maxValues = MyMemory.ArrayOfDouble(size);

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
        ParametersSumDouble parameters = MyMemory.ParametersSumDouble(min, max, minValues, maxValues, mapDouble, precision);
        snode.setState(MyMemory.StateSumDouble(parameters));

        build(result, snode, D, size, true);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

}
