import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import representation.MDDPrinter;

public class Main {

    public static void main(String[] args) {
        MDD mdd = Memory.MDD();
        mdd.setSize(6);
        mdd.addPath(0, 1, 2, 3, 4);
        mdd.addPath(0, 2, 2, 3, 4);
        mdd.addPath(0, 2, 2, 5, 4);
        mdd.reduce();

        MDD mdd2 = Memory.MDD();
        mdd2.setSize(6);
        mdd2.addPath(0, 1, 2, 3, 4);
        mdd2.addPath(4, 3, 2, 1, 0);
        mdd2.reduce();

        MDD mdd3 = Operation.intersection(mdd, mdd2);

        mdd3.accept(new MDDPrinter());

        Memory.free(mdd);
        Memory.free(mdd2);
        Memory.free(mdd3);


        mdd = Memory.MDD();
        mdd.setSize(5);
        mdd.addPath(8, 1, 2, 5);
        mdd.reduce();

        mdd.accept(new MDDPrinter());
    }

}
