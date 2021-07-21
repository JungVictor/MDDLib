package mdd.operations;

import mdd.components.Layer;
import mdd.components.Node;
import memory.*;
import structures.generics.SetOf;
import structures.lists.UnorderedListOfInt;
import structures.lists.ListOfLayer;
import structures.lists.UnorderedListOfNode;
import utils.Logger;

import java.util.*;

/**
 * <b>Structure of Pack used when performing the reduce operation on MDD.</b>
 */
public class Pack implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    static private final HashMap<Integer, UnorderedListOfNode> Va = new HashMap<>();
    static private final HashMap<Node, UnorderedListOfNode> Na = new HashMap<>();
    static private final UnorderedListOfNode Nlist = UnorderedListOfNode.create();
    static private final UnorderedListOfInt Vlist = UnorderedListOfInt.create();
    static private final Queue<Pack> Q = new LinkedList<>();
    static private final ArrayList<Node> M = new ArrayList<>();
    static private ListOfLayer LAYERS;

    private int pos, l;
    private final UnorderedListOfNode nodes = UnorderedListOfNode.create();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    // init             || add

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    public Pack(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Set the position and the depth of the Pack
     * @param pos Position of the pack
     * @param l Depth of the pack
     */
    public void init(int pos, int l){
        this.nodes.clear();
        this.pos = pos;
        this.l = l;
    }

    /**
     * Set the position and the depth of the Pack.
     * Add all nodes in the Layer to the pack
     * @param pos Position of the pack
     * @param l Depth of the pack
     * @param L Layer.
     */
    public void init(int pos, int l, Layer L){
        init(pos, l);
        if(L != null) for(Node node : L) nodes.add(node);
    }

    public static Pack create(int pos, int l, Layer L){
        Pack object = allocator().allocate();
        object.init(pos, l, L);
        return object;
    }

    /**
     * Add all nodes to the pack
     * @param nodes Collection of nodes
     */
    public void add(UnorderedListOfNode nodes){
        this.nodes.add(nodes);
    }


    //**************************************//
    //          STATIC FUNCTIONS            //
    //**************************************//
    // pReduce          || reduceLayer
    // reducePack

    /**
     * Function that perform the reduction of a MDD.
     * @param L Layers of the MDD
     * @param size Size of the MDD
     * @param V Values to consider when performing reduction
     */
    static public void pReduce(ListOfLayer L, int size, SetOf<Integer> V){
        Va.clear();
        for(int v : V) Va.put(v, UnorderedListOfNode.create());
        Na.clear();
        Vlist.clear();
        Nlist.clear();
        LAYERS = L;
        for(int i = size-2; i > 0; i--) {
            Logger.out.information("\rReducing layer : " + i);
            reduceLayer(L.get(i), i);
        }

        for(UnorderedListOfNode nodes : Va.values()) Memory.free(nodes);
        for(UnorderedListOfNode nodes : Na.values()) Memory.free(nodes);
        LAYERS = null;
    }

    /**
     * Reduce a layer
     * @param L Layer
     * @param i Depth of the layer
     */
    static private void reduceLayer(Layer L, int i){
        UnorderedListOfNode removed = UnorderedListOfNode.create();
        for(Node node : L) if(node.numberOfChildren() == 0) removed.add(node);
        for(Node node : removed) L.removeAndFree(node);
        Memory.free(removed);
        Pack p = Pack.create(0, i, L);

        Q.clear();
        reducePack(p);

        while(!Q.isEmpty()){
            p = Q.poll();
            reducePack(p);
            Memory.free(p);
        }
    }

    /**
     * Reduce nodes in the pack
     * @param p Pack
     */
    static private void reducePack(Pack p){
        int i = p.pos;
        M.clear();
        for(Node x : p.nodes){
            int v = x.getValue(i);
            if(Va.get(v).size() == 0) Vlist.add(v);
            Va.get(v).add(x);
        }
        for(int v : Vlist){
            for(Node x : Va.get(v)){
                Node y = x.getChildByIndex(i);
                if(!Na.containsKey(y)) Na.put(y, UnorderedListOfNode.create());
                if(Na.get(y).size() == 0) Nlist.add(x.getChild(v));
                Na.get(y).add(x);
            }
            Va.get(v).clear();
            for(Node y : Nlist){
                if(Na.get(y).size() > 1){
                    Pack p2 = Pack.create(i+1, p.l, null);
                    M.clear();
                    for(Node x : Na.get(y)) if(x.numberOfChildren() == i+1) M.add(x);
                    if(M.size() > 0) {
                        for (Node x : M) Na.get(y).removeElement(x);
                        Node ALPHA = M.get(0);
                        for (int m = 1; m < M.size(); m++) {
                            M.get(m).replaceReferencesBy(ALPHA);
                            LAYERS.get(p.l).removeAndFree(M.get(m));
                        }
                    }
                    p2.add(Na.get(y));
                    Q.add(p2);
                }
                Na.get(y).clear();
            }
            Nlist.clear();
        }
        Vlist.clear();
    }



    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free() {
        nodes.clear();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the Pack type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<Pack> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected Pack[] arrayCreation(int capacity) {
            return new Pack[capacity];
        }

        @Override
        protected Pack createObject(int index) {
            return new Pack(index);
        }
    }
}

