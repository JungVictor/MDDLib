package problems;

import builder.constraints.ConstraintBuilder;
import mdd.MDD;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Memory;
import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;
import utils.Logger;

public class LCS {

    public static MDD solve(String[] words, SetOf<Integer> D){

        ArrayOfInt letters = ArrayOfInt.create(words[0].length());
        for (int j = 0; j < words[0].length(); j++) letters.set(j, words[0].charAt(j));

        MDD result = ConstraintBuilder.subset(MDD.create(), letters, D);
        MDD tmp = result;
        for(int i = 1; i < words.length; i++) {
            Logger.out.information(i+" " + (result.size()-1) + "\n");
            for (int j = 0; j < words[0].length(); j++) letters.set(j, words[i].charAt(j));

            MDD word = ConstraintBuilder.subset(MDD.create(), letters, D);
            Logger.out.information(word.nSolutions() + "\n");
            result = Operation.intersection(result, word);
            Memory.free(tmp);
            Memory.free(word);
            tmp = result;
            reduce(result);
        }
        removeEpsilon(result);
        result.reduce();
        return result;
    }

    private static void reduce(MDD result){
        int size = result.size() - 1;
        while (result.getLayer(size).size() == 1 && size > 0) size--;
        result.setSize(size+2);
    }

    private static void removeEpsilon(MDD result){
        for(int i =  0; i < result.size(); i++){
            for(Node node : result.getLayer(i)) {
                if(node.containsLabel(-1)) node.removeChild(-1);
            }
        }
    }

}
