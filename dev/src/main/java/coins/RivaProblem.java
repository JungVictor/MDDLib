package coins;

import dd.DecisionDiagram;
import dd.mdd.MDD;
import dd.mdd.nondeterministic.NDMDD;
import dd.mdd.nondeterministic.NDOperation;
import dd.operations.Operation;
import memory.Memory;
import structures.Domains;
import structures.arrays.ArrayOfInt;
import structures.arrays.ArrayOfMDD;
import structures.arrays.ArrayOfNodeInterface;
import structures.generics.SetOf;
import structures.lists.ListOfInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RivaProblem {

    /**
     * Solve the Riva problem with symmetries
     * @param T Sum of the problem
     * @param M Number of MDD to intersect
     * @param minD Minimum number of decomposition for an MDD
     * @param maxD Maximum number of decomposition for an MDD
     * @return The MDD containing the result of the intersection
     */
    public static MDD solve(int T, int M, int minD, int maxD){
        MDD accumulator = null, mdd;
        MDD tmp = null;
        // Create the array of decomposition
        // Maximum size is maxD
        ArrayOfInt decomposition = ArrayOfInt.create(maxD);
        ArrayOfMDD mdds = ArrayOfMDD.create(maxD);

        maxD = maxD - minD;
        Random random = new Random(0);
        for(int i = 0; i < M; i++){
            // Create the MDD composed of k MDDs (cartesian prod)
            int k = maxD == 0 ? 0 : random.nextInt(maxD);
            CoinUtil.decomposeInto(T, k+minD, random, decomposition);
            mdds.setLength(k+minD);
            for(int d = 0; d < k+minD; d++){
                int t = decomposition.get(d);
                Domains domains = CoinUtil.generateSystem(t);
                mdds.set(d, CoinChangeProblem.solve(MDD.create(), t, domains));
            }
            if(mdds.length() > 1) {
                mdd = Operation.concatenate(mdds);
                for(int j = 0; j < mdds.length; j++){
                    Memory.free(mdds.get(j));
                    mdds.clear();
                }
            }
            else mdd = mdds.get(0);

            // If it's the first step, accumulator is simply the MDD
            if(accumulator == null) accumulator = mdd;
            else {
                // Else, we compute the intersection between the accumulator
                // And the MDD
                tmp = accumulator;
                accumulator = Operation.intersection(accumulator, mdd);
                Memory.free(tmp);
            }
        }
        Memory.free(decomposition);
        Memory.free(mdds);
        System.out.println(accumulator.nSolutions());

        return accumulator;
    }

    public static MDD breaksym(int T, MDD mdd){

        SetOf<Integer> V = Memory.SetOfInteger();
        for(int i = 1; i < mdd.size() - 1; i++){
            if(!mdd.getDomains().get(i).equals(mdd.getDomains().get(i-1))) V.add(i);
        }
        System.out.println(mdd.getDomains());
        System.out.println(V);

        MDD sym = CoinChangeProblem.solveNoSym(MDD.create(), T, mdd.getDomains(), V);

        return Operation.intersection(sym, mdd);
    }

    /**
     * Solve the Riva problem without symmetries
     * @param T Sum of the problem
     * @param M Number of MDD to intersect
     * @param minD Minimum number of decomposition for an MDD
     * @param maxD Maximum number of decomposition for an MDD
     * @return The MDD containing the result of the intersection
     */
    public static MDD solve_nosym(int T, int M, int minD, int maxD) {
        DecisionDiagram accumulator = null;
        DecisionDiagram tmp = null;
        MDD mdd;
        // Create the array of decomposition
        // Maximum size is maxD
        ArrayOfInt decomposition = ArrayOfInt.create(maxD);
        ArrayOfNodeInterface roots = ArrayOfNodeInterface.create(maxD);
        ArrayOfMDD mdds = ArrayOfMDD.create(maxD);

        maxD = maxD - minD;
        Random random = new Random(0);
        for (int i = 0; i < M; i++) {
            // Create the MDD composed of k MDDs (cartesian prod)
            int k = maxD == 0 ? 0 : random.nextInt(maxD);
            CoinUtil.decomposeInto(T, k + minD, random, decomposition);
            mdds.setLength(k + minD);
            decomposition.setLength(k + minD);
            for (int d = 0; d < k + minD; d++) {
                int t = decomposition.get(d);
                Domains domains = CoinUtil.generateSystem(t);
                mdds.set(d, CoinChangeProblem.solveNoSym(MDD.create(), t, domains));
            }
            if (mdds.length() > 1) {
                mdd = Operation.concatenate(mdds);
                for (int j = 0; j < mdds.length; j++) {
                    Memory.free(mdds.get(j));
                    mdds.clear();
                }
            } else mdd = mdds.get(0);

            // If it's the first step, accumulator is simply the MDD
            if (accumulator == null) {
                accumulator = CoinChangeProblem.solveNoSym(MDD.create(), T, CoinUtil.generateSystemUniversal(mdd));
            }
            // Else, we compute the intersection between the accumulator
            // And the MDD
            ArrayList<int[]> solutions = accumulator.extractSolutions(10);
            for(int[] sol : solutions) System.out.println(Arrays.toString(sol));
            tmp = accumulator;
            roots.setLength(k + minD);
            int acc = 0;
            for (int j = 0; j < decomposition.length; j++) {
                roots.set(j, mdd.getLayer(acc).getNode());
                acc += decomposition.get(j);
            }
            accumulator = NDOperation.partiallyOrderedIntersection(NDMDD.create(), mdd, accumulator, roots);
            Memory.free(tmp);
            Memory.free(mdd);
        }
        MDD result = NDOperation.determinise(MDD.create(), (NDMDD) accumulator);
        Memory.free(accumulator);
        Memory.free(decomposition);
        Memory.free(mdds);

        return result;
    }

}
