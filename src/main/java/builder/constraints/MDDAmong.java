package builder.constraints;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import structures.generics.ArrayOf;

public class MDDAmong {

    private MDDAmong(){}

    public static MDD generate(MDD mdd, int q, int min, int max){
        mdd.setSize(q+1);
        ArrayOf<Node>[] nodes = new ArrayOf[q+1];
        for(int i = 0; i < q+1; i++) nodes[i] = Memory.ArrayOfNode(i+1);
        nodes[0].set(0, mdd.getRoot());
        for(int i = 0; i < q; i++){
            for(int n = 0; n < i+1; n++){
                if(nodes[i].get(n) == null) continue;
                int max_possible = q-i+n;
                if(max_possible > min) {
                    if(nodes[i+1].get(n) == null) nodes[i+1].set(n, mdd.Node());
                    mdd.addArcAndNode(nodes[i].get(n), 0, nodes[i+1].get(n), i+1);
                }
                if(n < max) {
                    if(nodes[i+1].get(n+1) == null) nodes[i+1].set(n+1, mdd.Node());
                    mdd.addArcAndNode(nodes[i].get(n), 1, nodes[i+1].get(n+1), i+1);
                }
            }
        }
        for(ArrayOf<Node> array : nodes) Memory.free(array);
        mdd.addValue(0); mdd.addValue(1);
        mdd.reduce();
        return mdd;
    }
}