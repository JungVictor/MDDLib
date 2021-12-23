package dd.operations;

import dd.bdd.BDD;
import dd.bdd.components.BinaryNode;
import dd.mdd.MDD;
import dd.mdd.components.Layer;
import dd.mdd.components.Node;
import memory.Memory;
import structures.generics.MapOf;
import structures.lists.UnorderedListOfBinaryNode;
import structures.lists.UnorderedListOfNode;
import utils.Logger;

public class HashReduce {

    private final static MapOf<String, UnorderedListOfNode> packs = new MapOf<>(null);
    private final static MapOf<BinaryNode, Integer> ID = new MapOf<>(null);
    private final static MapOf<Long, UnorderedListOfBinaryNode> nodes = new MapOf<>(null);

    public static void reduce(MDD mdd){
        for(int i = mdd.size()-2; i > 0; i--) {
            Logger.out.information("\rReducing layer : " + i);
            reduceLayer(mdd.getLayer(i));
        }
    }

    public static void reduce(BDD bdd){
        UnorderedListOfBinaryNode L;
        // Assigning each node to an ID
        for(int i = 0; i < bdd.size(); i++) {
            L = bdd.getLayer(i);
            for(int idx = 0; idx < L.size(); idx++) ID.put(L.get(idx), idx+1);
        }
        ID.put(null, 0);

        for(int i = bdd.size()-2; i > 0; i--) {
            Logger.out.information("\rReducing layer : " + i);
            reduceLayer(bdd, i);
        }

        ID.clear();
    }

    //**************************************//
    //                 BDDs                 //
    //**************************************//

    private static void reduceLayer(BDD bdd, int layer){
        long hash;
        UnorderedListOfBinaryNode L = bdd.getLayer(layer);
        UnorderedListOfBinaryNode removed = UnorderedListOfBinaryNode.create();
        for(BinaryNode node : L) {
            if(node.numberOfChildren() == 0) removed.add(node);
        }
        for(BinaryNode node : removed) bdd.removeNode(node, layer);
        Memory.free(removed);

        for(BinaryNode node : L) {
            hash = hash(node, L.size());
            if(!nodes.contains(hash)) nodes.put(hash, UnorderedListOfBinaryNode.create());
            nodes.get(hash).add(node);
        }
        for (UnorderedListOfBinaryNode pack : nodes.values()) {
            BinaryNode ALPHA = pack.get(0);
            for (int m = 1; m < pack.size(); m++) {
                pack.get(m).replaceReferencesBy(ALPHA);
                bdd.removeNode(pack.get(m), layer);
            }
            Memory.free(pack);
        }
        nodes.clear();
    }

    private static long hash(BinaryNode node, int layerSize){
        return (long) ID.get(node.getChild(0)) * layerSize + ID.get(node.getChild(1));
    }


    //**************************************//
    //                 MDDs                 //
    //**************************************//

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
