import mdd.MDD;
import memory.Memory;
import problems.GolombRuler;
import representation.MDDPrinter;

public class GolombRulerInstance {

    public static void main(String[] args) {
        int domain = 85;
        int size = 12;

        if(args.length >= 1) domain = Integer.parseInt(args[0]);
        if(args.length >= 2) size = Integer.parseInt(args[1]);

        MDD result = GolombRuler.generate(Memory.MDD(), domain, size);
        result.accept(new MDDPrinter());
    }

}
