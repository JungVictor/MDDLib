package problems;

import builder.constraints.ConstraintBuilder;
import mdd.MDD;
import mdd.components.Layer;
import mdd.components.Node;
import mdd.operations.Operation;
import memory.Memory;
import representation.MDDPrinter;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import utils.Logger;

public class LCS {

    public static MDD solve(String[] words, SetOf<Integer> D){

        ArrayOfInt letters = Memory.ArrayOfInt(words[0].length());
        for (int j = 0; j < words[0].length(); j++) letters.set(j, words[0].charAt(j));

        MDD result = ConstraintBuilder.subset(Memory.MDD(), letters, D);
        MDD tmp = result;
        for(int i = 1; i < words.length; i++) {
            Logger.out.information(i+" " + (result.size()-1) + "\n");
            for (int j = 0; j < words[0].length(); j++) letters.set(j, words[i].charAt(j));

            MDD word = ConstraintBuilder.subset(Memory.MDD(), letters, D);
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
        while (result.getLayer(size).size() == 1) size--;
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
