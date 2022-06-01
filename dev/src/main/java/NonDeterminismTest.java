import dd.mdd.MDD;
import dd.mdd.nondeterministic.NDMDD;
import dd.mdd.nondeterministic.NDOperation;
import dd.mdd.nondeterministic.components.NDNode;
import representation.MDDPrinter;
import structures.arrays.ArrayOfNodeInterface;

public class NonDeterminismTest {

    public static void main(String[] args) {
        intersection();
    }

    private static void determinise(){
        NDMDD ndmdd = NDMDD.create();
        ndmdd.setSize(4);

        NDNode R = ndmdd.getRoot();
        NDNode A = NDNode.create();
        NDNode B = NDNode.create();
        NDNode C = NDNode.create();
        NDNode D = NDNode.create();
        NDNode tt = NDNode.create();

        ndmdd.addNode(A, 1);
        ndmdd.addNode(B, 1);
        ndmdd.addNode(C, 2);
        ndmdd.addNode(D, 2);
        ndmdd.addNode(tt, 3);

        int i = 0;
        ndmdd.addArc(R, 1, A, i);
        ndmdd.addArc(R, 2, A, i);
        ndmdd.addArc(R, 2, B, i);

        i++;
        ndmdd.addArc(A, 1, C, i);
        ndmdd.addArc(B, 1, C, i);
        ndmdd.addArc(B, 2, C, i);
        ndmdd.addArc(B, 2, D, i);

        i++;
        ndmdd.addArc(C, 1, tt, i);
        ndmdd.addArc(D, 2, tt, i);

        MDD result = MDD.create();

        NDOperation.determinise(result, ndmdd);

        result.accept(new MDDPrinter());
    }

    private static void intersection(){

        MDD partiallyOrdered = MDD.create();
        partiallyOrdered.setSize(7);

        partiallyOrdered.addPath(2, 1, 1, 2, 2, 1);
        partiallyOrdered.addPath(2, 1, 1, 0, 0, 0);
        partiallyOrdered.addPath(2, 1, 1, 1, 1, 1);
        partiallyOrdered.addPath(2, 2, 1, 2, 2, 1);
        partiallyOrdered.addPath(2, 2, 1, 0, 0, 0);
        partiallyOrdered.addPath(2, 2, 1, 1, 1, 1);
        partiallyOrdered.addPath(1, 1, 1, 2, 2, 1);
        partiallyOrdered.addPath(1, 1, 1, 0, 0, 0);
        partiallyOrdered.addPath(1, 1, 1, 1, 1, 1);


        MDD ordered = MDD.create();
        ordered.setSize(7);

        ordered.addPath(2, 2, 2, 1, 1, 1);

        NDMDD result = NDMDD.create();

        ArrayOfNodeInterface roots = ArrayOfNodeInterface.create(2);
        roots.set(0, partiallyOrdered.getRoot());
        roots.set(1, partiallyOrdered.getLayer(3).getNode());

        NDOperation.partiallyOrderedIntersection(result, partiallyOrdered, ordered, roots);

        MDD determinist = NDOperation.determinise(MDD.create(), result);

        determinist.accept(new MDDPrinter());
    }

}
