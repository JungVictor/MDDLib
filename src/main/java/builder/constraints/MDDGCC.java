package builder.constraints;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Memory;
import structures.Binder;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;
import structures.integers.TupleOfInt;
import utils.Logger;

import java.util.HashMap;

public class MDDGCC {

    private MDDGCC(){}

    public static MDD generate(MDD mdd, int n, MatrixOfInt couples, ArrayOfInt V){
        ArrayOfInt B = Memory.ArrayOfInt(2);
        B.set(0,0); B.set(1,1);

        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();
        int min, max;

        // If there is only one value constrained, then we perform the operation directly of the result MDD
        if (couples.getHeight() == 1){
            V0.clear(); V1.clear();
            for(int v : V) if(v != couples.get(0, 0)) V0.add(v);
            V1.add(couples.get(0, 0));
            min = couples.get(0, 1);
            if(couples.getLength() == 3) max = couples.get(0, 2);
            else max = min;
            MDDBuilder.sum(mdd, min, max, n, B);
            mdd.replace(V0, V1);
        }

        // Else, we have to build all values' MDD and perform an intersection
        else {
            ArrayOf<MDD> sums = Memory.ArrayOfMDD(couples.getHeight());
            for (int i = 0; i < sums.length; i++) {
                V0.clear();
                V1.clear();
                for (int v : V) if (v != couples.get(i, 0)) V0.add(v);
                V1.add(couples.get(i, 0));
                min = couples.get(i, 1);
                if (couples.getLength() == 3) max = couples.get(i, 2);
                else max = min;
                sums.set(i, MDDBuilder.sum(mdd.MDD(), min, max, n, B));
                sums.get(i).replace(V0, V1);
            }
            Operation.intersection(mdd, sums);
            for(int i = 0; i < sums.length; i++) Memory.free(sums.get(i));
            Memory.free(sums);
        }

        Memory.free(V0);
        Memory.free(V1);
        Memory.free(B);
        return mdd;
    }

    public static MDD intersection(MDD result, MDD mdd, MapOf<Integer, TupleOfInt> couples){
        result.setSize(mdd.size());

        Binder binder = Memory.Binder();

        // TODO : allocation from memory
        HashMap<Node, ArrayOfInt> values = new HashMap<>(),
                nextValues = new HashMap<>(),
                tmp;
        HashMap<String, Node> gcc = new HashMap<>();

        Node fakeroot = Memory.Node();

        ArrayOfInt Vcopy = Memory.ArrayOfInt(couples.keySet().size());

        values.put(fakeroot, Vcopy);

        result.getRoot().associates(mdd.getRoot(), fakeroot);

        for(int i = 1; i < result.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(Node x : result.getLayer(i-1)) {
                Node x1 = x.getX1();
                ArrayOfInt domain = values.get(x.getX2());
                for(int j = 0; j < domain.length; j++){
                    if(!x1.containsLabel(j)) continue;
                    if(domain.get(j)+1 > couples.get(j).getSecond()) continue;
                    if(i >= result.size() - 1 && domain.get(j)+1 < couples.get(j).getFirst()) continue;
                    ArrayOfInt newDomain = domain(domain, j);
                    String key = newDomain.toString();
                    Node y2 = gcc.get(key);

                    if(y2 == null) {
                        y2 = result.Node();
                        gcc.put(key, y2);
                        nextValues.put(y2, newDomain);
                    } else Memory.free(newDomain);

                    Operation.addArcAndNode(result, x, x1.getChild(j), y2, j, i, binder);
                }
            }
            for(Node node : values.keySet()) Memory.free(node);
            for(ArrayOfInt array : values.values()) Memory.free(array);
            gcc.clear();
            values.clear();
            tmp = values;
            values = nextValues;
            nextValues = tmp;
            binder.clear();

            // TODO : Cleanup
            if(result.getLayer(i).size() == 0) {
                result.clear();
                return result;
            }

        }

        result.reduce();
        return result;
    }

    private static ArrayOfInt domain(ArrayOfInt V, int value){
        ArrayOfInt domain = Memory.ArrayOfInt(V.length);
        domain.copy(V);
        domain.set(value, domain.get(value)+1);
        return domain;
    }
}
