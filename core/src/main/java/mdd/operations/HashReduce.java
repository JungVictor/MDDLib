package mdd.operations;

import mdd.MDD;
import mdd.components.Layer;
import mdd.components.Node;
import memory.Memory;
import structures.Signature;
import structures.generics.MapOf;
import structures.lists.UnorderedListOfNode;
import utils.Logger;

public class HashReduce {

    private final static MapOf<String, UnorderedListOfNode> packs = new MapOf<>(null);

    public static void reduce(MDD mdd){
        for(int i = mdd.size()-2; i > 0; i--) {
            Logger.out.information("\rReducing layer : " + i);
            reduceLayer(mdd.getLayer(i));
        }
    }

    private static void reduceLayer(Layer L){
        String hash;
        UnorderedListOfNode removed = UnorderedListOfNode.create();
        for(Node node : L) {
            if(node.numberOfChildren() == 0) removed.add(node);
            else node.sortChildren();
        }
        for(Node node : removed) L.removeAndFree(node);
        Memory.free(removed);

        for(Node node : L) {
            hash = hash(node);
            if(!packs.contains(hash)) packs.put(hash, UnorderedListOfNode.create());
            packs.get(hash).add(node);
        }
        for (UnorderedListOfNode pack : packs.values()) {
            Node ALPHA = pack.get(0);
            for (int m = 1; m < pack.size(); m++) {
                pack.get(m).replaceReferencesBy(ALPHA);
                L.removeAndFree(pack.get(m));
            }
            Memory.free(pack);
        }
        packs.clear();
    }

    private static String hash(Node node){
        StringBuilder builder = new StringBuilder();
        builder.append(node.numberOfChildren());
        builder.append("/");
        for(int label : node.getChildren()) {
            builder.append(label);
            builder.append(",");
            builder.append(node.getChild(label).allocatedIndex());
            builder.append(";");
        }
        return builder.toString();
    }

}
