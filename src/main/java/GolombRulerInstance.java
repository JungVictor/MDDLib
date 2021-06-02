import mdd.MDD;
import memory.Memory;
import problems.GolombRuler;
import representation.MDDPrinter;

public class GolombRulerInstance {

    public static void main(String[] args) {
        MDD result = GolombRuler.generate(Memory.MDD(), 6, 4);
        result.accept(new MDDPrinter());
    }

}
