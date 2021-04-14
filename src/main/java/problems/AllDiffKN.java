package problems;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Memory;
import representation.MDDPrinter;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

import java.util.Arrays;

public class AllDiffKN {

    private int K, n;

    public AllDiffKN(int K, int n) {
        this.K = K;
        this.n = n;
    }

    public MDD solve(MDD mdd){
        // The initial domain and mapping
        ArrayOfInt initial_domain = Memory.ArrayOfInt(K+1);
        MapOf<Integer, SetOf<Integer>> mapping = Memory.MapOfIntegerSetOfInteger();
        for(int i = 0; i <= K; i++) {
            initial_domain.set(i, i);
            mapping.put(i, Memory.SetOfInteger());
        }
        for(int i = 0; i < K; i++) mapping.put(K+1+i, Memory.SetOfInteger());

        ArrayOfInt tokens = Memory.ArrayOfInt(K);
        for(int i = 0; i < tokens.length; i++) tokens.set(i, K+i+1);

        // The AllDiff MDD defined over K variables
        MDD alldiff = MDDBuilder.alldiff(mdd.MDD(), initial_domain, tokens,  K+1);

        MDD alldiffN = alldiff.copy();
        MDD old_alldiffN = alldiffN;
        while(alldiffN.size() < n+1) {
            alldiffN = Operation.concatenate(alldiffN, alldiff);
            Memory.free(old_alldiffN);
            old_alldiffN = alldiffN;
        }
        Memory.free(alldiff);

        MDD accumulator = domains(n);
        MDD old_accumulator = accumulator;
        for(int i = 0; i < K; i++) {
            accumulator = Operation.layerIntersection(accumulator, replaceValues(alldiffN.copy(), i, mapping), i, n+1, n+1);
            Memory.free(old_accumulator);
            old_accumulator = accumulator;
        }
        Operation.layerIntersection(mdd, accumulator, replaceValues(alldiffN.copy(), K, mapping), K, n+1, n+1);

        Memory.free(alldiffN);
        Memory.free(accumulator);

        return mdd;
    }

    private MDD replaceValues(MDD allDiffN, int offset, MapOf<Integer, SetOf<Integer>> values) {
        // We know that size is multiple of K+1
        int size = allDiffN.size() / (K+1);
        for(int i = 0; i < size; i++) {
            for(int k : values) {
                values.get(k).clear();
                values.get(k).add(k + offset + i * (K+1));
            }
            int start = i * (K+1), stop = start + K + 1;
            allDiffN.replace(values, start, stop + 1);
        }
        return allDiffN;
    }

    private MDD domains(int n){
        MDD domains = Memory.MDD();
        domains.setSize(n+1);
        Node current = domains.getRoot();
        for(int i = 0; i < domains.size() - 1; i++){
            Node next = domains.Node();
            domains.addNode(next, i+1);
            for(int j = 0; j <= K; j++) domains.addArc(current, j+i, next);
            current = next;
        }
        domains.reduce();
        return domains;
    }

}
