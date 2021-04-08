package mdd;

import mdd.components.Layer;
import mdd.components.Node;
import mdd.operations.Pack;
import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import representation.MDDVisitor;
import structures.generics.ListOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

import java.util.HashMap;
import java.util.Set;

public class MDD implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<MDD> pool;
    private int ID = -1;
    //

    // Root node
    private final Node root;
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
        root = Node();
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

    public Node Node(){
        return Memory.Node();
    }

    public MDD MDD(){
        return Memory.MDD();
    }

    public MDD copy(MDD copy, int offset, int start, int stop){
        for(int i = start; i < stop; i++){
            for(Node original : getLayer(i)) {
                Node copyNode = original.getX1();
                for(int arc : original.getChildren()){
                    Node child = original.getChild(arc);
                    Node copyChild = child.getX1();
                    // Child node is not yet copied
                    if(copyChild == null) {
                        copyChild = copy.Node();
                        child.associates(copyChild, null);
                        copy.addNode(copyChild, i+1+offset);
                    }
                    copy.addArc(copyNode, arc, copyChild);
                }
                original.associates(null, null);
            }
        }
        return copy;
    }

    public MDD copy(MDD copy, Node root, int offset){
        getRoot().associates(root, null);
        copy(copy, offset, 0, size());
        copy.setTT();
        return copy;
    }

    public MDD copy(MDD copy){
        copy.setSize(size());
        return copy(copy, copy.getRoot(), 0);
    }

    public MDD copy(){
        return copy(MDD());
    }

    public void clearAllAssociations(){
        for(int i = 0; i < size(); i++) for(Node node : getLayer(i)) node.clearAssociations();
    }

    // TODO : Implementation of replace
    public void replace(MapOf<Integer, SetOf<Integer>> values){
        MapOf<Integer, Node> V = Memory.MapOfIntegerNode();
        SetOf<Integer> setV = Memory.SetOfInteger();
        for(int i = 0; i < size-1; i++){
            for(Node node : getLayer(i)) {
                setV.clear();
                V.clear();
                setV.add(node.getValues());
                for(int v : setV) {
                    V.put(v, node.getChild(v));
                    node.getChild(v).removeParent(v, node);
                    node.removeChild(v);
                }
                for(int v : V) {
                    Node child = V.get(v);
                    for(int value : values.get(v)) addArc(node, value, child);
                }
            }
        }
        Memory.free(V);
        Memory.free(setV);
    }

    public void replace(SetOf<Integer> V0, SetOf<Integer> V1){
        MapOf<Integer, SetOf<Integer>> values = Memory.MapOfIntegerSetOfInteger();
        values.put(0, V0);
        values.put(1, V1);
        replace(values);
        Memory.free(values);
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

    public int size(){
        return size;
    }

    public SetOf<Integer> getV(){
        return V;
    }

    public int nodes(){
        int n = 0;
        for(int i = 0; i < size; i++) n += getLayer(i).size();
        return n;
    }

    public int arcs(){
        int n = 0;
        for(int i = 0; i < size; i++) for(Node x : getLayer(i)) n += x.numberOfChildren();
        return n;
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
                Node next = Node();
                addArcAndNode(current, v, next, i+1);
                current = next;
            }
        }
    }

    public void addNode(Node node, int layer){
        getLayer(layer).add(node);
    }

    public void removeNode(Node node, int layer){
        getLayer(layer).removeAndFree(node);
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
        // Merge the leaves of the MDD into a tt node
        if(getLayer(size - 1).size() == 1) this.tt = getLayer(size - 1).getNode();
        else {
            Node newTT = Node();
            for (Node leaf : getLayer(size - 1)) leaf.replaceReferencesBy(newTT);
            getLayer(size - 1).clear();
            getLayer(size - 1).add(newTT);
            this.tt = newTT;
        }

        // Merge similar nodes
        if(getLayer(size - 2).size() != 0) Pack.pReduce(L, size, V);
    }

    public void reduce(){
        reduce(size, true);
    }

    private void setTT(){
        if(getLayer(size - 1).size() == 1) this.tt = getLayer(size - 1).getNode();
        else {
            Node newTT = Node();
            for (Node leaf : getLayer(size - 1)) leaf.replaceReferencesBy(newTT);

            getLayer(size - 1).clear();
            getLayer(size - 1).add(newTT);
            this.tt = newTT;
        }
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        Layer L0 = getLayer(0);
        L.clear();
        L.add(L0);
        V.clear();
        root.clear();
        this.tt = null;
    }

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
        prepare();
        this.pool.free(this, ID);
    }

    @Override
    public boolean isAtomic() {
        return false;
    }
}
