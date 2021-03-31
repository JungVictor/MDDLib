package pmdd.memory;

import memory.MemoryPool;
import pmdd.components.properties.NodeProperty;
import pmdd.components.properties.PropertyGCC;

public class Memory {

    private Memory(){}

    private static final MemoryPool<NodeProperty> gccs = new MemoryPool<>();
    public static PropertyGCC PropertyGCC(int size){
        PropertyGCC object = (PropertyGCC) gccs.get();
        if(object == null){
            object = new PropertyGCC(gccs, size);
            gccs.add(object);
        }
        object.prepare();
        return object;
    }
    public static PropertyGCC PropertyGCC(int[] max){
        PropertyGCC object = (PropertyGCC) gccs.get();
        if(object == null){
            object = new PropertyGCC(gccs, max);
            gccs.add(object);
        }
        object.prepare();
        return object;
    }


}
