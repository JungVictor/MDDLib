package pmdd.components;

import mdd.components.Node;
import memory.Memory;
import memory.MemoryPool;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import structures.generics.MapOf;

public class PNode extends Node {

    private final MapOf<String, NodeProperty> properties = new MapOf<>(null);

    public PNode(MemoryPool<Node> pool) {
        super(pool);
    }

    @Override
    public NodeType getNodeType(){
        return NodeType.PROPERTY_NODE;
    }

    @Override
    public Node Node(){
        return PMemory.PNode();
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
        for(String property : properties) Memory.free(properties.get(property));
        properties.clear();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare(){
        super.prepare();
        properties.clear();
    }

    @Override
    public void free(){
        super.free();
        clearProperties();
    }

}
