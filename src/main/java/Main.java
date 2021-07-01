import builder.MDDBuilder;
import mdd.MDD;
import memory.Memory;
import representation.MDDPrinter;
import structures.generics.SetOf;

public class Main {

    public static void main(String[] args) {
        SetOf<Integer> V = Memory.SetOfInteger();
        for(int i = 0; i < 2; i++) V.add(i);
        MDD sum = MDDBuilder.sum(Memory.MDD(), 1, 3, 4, V);
        MDDPrinter printer = new MDDPrinter();
        sum.accept(printer);
    }

}
