package mdd;

import mdd.components.Layer;
import mdd.components.Node;
import mdd.operations.Pack;
import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import representation.MDDVisitor;
import structures.generics.ListOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

import java.util.ArrayList;
import java.util.Set;

public class MDD implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<MDD> pool;
    private int ID = -1;
    //

    // Root node
    private final Node root = Memory.Node();
    private Node tt;

    // Layers
    private final ListOf<Layer> L = Memory.ListOfLayer();
    private int size;

    // Domain of the MDD
    private final SetOf<Integer> V = Memory.SetOfInteger();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public MDD(MemoryPool<MDD> pool){
        this.pool = pool;
        L.add(Memory.Layer());
        L.get(0).add(root);
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    /**
     * Accept a MDDVisitor (design pattern).
     * Mainly used to represent a MDD
     * @param visitor MDDVisitor
     */
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
        for(int i = 0; i < size; i++) getLayer(i).accept(visitor);
    }

    //**************************************//
    //               GETTERS                //
    //**************************************//

    public Node getRoot(){
        return root;
    }

    public Node getTt(){
        return tt;
    }

    public Layer getLayer(int i){
        return L.get(i);
    }

    public int getSize(){
        return size;
    }

    public SetOf<Integer> getV(){
        return V;
    }


    //**************************************//
    //               SETTERS                //
    //**************************************//

    public void setSize(int size){
        if(this.L.size() < size) for(int i = 0, diff = size - L.size(); i < diff; i++) L.add(Memory.Layer());
        this.size = size;
    }

    public void addValue(int v){
        V.add(v);
    }



    //**************************************//
    //             MANAGE MDD               //
    //**************************************//

    public void addPath(int... values){
        Node current = getRoot();
        for(int i = 0; i < values.length; i++) {
            int v = values[i];
            if(current.containsLabel(v)) current = current.getChild(v);
            else{
                Node next = Memory.Node();
                addArcAndNode(current, v, next, i+1);
                current = next;
            }
        }
    }

    public void addNode(Node node, int layer){
        getLayer(layer).add(node);
    }

    public void removeNode(Node node, int layer){
        getLayer(layer).remove(node);
    }

    public void addArc(Node source, int value, Node destination){
        source.addChild(value, destination);
        destination.addParent(value, source);
        addValue(value);
    }

    public void addArcAndNode(Node source, int value, Node destination, int layer){
        addArc(source, value, destination);
        addNode(destination, layer);
    }

    public void reduce(int size, boolean remove){
        // Remove all useless nodes (without child or parent)
        if(remove) {
            ListOf<Node> layer = Memory.ListOfNode();
            for (int i = size - 2; i > 0; i--) {
                layer.add(getLayer(i).getNodes());
                for (Node node : layer) if (node.numberOfChildren() == 0) removeNode(node, i);
                layer.clear();
            }
            Memory.free(layer);
        }

        // Merge the leaves of the MDD into a tt node
        if(getLayer(size - 1).size() == 1) {
            this.tt = getLayer(size - 1).getNode();
            return;
        }
        Node newTT = Memory.Node();
        for(Node leaf : getLayer(size - 1)) leaf.replaceReferencesBy(newTT);

        getLayer(size - 1).clear();
        getLayer(size - 1).add(newTT);
        this.tt = newTT;

        // Merge similar nodes
        if(getLayer(size - 1).size() != 0) Pack.pReduce(L, size, V);
    }

    public void reduce(){
        reduce(size, true);
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface
    @Override
    public void prepare() {}

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        for(int i = 1; i < size; i++){
            getLayer(i).freeAllNodes();
            Memory.free(getLayer(i));
        }
        Layer L0 = getLayer(0);
        L.clear();
        L.add(L0);
        V.clear();
        root.clear();
        this.pool.free(this, ID);
    }

    @Override
    public boolean isAtomic() {
        return false;
    }
}
