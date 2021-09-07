package confidence;

import builder.constraints.ConstraintBuilder;
import confidence.parameters.ParametersMulPF;
import confidence.parameters.ParametersMulRelaxed;
import confidence.states.StateMul;
import confidence.states.StateMulPF;
import confidence.states.StateMulRelaxed;
import confidence.states.StateSumDouble;
import confidence.structures.PrimeFactorization;
import confidence.structures.arrays.ArrayOfBigInteger;
import confidence.structures.arrays.ArrayOfPrimeFactorization;
import confidence.utils.SpecialOperations;
import mdd.MDD;
import mdd.components.SNode;
import memory.Memory;
import confidence.parameters.ParametersMul;
import confidence.parameters.ParametersSumDouble;
import structures.Domains;
import structures.generics.MapOf;
import structures.arrays.ArrayOfDouble;

import java.math.BigInteger;

public class MyConstraintBuilder extends ConstraintBuilder {

    public static strictfp MDD mulRelaxed(MDD result, Domains D, double min, double max, double maxProbaDomains, double maxProbaEpsilon, int size){
        // CHECK MyConstraintOperation mulRelaxed IF MAKING CHANGE TO THIS FUNCTION !
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

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static MDD mul(MDD result, Domains D, BigInteger min, BigInteger max, int size){
        SNode snode = SNode.create();
        ArrayOfBigInteger minValues = ArrayOfBigInteger.create(size);
        ArrayOfBigInteger maxValues = ArrayOfBigInteger.create(size);

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

        ParametersMul parameters = ParametersMul.create(min, max, minValues, maxValues);
        snode.setState(StateMul.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static MDD mulPF(MDD result, Domains D, double min, double max, MapOf<Integer, PrimeFactorization> mapPrimeFact, int size){
        SNode snode = SNode.create();
        ArrayOfPrimeFactorization minValues = ArrayOfPrimeFactorization.create(size);
        ArrayOfPrimeFactorization maxValues = ArrayOfPrimeFactorization.create(size);

        //Important d'initialiser la dernière valeur du tableau à 1
        minValues.set(size-1, PrimeFactorization.create(1));
        maxValues.set(size-1, PrimeFactorization.create(1));

        for(int i = size - 2; i >= 0; i--){
            //On initialise pour ne pas avoir de souci avec les conditions dans la boucle
            PrimeFactorization vMin = PrimeFactorization.create(1);
            PrimeFactorization vMax = PrimeFactorization.create(1);
            boolean firstIteration = true;

            PrimeFactorization primeFactV;
            for(int v : D.get(i+1)) {
                primeFactV = mapPrimeFact.get(v);

                if (firstIteration){
                    vMin = primeFactV;
                    vMax = primeFactV;
                    firstIteration = false;
                } else {
                    if(primeFactV.toLog10() < vMin.toLog10()) vMin = primeFactV;
                    if(primeFactV.toLog10() > vMax.toLog10()) vMax = primeFactV;
                }

            }

            if(i < size - 1) {
                vMin = vMin.multiply(minValues.get(i+1));
                vMax = vMax.multiply(maxValues.get(i+1));
            }
            minValues.set(i, vMin);
            maxValues.set(i, vMax);
        }


        ParametersMulPF parameters = ParametersMulPF.create(min, max, minValues, maxValues, mapPrimeFact);
        snode.setState(StateMulPF.create(parameters));

        build(result, snode, D, size);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

    public static strictfp MDD sumDouble(MDD result, Domains D, double min, double max, MapOf<Integer, Double> mapDouble, int epsilon, int size){
        // CHECK MyConstraintOperation sumDouble IF MAKING CHANGE TO THIS FUNCTION !
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
        ParametersSumDouble parameters = ParametersSumDouble.create(min, max, minValues, maxValues, mapDouble, epsilon);
        snode.setState(StateSumDouble.create(parameters));

        build(result, snode, D, size, true);

        Memory.free(parameters);
        result.reduce();
        return result;
    }

}
