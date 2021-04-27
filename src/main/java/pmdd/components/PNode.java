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

    public MapOf<String, NodeProperty> getProperties(){
        return properties;
    }

    public boolean hasProperty(String propertyName){
        return properties.get(propertyName) != null;
    }

    public NodeProperty getProperty(String propertyName){
        return properties.get(propertyName);
    }

    public void addProperty(String propertyName, NodeProperty property){
        properties.put(propertyName, property);
    }

    public NodeProperty removeProperty(String propertyName){
        return properties.remove(propertyName);
    }

    public void clearProperties(){
        for(String property : properties) Memory.free(properties.get(property));
        properties.clear();
    }

    @Override
    public void prepare(){
        super.prepare();
        properties.clear();
    }

}
