import mdd.MDD;
import memory.Memory;
import problems.GolombRuler;
import representation.MDDPrinter;
import utils.ArgumentParser;

public class GolombRulerInstance {

    public static void main(String[] args) {

        ArgumentParser parser = new ArgumentParser("-domain", "85", "-size", "12");
        parser.read(args);

        int domain = Integer.parseInt(parser.get("-domain"));
        int size = Integer.parseInt(parser.get("-size"));

        MDD result = GolombRuler.generate(Memory.MDD(), domain, size);
        result.accept(new MDDPrinter());
    }

}
