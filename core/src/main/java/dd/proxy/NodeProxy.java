package dd.proxy;

import dd.AbstractNode;
import dd.interfaces.NodeInterface;
import memory.AllocatorOf;
import memory.Memory;
import structures.arrays.ArrayOfNodeInterface;
import structures.generics.MapOf;
import structures.successions.SuccessionOfNodeInterface;

public class NodeProxy extends AbstractNode {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    // The decision diagram
    private DDProxy parentDD;

    private MapOf<Integer, Integer> children;
    private boolean loaded;

    private static Allocator allocator(){ return localStorage.get(); }

    // Private constructor
    private NodeProxy(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public static NodeProxy create(DDProxy parentDD){
        NodeProxy node = allocator().allocate();
        node.parentDD = parentDD;
        node.children = Memory.MapOfIntegerInteger();
        node.loaded = false;
        return node;
    }

    public void setLoaded(boolean loaded){
        this.loaded = loaded;
    }

    @Override
    public NodeInterface Node() {
        return create(parentDD);
    }

    @Override
    public NodeInterface getChild(int label) {
        if(!loaded) {
            parentDD.loadNodes();
        }
        if(children.get(label) == null) {
            return null;
        }
        return parentDD.getNode(children.get(label));
    }

    @Override
    public boolean containsLabel(int label){
        return getChild(label) != null;
    }

    @Override
    public int numberOfChildren() {
        return children.size();
    }

    @Override
    public Iterable<Integer> iterateOnChildLabel() {
        return children.keySet();
    }

    protected void addChild(int label, int child){
        children.put(label, child);
    }

    @Override
    public void clearChildren() {
        children.clear();
    }

    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public void free() {
        // clean all references and
        // free the variables you have to
        Memory.free(children);
        parentDD = null;
        allocator().free(this); // Free the object
    }

    // =======================================

    @Override
    public void addChild(int label, NodeInterface child) {
        throw new UnsupportedOperationException("NodeProxy only support having ID as children");
    }

    @Override
    public void setX(NodeInterface node, int i) {
        throw new UnsupportedOperationException("NodeProxy are read only !");
    }

    @Override
    public void associate(ArrayOfNodeInterface nodes) {
        throw new UnsupportedOperationException("NodeProxy are read only !");
    }

    @Override
    public void associate(NodeInterface x1, NodeInterface x2) {
        throw new UnsupportedOperationException("NodeProxy are read only !");
    }

    @Override
    public SuccessionOfNodeInterface getAssociations() {
        throw new UnsupportedOperationException("NodeProxy are read only !");
    }

    @Override
    public NodeInterface getX(int i) {
        throw new UnsupportedOperationException("NodeProxy are read only !");
    }

    @Override
    public void removeChild(int label) {
        throw new UnsupportedOperationException("NodeProxy are read only !");
    }

    @Override
    public void clearAssociations() {
        throw new UnsupportedOperationException("NodeProxy are read only !");
    }

    @Override
    public Iterable<NodeInterface> iterateOnParents(int label) {
        throw new UnsupportedOperationException("NodeProxy does not have access to the parents !");
    }

    @Override
    public Iterable<Integer> iterateOnParentLabel() {
        throw new UnsupportedOperationException("NodeProxy does not have access to the parents !");
    }

    @Override
    public int numberOfParentsLabel() {
        throw new UnsupportedOperationException("NodeProxy does not have access to the parents !");
    }

    @Override
    public int numberOfParents(int label) {
        throw new UnsupportedOperationException("NodeProxy does not have access to the parents !");
    }

    @Override
    public void addParent(int label, NodeInterface parent) {
        throw new UnsupportedOperationException("NodeProxy does not have access to the parents !");
    }

    @Override
    public void removeParent(int label, NodeInterface parent) {
        throw new UnsupportedOperationException("NodeProxy does not have access to the parents !");
    }

    @Override
    public void clearParents() {
        throw new UnsupportedOperationException("NodeProxy does not have access to the parents !");
    }


    static final class Allocator extends AllocatorOf<NodeProxy> {

        // You can specify the initial capacity. Default : 10.
        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected NodeProxy[] arrayCreation(int capacity) {
            return new NodeProxy[capacity];
        }

        @Override
        protected NodeProxy createObject(int index) {
            return new NodeProxy(index);
        }
    }

}
