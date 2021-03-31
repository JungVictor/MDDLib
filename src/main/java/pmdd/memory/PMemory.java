package pmdd.memory;

import mdd.MDD;
import mdd.components.Node;
import memory.MemoryPool;
import pmdd.PMDD;
import pmdd.components.PNode;
import pmdd.components.properties.NodeProperty;
import pmdd.components.properties.PropertyGCC;
import pmdd.components.properties.PropertySequence;
import pmdd.components.properties.PropertySum;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;

public class PMemory {

    private PMemory(){}

    private static final MemoryPool<MDD> pmdds = new MemoryPool<>();
    public static PMDD PMDD(){
        PMDD object = (PMDD) pmdds.get();
        if(object == null){
            object = new PMDD(pmdds);
            pmdds.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<Node> pnodes = new MemoryPool<>();
    public static PNode PNode(){
        PNode object = (PNode) pnodes.get();
        if(object == null){
            object = new PNode(pnodes);
            pnodes.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<NodeProperty> gccs = new MemoryPool<>();
    public static PropertyGCC PropertyGCC(ArrayOfInt max){
        PropertyGCC object = (PropertyGCC) gccs.get();
        if(object == null){
            object = new PropertyGCC(gccs, max);
            gccs.add(object);
        }
        object.prepare();
        return object;
    }


    private static final MemoryPool<NodeProperty> sequences = new MemoryPool<>();
    public static PropertySequence PropertySequence(SetOf<Integer> label, int size, boolean base){
        if(base) return PropertySequence(label, size);
        PropertySequence object = (PropertySequence) sequences.get();
        if(object == null){
            object = new PropertySequence(sequences, label, size);
            sequences.add(object);
        }
        object.prepare();
        return object;
    }
    public static PropertySequence PropertySequence(SetOf<Integer> label, int size){
        PropertySequence object = (PropertySequence) sequences.get();
        if(object == null){
            object = new PropertySequence(sequences, label, size, true);
            sequences.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<NodeProperty> sums = new MemoryPool<>();
    public static PropertySum PropertySum(int v1, int v2, int max){
        PropertySum object = (PropertySum) sums.get();
        if(object == null){
            object = new PropertySum(sums, v1, v2, max);
            sums.add(object);
        }
        object.prepare();
        return object;
    }
    public static PropertySum PropertySum(int v1, int v2){
        return PropertySum(v1, v2, Integer.MAX_VALUE);
    }

}
