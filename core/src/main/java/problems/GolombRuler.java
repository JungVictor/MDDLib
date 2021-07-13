package problems;

import builder.MDDBuilder;
import builder.constraints.parameters.ParametersDiff;
import mdd.MDD;
import mdd.components.Node;
import mdd.components.SNode;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import structures.generics.ArrayOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

public class GolombRuler {

    public static MDD generate(MDD result, int domain, int size){
        SetOf<Integer> D = Memory.SetOfInteger();
        ArrayOfInt V = Memory.ArrayOfInt(domain+1);
        for(int i = 0; i < domain+1; i++) {
            D.add(i);
            V.set(i,i);
        }
        MDD tmp = MDDBuilder.gt(Memory.MDD(), size+1, V);
        MDD old = tmp;

        ArrayOf<Node> nodes = Memory.ArrayOfNode(size-1);

        for(int i = 1; i < size; i++){
            SNode constraint = Memory.SNode();
            ParametersDiff parameters = Memory.ParametersDiff(i);
            constraint.setState(Memory.StateDiff(parameters, size+1));
            nodes.set(i-1, constraint);
        }

        ConstraintOperation.intersection(result, tmp, nodes);

        /*
        tmp = ConstraintOperation.diff(Memory.MDD(), tmp, 1, false);
        Memory.free(old);
        old = tmp;
        tmp.removeChildless();
        for(int i = 2; i < size-1; i++) {
            tmp = ConstraintOperation.diff(Memory.MDD(), tmp, i, true);
            Memory.free(old);
            old = tmp;
            tmp.removeChildless();
        }
        ConstraintOperation.diff(result, tmp, size-1, true);

         */
        result.reduce();
        return result;
    }

}
