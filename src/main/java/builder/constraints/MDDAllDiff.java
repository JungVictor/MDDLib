package builder.constraints;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import representation.MDDPrinter;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;

import java.util.Arrays;
import java.util.HashMap;

public class MDDAllDiff {

    private MDDAllDiff(){}

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

    private static ArrayOfInt domain(ArrayOfInt V, int value){
        ArrayOfInt domain = Memory.ArrayOfInt(V.length - 1);
        int i = 0;
        for(int v : V) if(v != value) domain.set(i++, v);
        return domain;
    }

}
