package builder.constraints;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import representation.MDDPrinter;
import structures.Binder;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

import java.util.Arrays;
import java.util.HashMap;

public class MDDAllDifferent {

    private MDDAllDifferent(){}

    public static MDD generate(MDD mdd, ArrayOfInt V, int size){
        return generate(mdd, V, null, size);
    }

    public static MDD generate(MDD mdd, ArrayOfInt V, ArrayOfInt C, int size){
        mdd.setSize(size + 1);

        // TODO : allocation from memory
        HashMap<Node, ArrayOfInt> values = new HashMap<>();
        HashMap<String, Node> alldiff = new HashMap<>();

        ArrayOfInt Vcopy = Memory.ArrayOfInt(V.length);
        Vcopy.copy(V);
        values.put(mdd.getRoot(), Vcopy);

        for(int i = 0; i < mdd.size() - 1; i++){
            alldiff.clear();
            for(Node node : mdd.getLayer(i)) {
                ArrayOfInt domain = values.get(node);
                for(int j = 0; j < domain.length; j++){
                    ArrayOfInt newDomain = domain(domain, domain.get(j));
                    String key = newDomain.toString();
                    Node newNode;
                    if(!alldiff.containsKey(key)) {
                        newNode = mdd.Node();
                        alldiff.put(key, newNode);
                        values.put(newNode, newDomain);
                        mdd.addNode(newNode, i+1);
                    } else {
                        Memory.free(newDomain);
                        newNode = alldiff.get(key);
                    }
                    mdd.addArc(node, domain.get(j), newNode);
                }
                if(C != null && C.length > 0) {
                    ArrayOfInt copyDomain = Memory.ArrayOfInt(domain.length);
                    copyDomain.copy(domain);
                    String key = copyDomain.toString();
                    Node newNode = mdd.Node();
                    alldiff.put(key, newNode);
                    values.put(newNode, copyDomain);
                    mdd.addNode(newNode, i+1);
                    for(int c : C) mdd.addArc(node, c, newNode);
                }
                Memory.free(values.get(node));
                values.remove(node);
            }
        }
        mdd.reduce();
        return mdd;
    }


    public static MDD intersection(MDD result, MDD mdd, ArrayOfInt V){
        return intersection(result, mdd, V, null);
    }

    public static MDD intersection(MDD result, MDD mdd, ArrayOfInt V, ArrayOfInt C){
        result.setSize(mdd.size());

        // TODO : allocation from memory
        HashMap<Node, ArrayOfInt> values = new HashMap<>(),
                                      nextValues = new HashMap<>(),
                                      tmp;
        HashMap<String, Node> alldiff = new HashMap<>();

        ArrayOfInt Vcopy = Memory.ArrayOfInt(V.length);
        Vcopy.copy(V);
        values.put(result.getRoot(), Vcopy);

        result.getRoot().associates(mdd.getRoot(), null);

        for(int i = 1; i < result.size(); i++){
            for(Node x : result.getLayer(i-1)) {
                Node x1 = x.getX1();
                ArrayOfInt domain = values.get(x);
                for(int j = 0; j < domain.length; j++){
                    if(!x1.containsLabel(domain.get(j))) continue;
                    ArrayOfInt newDomain = domain(domain, domain.get(j));
                    String key = newDomain.toString();
                    Node y;
                    if(!alldiff.containsKey(key)) {
                        y = result.Node();
                        alldiff.put(key, y);
                        nextValues.put(y, newDomain);
                        result.addNode(y,i);
                        y.associates(x1.getChild(domain.get(j)), y);
                    } else {
                        Memory.free(newDomain);
                        y = alldiff.get(key);
                    }
                    result.addArc(x, domain.get(j), y);
                }
                if(C != null && C.length > 0) {
                    ArrayOfInt copyDomain = Memory.ArrayOfInt(domain.length);
                    copyDomain.copy(domain);
                    String key = copyDomain.toString();
                    Node y = result.Node();
                    alldiff.put(key, y);
                    nextValues.put(y, copyDomain);
                    for(int c : C) if(x1.containsLabel(c)) result.addArc(x, c, y);
                }
            }
            alldiff.clear();
            values.clear();
            tmp = values;
            values = nextValues;
            nextValues = tmp;

            // TODO : Cleanup
            if(result.getLayer(i).size() == 0) {
                result.reduce();
                return result;
            }

        }

        result.reduce();
        return result;
    }

    private static ArrayOfInt domain(ArrayOfInt V, int value){
        ArrayOfInt domain = Memory.ArrayOfInt(V.length - 1);
        int i = 0;
        for(int v : V) if(v != value) domain.set(i++, v);
        return domain;
    }

    private static String keyGen(ArrayOfInt V, ArrayOfInt domain){
        return domain.toString();
    }



}
