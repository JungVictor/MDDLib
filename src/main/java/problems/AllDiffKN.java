package problems;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Memory;
import representation.MDDPrinter;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

public class AllDiffKN {

    private int K, n;

    public AllDiffKN(int K, int n) {
        this.K = K;
        this.n = n;
    }

    public MDD solve(MDD mdd){

        ArrayOfInt initial_domain = Memory.ArrayOfInt(K + K + 1);
        // The initial domain and mapping
        initial_domain.setLength(K+1+K);
        for (int i = 0; i <= K+K; i++) initial_domain.set(i, i);
        ArrayOfInt tokens = Memory.ArrayOfInt(initial_domain.length - 1);
        for (int i = 0; i < tokens.length; i++) tokens.set(i, K + K + i + 1);

        MDD alldiff = MDDBuilder.alldiff(mdd.MDD(), initial_domain, tokens,  K+K+1);



        MapOf<Integer, SetOf<Integer>> mapping = Memory.MapOfIntegerSetOfInteger();
        for(int i = 0; i < K*4+1; i++) mapping.put(i, Memory.SetOfInteger());

        /*
        MDD alldiffN = alldiff.copy();
        MDD old_alldiffN = alldiffN;
        while(alldiffN.size() < n+1) {
            alldiffN = Operation.concatenate(alldiffN, alldiff);
            Memory.free(old_alldiffN);
            old_alldiffN = alldiffN;
        }

         */
        //Memory.free(alldiff);

        MDD accumulator = domains(n);
        MDD old_accumulator = accumulator;
        //accumulator = Operation.intersection(accumulator, alldiff.copy(), 0, alldiff.size(), n+1);
        //accumulator.accept(new MDDPrinter());

        for(int i = 0; i < K; i++) {
            accumulator = Operation.intersection(accumulator, alldiff.copy(), i, alldiff.size() + i, n+1);
            Memory.free(old_accumulator);
            old_accumulator = accumulator;
        }

        for(int i = K; i < n-1; i++) {
            accumulator = Operation.intersection(accumulator, replaceValues(alldiff.copy(), i-K, mapping), i, Math.min(n+1, alldiff.size() + i), n+1);
            Memory.free(old_accumulator);
            old_accumulator = accumulator;
        }
        Operation.intersection(mdd, accumulator, replaceValues(alldiff.copy(), n-1-K, mapping), n-1, n+1, n+1);

        //Memory.free(alldiffN);
        Memory.free(accumulator);

        return mdd;
    }

    private MDD replaceValues(MDD allDiffN, int offset, MapOf<Integer, SetOf<Integer>> values) {
        // We know that size is multiple of K+1
        int size = allDiffN.size() / (K*2+1);
        for(int i = 0; i < size; i++) {
            for(int k : values) {
                values.get(k).clear();
                values.get(k).add(k + offset + i * (K*2+1));
            }
            int start = i * (K*2+1), stop = start + K*2 + 1;
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
            for(int j = 0; j <= K; j++) {
                if(i-j >= 0) domains.addArc(current, i-j, next);
                domains.addArc(current, j+i, next);
            }
            current = next;
        }
        domains.reduce();
        return domains;
    }

}
