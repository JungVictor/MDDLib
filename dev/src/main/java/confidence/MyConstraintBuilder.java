package confidence;

import builder.constraints.ConstraintBuilder;
import confidence.parameters.*;
import confidence.states.*;
import confidence.structures.PrimeFactorization;
import confidence.structures.arrays.ArrayOfPrimeFactorization;
import dd.mdd.MDD;
import dd.mdd.components.SNode;
import memory.Memory;
import structures.Domains;
import structures.generics.MapOf;

public class MyConstraintBuilder extends ConstraintBuilder {

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

}
