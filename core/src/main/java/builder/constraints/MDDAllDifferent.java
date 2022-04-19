package builder.constraints;

import dd.AbstractNode;
import dd.interfaces.NodeInterface;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import memory.Memory;
import structures.arrays.ArrayOfInt;
import utils.Logger;

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

        ArrayOfInt Vcopy = ArrayOfInt.create(V.length);
        Vcopy.copy(V);
        values.put(mdd.getRoot(), Vcopy);

        for(int i = 0; i < mdd.size() - 1; i++){
            Logger.out.information("\rALLDIFF LAYER " + i);
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
                    mdd.addArc(node, domain.get(j), newNode, i);
                }
                if(C != null && C.length > 0) {
                    ArrayOfInt copyDomain = ArrayOfInt.create(domain.length);
                    copyDomain.copy(domain);
                    String key = copyDomain.toString();
                    Node newNode = mdd.Node();
                    alldiff.put(key, newNode);
                    values.put(newNode, copyDomain);
                    mdd.addNode(newNode, i+1);
                    for(int c : C) mdd.addArc(node, c, newNode, i);
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

        ArrayOfInt Vcopy = ArrayOfInt.create(V.length);
        Vcopy.copy(V);
        values.put(result.getRoot(), Vcopy);

        result.getRoot().associate(mdd.getRoot(), null);

        for(int i = 1; i < result.size(); i++){
            for(Node x : result.getLayer(i-1)) {
                NodeInterface x1 = x.getX1();
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
                        y.associate(x1.getChild(domain.get(j)), y);
                    } else {
                        Memory.free(newDomain);
                        y = alldiff.get(key);
                    }
                    result.addArc(x, domain.get(j), y, i-1);
                }
                if(C != null && C.length > 0) {
                    ArrayOfInt copyDomain = ArrayOfInt.create(domain.length);
                    copyDomain.copy(domain);
                    String key = copyDomain.toString();
                    Node y = result.Node();
                    alldiff.put(key, y);
                    nextValues.put(y, copyDomain);
                    for(int c : C) if(x1.containsLabel(c)) result.addArc(x, c, y, i-1);
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
        ArrayOfInt domain = ArrayOfInt.create(V.length - 1);
        int i = 0;
        for(int v : V) if(v != value) domain.set(i++, v);
        return domain;
    }

    private static String keyGen(ArrayOfInt V, ArrayOfInt domain){
        return domain.toString();
    }



}
