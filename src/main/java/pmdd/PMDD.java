package pmdd;

import mdd.MDD;
import mdd.components.Node;
import memory.Memory;
import memory.MemoryPool;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import structures.generics.MapOf;

public class PMDD extends MDD {

    public PMDD(MemoryPool<MDD> pool) {
        super(pool);
    }

    @Override
    public Node Node(){
        if(getRoot() != null) return getRoot().Node();
        return PMemory.PNode();
    }

    @Override
    public MDD MDD(){
        return PMemory.PMDD();
    }

    //**************************************//
    //              PROPERTIES              //
    //**************************************//

    public MapOf<String, NodeProperty> propagateProperties(){
        for(int i = 0; i < size() - 1; i++){
            for(Node node : getLayer(i)) {
                ((PNode) node).transferProperties();
                ((PNode) node).clearProperties();
            }
        }
        return ((PNode) getTt()).getProperties();
    }

    public void addRootProperty(String propertyName, NodeProperty property){
        ((PNode) getRoot()).addProperty(propertyName, property);
    }

    public NodeProperty removeRootProperty(String propertyName){
        return ((PNode) getRoot()).removeProperty(propertyName);
    }

    public NodeProperty getTTProperty(String propertyName){
        return ((PNode) getTt()).getProperty(propertyName);
    }

}
