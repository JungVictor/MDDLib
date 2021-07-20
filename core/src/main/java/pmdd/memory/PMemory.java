package pmdd.memory;

import memory.MemoryPool;
import pmdd.components.properties.*;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.TupleOfInt;

public class PMemory {

    private PMemory(){}

    private static final MemoryPool<NodeProperty> gccs = new MemoryPool<>();
    public static PropertyGCC PropertyGCC(MapOf<Integer, TupleOfInt> max){
        PropertyGCC object = (PropertyGCC) gccs.get();
        if(object == null){
            object = new PropertyGCC(gccs);
            gccs.add(object);
        }
        object.prepare();
        object.setMaxValues(max);
        return object;
    }

    private static final MemoryPool<NodeProperty> alldiffs = new MemoryPool<>();
    public static PropertyAllDiff PropertyAllDiff(SetOf<Integer> values){
        PropertyAllDiff object = (PropertyAllDiff) alldiffs.get();
        if(object == null){
            object = new PropertyAllDiff(alldiffs);
            alldiffs.add(object);
        }
        object.prepare();
        object.addValues(values);
        return object;
    }


    private static final MemoryPool<NodeProperty> sequences = new MemoryPool<>();
    public static PropertySequence PropertySequence(SetOf<Integer> label, int size, boolean base){
        if(base) return PropertySequence(label, size);
        PropertySequence object = (PropertySequence) sequences.get();
        if(object == null){
            object = new PropertySequence(sequences, label, size);
            sequences.add(object);
        } else object.init(size);
        object.prepare();
        return object;
    }
    public static PropertySequence PropertySequence(SetOf<Integer> label, int size){
        PropertySequence object = (PropertySequence) sequences.get();
        if(object == null){
            object = new PropertySequence(sequences, label, size, true);
            sequences.add(object);
        } else object.init(1);
        object.prepare();
        return object;
    }

    private static final MemoryPool<NodeProperty> sums = new MemoryPool<>();
    public static PropertySum PropertySum(int v1, int v2){
        PropertySum object = (PropertySum) sums.get();
        if(object == null){
            object = new PropertySum(sums);
            sums.add(object);
        }
        object.prepare();
        object.setValue(v1, v2);
        return object;
    }

    public static PropertySum PropertySum(int v1, int v2, MapOf<Integer, Integer> bindings){
        PropertySum object = (PropertySum) sums.get();
        if(object == null){
            object = new PropertySum(sums);
            sums.add(object);
        }
        object.prepare();
        object.setValue(v1, v2);
        object.setBindings(bindings);
        return object;
    }

    private static final MemoryPool<NodeProperty> amongs = new MemoryPool<>();
    public static PropertyAmong PropertyAmong(int q, int min, int max, SetOf<Integer> V){
        PropertyAmong object = (PropertyAmong) amongs.get();
        if(object == null){
            object = new PropertyAmong(amongs);
            amongs.add(object);
        }
        object.setParameters(q, min, max, V);
        object.prepare();
        return object;
    }

}
