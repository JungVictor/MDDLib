package mdd.components;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import representation.MDDVisitor;
import structures.generics.ArrayOf;

public class Node implements MemoryObject {

    public enum NodeType {
        SIMPLE_NODE, PROPERTY_NODE
    }

    // MemoryObject variables
    private final MemoryPool<Node> pool;
    private int ID = -1;
    //

    private OutArcs children;
    private InArcs parents;
    private ArrayOf<Node> associations;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public Node(MemoryPool<Node> pool) {
        this.pool = pool;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    /**
     * Accept a MDDVisitor. Used to represent the MDD.
     * @param visitor
     */
    public void accept(MDDVisitor visitor){
        visitor.visit(this);
        children.accept(visitor);
    }

    public NodeType getNodeType(){
        return NodeType.SIMPLE_NODE;
    }

    //**************************************//
    //               SETTERS                //
    //**************************************//

    public void associates(ArrayOf<Node> associations){
        if(this.associations.length() < associations.length()) this.associations.setLength(associations.length());
        for(int i = 0; i < associations.length(); i++) this.associations.set(i, associations.get(i));
    }

    public void associates(Node node1, Node node2){
        associations.set(0, node1);
        associations.set(1, node2);
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    public ArrayOf<Node> getAssociations(){
        return associations;
    }

    public Node getX1(){
        return associations.get(0);
    }

    public Node getX2(){
        return associations.get(1);
    }

    public Node getX(int i){
        return associations.get(i);
    }

    public Node getChild(int label){
        return children.get(label);
    }

    public Node getChildByIndex(int index){
        return children.getByIndex(index);
    }

    public int getValue(int index){
        return children.getValue(index);
    }

    public boolean containsLabel(int label){
        return children.contains(label);
    }

    public int numberOfChildren(){
        return children.size();
    }

    public OutArcs getChildren(){
        return children;
    }

    protected InArcs getParents(){
        return parents;
    }

    //**************************************//
    //           NODE MANAGEMENT            //
    //**************************************//

    public void remove(){
        for(int value : children) children.get(value).removeParent(value, this);
        for(int value : parents) for(Node node : parents.get(value)) node.removeChild(value);
    }

    public void clear(){
        children.clear();
        parents.clear();
        associations.clear();
    }


    //**************************************//
    //           ARCS MANAGEMENT            //
    //**************************************//

    public void addChild(int value, Node child){
        this.children.add(value, child);
    }

    public void removeChild(int value){
        this.children.remove(value);
    }

    public void addParent(int value, Node parent){
        this.parents.add(value, parent);
    }

    public void removeParent(int value, Node parent){
        this.parents.remove(value, parent);
    }

    public void replaceParentsReferencesBy(Node node){
        for(int value : parents){
            for(Node parent : parents.get(value)) {
                parent.addChild(value, node);
                node.addParent(value, parent);
            }
        }
        parents.clear();
    }

    public void replaceChildrenReferencesBy(Node node){
        for(Integer value : children){
            Node child = children.get(value);
            child.removeParent(value, this);
            child.addParent(value, node);
            node.addChild(value, child);
        }
        children.clear();
    }

    public void replaceReferencesBy(Node node){
        replaceChildrenReferencesBy(node);
        replaceParentsReferencesBy(node);
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        children = Memory.OutArcs();
        parents = Memory.InArcs();
        associations = Memory.ArrayOfNode(2);
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        Memory.free(children);
        Memory.free(parents);
        Memory.free(associations);
        this.pool.free(this, ID);
    }
}
