import builder.MDDBuilder;
import mdd.MDD;
import memory.Memory;
import problems.LCS;
import representation.MDDPrinter;
import structures.generics.SetOf;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        String alphabet = "ABCDEFGH";
        int nWords = 100;
        int wSize = 50;

        if(args.length > 0) alphabet = args[0];
        if(args.length > 1) nWords = Integer.parseInt(args[1]);
        if(args.length > 2) wSize = Integer.parseInt(args[2]);

        // ==================================================
        Random random = new Random();
        SetOf<Integer> D = Memory.SetOfInteger();
        for(int i = 0; i < alphabet.length(); i++) D.add((int) alphabet.charAt(i));


        String[] words = new String[nWords];
        for(int i = 0; i < words.length; i++) {
            StringBuilder builder = new StringBuilder();
            for(int j = 0; j < wSize; j++) builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
            words[i] = builder.toString();
        }

        MDD result = LCS.solve(words, D);
        result.accept(new MDDPrinter(true));
    }

}
