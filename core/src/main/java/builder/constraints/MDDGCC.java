package builder.constraints;

import builder.MDDBuilder;
import dd.interfaces.INode;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import dd.operations.Operation;
import memory.Binary;
import memory.Memory;
import structures.Binder;
import structures.arrays.ArrayOfMDD;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import structures.tuples.TupleOfInt;
import utils.Logger;

import java.util.HashMap;

public class MDDGCC {

    private MDDGCC(){}

    public static MDD generate_(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, ArrayOfInt V){
        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();
        int min, max;

        // If there is only one value constrained, then we perform the operation directly of the result MDD
        if (couples.keySet().size() == 1){
            for(int v : couples.keySet()) {
                V0.clear(); V1.clear();
                for(int v0 : V) V0.add(v0);
                V0.remove(v);
                V1.add(v);
                min = couples.get(v).getFirst();
                max = couples.get(v).getSecond();
                MDDBuilder.sum(mdd, min, max, n, Binary.Set());
                mdd.replace(V0, V1);
            }
        }

        // Else, we have to build all values' MDD and perform an intersection
        else {
            ArrayOfMDD sums = ArrayOfMDD.create(couples.size());
            int p = 0;
            V0.clear(); V1.clear();
            for(int v : V) V0.add(v);
            for(int v : couples.keySet()) {
                V0.remove(v);
                V1.add(v);
                min = couples.get(v).getFirst();
                max = couples.get(v).getSecond();
                sums.set(p, MDDBuilder.sum(MDD.create(), min, max, n, Binary.Set()));
                sums.get(p++).replace(V0, V1);
                V0.add(v);
                V1.remove(v);
            }
            Operation.intersection(mdd, sums);
            for(int i = 0; i < sums.length; i++) Memory.free(sums.get(i));
            Memory.free(sums);
        }

        Memory.free(V0);
        Memory.free(V1);
        return mdd;
    }

    public static MDD generate(MDD mdd, int n, MapOf<Integer, TupleOfInt> couples, ArrayOfInt V){
        mdd.setSize(n);

        // TODO : allocation from memory
        HashMap<Node, ArrayOfInt> values = new HashMap<>(),
                nextValues = new HashMap<>(),
                tmp;
        HashMap<String, Node> gcc = new HashMap<>();

        ArrayOfInt Vcopy = ArrayOfInt.create(couples.keySet().size());
        values.put(mdd.getRoot(), Vcopy);

        for(int i = 1; i < mdd.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(Node x : mdd.getLayer(i-1)) {
                ArrayOfInt domain = values.get(x);
                for(int j = 0; j < domain.length; j++){
                    if(domain.get(j)+1 > couples.get(j).getSecond()) continue;
                    if(i >= mdd.size() - 1 && domain.get(j)+1 < couples.get(j).getFirst()) continue;
                    ArrayOfInt newDomain = domain(domain, j);
                    String key = newDomain.toString();
                    Node y = gcc.get(key);

                    if(y == null) {
                        y = mdd.Node();
                        gcc.put(key, y);
                        nextValues.put(y, newDomain);
                        mdd.addNode(y, i);
                    } else Memory.free(newDomain);
                    mdd.addArc(x, j, y, i-1);
                }
            }
            for(ArrayOfInt array : values.values()) Memory.free(array);
            gcc.clear();
            values.clear();
            tmp = values;
            values = nextValues;
            nextValues = tmp;
        }

        mdd.reduce();
        return mdd;
    }

    public static MDD intersection(MDD result, MDD mdd, MapOf<Integer, TupleOfInt> couples){
        result.setSize(mdd.size());

        Binder binder = Binder.create();

        // TODO : allocation from memory
        HashMap<Node, ArrayOfInt> values = new HashMap<>(),
                nextValues = new HashMap<>(),
                tmp;
        HashMap<String, Node> gcc = new HashMap<>();

        Node fakeroot = Node.create();

        ArrayOfInt Vcopy = ArrayOfInt.create(couples.keySet().size());

        values.put(fakeroot, Vcopy);

        result.getRoot().associate(mdd.getRoot(), fakeroot);

        for(int i = 1; i < result.size(); i++){
            Logger.out.information("\rLAYER " + i);
            for(Node x : result.getLayer(i-1)) {
                INode x1 = x.getX1();
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
        ArrayOfInt domain = ArrayOfInt.create(V.length);
        domain.copy(V);
        domain.set(value, domain.get(value)+1);
        return domain;
    }
}
