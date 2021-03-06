package dd.mdd.pmdd.components;

import dd.mdd.components.Node;
import dd.interfaces.IPropertyNode;
import memory.AllocatorOf;
import memory.Memory;
import dd.mdd.pmdd.components.properties.NodeProperty;
import structures.generics.MapOf;

/**
 * <b>The Node containing properties</b> <br>
 * It extends the basic function of Node and add functions relative to properties
 */
public class PropertyNode extends Node implements IPropertyNode {

    // Allocable variables
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private final MapOf<String, NodeProperty> properties = new MapOf<>(null);

    // Pruning
    // TODO : replace by TupleOfDouble ?
    public double[] value = {1, 0};

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    public PropertyNode(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a PNode
     * The object is managed by the allocator.
     * @return A fresh PNode
     */
    public static PropertyNode create(){
        PropertyNode node = allocator().allocate();
        node.prepare();
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyNode Node(){
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
            NodeProperty parentProperty = getProperty(property);
            if(parentProperty == null) continue;
            for (Integer label : getChildren()) {
                PropertyNode child = (PropertyNode) getChild(label);
                if (child.hasProperty(property)) parentProperty.mergeWithProperty(label, child.getProperty(property));
                else child.addProperty(property, parentProperty.createProperty(label));
            }
        }
    }

    /**
     * Transfer all properties to all parents
     */
    public void reverseTransferProperties(){
        for(String property : properties.keySet()) {
            for (Integer label : getParents()) {
                for (Node p : getParents().get(label)) {
                    PropertyNode parent = (PropertyNode) p;
                    if (parent.hasProperty(property))
                        getProperty(property).mergeWithProperty(label, parent.getProperty(property));
                    else parent.addProperty(property, getProperty(property).createProperty(label));
                }
            }
        }
    }

    /**
     * Get the map associating name to property.
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(){
        super.prepare();
        properties.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){
        clearProperties();
        value[0] = 1;
        value[1] = 0;
        super.free();
    }

    /**
     * <b>The allocator that is in charge of the PNode type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PropertyNode> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertyNode[] arrayCreation(int capacity) {
            return new PropertyNode[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PropertyNode createObject(int index) {
            return new PropertyNode(index);
        }
    }

}
