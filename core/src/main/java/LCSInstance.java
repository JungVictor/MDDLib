import dd.mdd.MDD;
import memory.Memory;
import problems.LCS;
import representation.MDDPrinter;
import structures.generics.SetOf;
import utils.ArgumentParser;

import java.util.Random;

public class LCSInstance {

    public static void main(String[] args) {

        ArgumentParser parser = new ArgumentParser("-alphabet", "ABCD", "-nwords", "1000", "-wsize", "100");
        parser.read(args);

        String alphabet = parser.get("-alphabet");
        int nWords = Integer.parseInt(parser.get("-nwords"));
        int wSize  = Integer.parseInt(parser.get("-wsize"));

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
