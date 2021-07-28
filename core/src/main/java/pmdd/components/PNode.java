package pmdd.components;

import mdd.components.Node;
import memory.AllocatorOf;
import memory.Memory;
import pmdd.components.properties.NodeProperty;
import structures.generics.MapOf;

/**
 * <b>The Node containing properties</b> <br>
 * It extends the basic function of Node and add functions relative to properties
 */
public class PNode extends Node {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private final MapOf<String, NodeProperty> properties = new MapOf<>(null);

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    private static Allocator allocator(){
        return localStorage.get();
    }

    public static PNode create(){
        PNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    public PNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public Node Node(){
        return create();
    }

    //**************************************//
    //              PROPERTIES              //
    //**************************************//

    /**
     * Transfer all properties to all children
     */
    public void transferProperties(){
        for(String property : properties.keySet()) {
            for (Integer label : getChildren()) {
                PNode child = (PNode) getChild(label);
                if (child.hasProperty(property)) getProperty(property).mergeWithProperty(label, child.getProperty(property));
                else child.addProperty(property, getProperty(property).createProperty(label));
            }
        }
    }

    /**
     * Get the map of name -> property.
     * @return The map of all properties tied to their name
     */
    public MapOf<String, NodeProperty> getProperties(){
        return properties;
    }

    /**
     * Check if the property with the given name is present in the node's properties
     * @param propertyName The name of the property
     * @return true if the node has the property, false otherwise
     */
    public boolean hasProperty(String propertyName){
        return properties.get(propertyName) != null;
    }

    /**
     * Get the property corresponding to the given name
     * @param propertyName The name of the property
     * @return The property if it exists, null otherwise
     */
    public NodeProperty getProperty(String propertyName){
        return properties.get(propertyName);
    }

    /**
     * Add a property with the given name to the node
     * @param propertyName The name of the property
     * @param property The property
     */
    public void addProperty(String propertyName, NodeProperty property){
        properties.put(propertyName, property);
    }

    /**
     * Remove the property tied to the given name
     * @param propertyName The name of the property
     * @return the property that was removed (null if there is no property with this name)
     */
    public NodeProperty removeProperty(String propertyName){
        return properties.remove(propertyName);
    }

    /**
     * Clear all the properties held by the node.
     * Free them from memory
     */
    public void clearProperties(){
        for(String property : properties) if(properties.get(property) != null) Memory.free(properties.get(property));
        properties.clear();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    protected void dealloc(){
        allocator().free(this);
    }

    @Override
    public void prepare(){
        super.prepare();
        properties.clear();
    }

    @Override
    public void free(){
        clearProperties();
        super.free();
    }

    /**
     * <b>The allocator that is in charge of the PNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected PNode[] arrayCreation(int capacity) {
            return new PNode[capacity];
        }

        @Override
        protected PNode createObject(int index) {
            return new PNode(index);
        }
    }

}
