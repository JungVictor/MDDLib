package dd.interfaces;

import dd.mdd.pmdd.components.properties.NodeProperty;
import structures.generics.MapOf;

public interface IPropertyNode extends INode {

    /**
     * Return a new node of the same type as this node implementing PropertyNodeInterface.
     * @return A new node implementing PropertyNodeInterface
     */
    IPropertyNode Node();

    /**
     * Transfer all properties to all children
     */
    void transferProperties();

    /**
     * Transfer all properties to all parents
     */
    void reverseTransferProperties();

    /**
     * Get the map associating name to property.
     * @return The map of all properties tied to their name
     */
    MapOf<String, NodeProperty> getProperties();

    /**
     * Check if the property with the given name is present in the node's properties
     * @param propertyName The name of the property
     * @return true if the node has the property, false otherwise
     */
    boolean hasProperty(String propertyName);

    /**
     * Get the property corresponding to the given name
     * @param propertyName The name of the property
     * @return The property if it exists, null otherwise
     */
    NodeProperty getProperty(String propertyName);

    /**
     * Add a property with the given name to the node
     * @param propertyName The name of the property
     * @param property The property
     */
    void addProperty(String propertyName, NodeProperty property);

    /**
     * Remove the property tied to the given name
     * @param propertyName The name of the property
     * @return the property that was removed (null if there is no property with this name)
     */
    NodeProperty removeProperty(String propertyName);

    /**
     * Clear all the properties held by the node.
     * Free them from memory
     */
    void clearProperties();


}
