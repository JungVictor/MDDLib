package builder.constraints;

import dd.mdd.MDD;
import dd.mdd.components.Node;
import structures.arrays.ArrayOfInt;

public class MDDUniversal {

    private MDDUniversal(){}

    public static MDD generate(MDD mdd, ArrayOfInt V, int size) {
        mdd.setSize(size+1);
        Node current = mdd.getRoot();
        Node next;

        for(int i = 1; i < size+1; i++){
            next = mdd.Node();
            mdd.addNode(next, i);
            for(int v : V) mdd.addArc(current, v, next, i-1);
            current = next;
        }

        mdd.reduce();
        return mdd;
    }

}
