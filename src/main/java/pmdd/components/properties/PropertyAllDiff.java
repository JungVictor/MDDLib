package pmdd.components.properties;


import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.generics.ListOf;
import structures.generics.SetOf;

import java.util.Collections;

/**
 * ALL DIFFERENT CONSTRAINT
 * We simply store into a set the already taken values.
 * If we try to add an already existing value, the constraint is violated.
 * We only take into account some values if specified, otherwise we take everything into account.
 * Result is an int : 0 = violated  -  1 = satisfied
 */
public class PropertyAllDiff extends NodeProperty {

    private SetOf<Integer> values;
    private SetOf<Integer> alldiff;
    private int result = 1;

    public PropertyAllDiff(MemoryPool<NodeProperty> pool){
        super(pool);
        super.setName(ALLDIFF);
    }

    public void addValue(int value){
        values.add(value);
    }

    public void addValues(SetOf<Integer> values){
        this.values.add(values);
    }

    @Override
    public int hash(){
        int hash = 0;
        for(int v : values) {
            if(alldiff.contains(v)) hash += 1;
            hash += hash;
        }
        return hash;
    }

    @Override
    public int hash(int value){
        if(alldiff.contains(value)) return hash();
        int hash = 0;
        for(int v : values) {
            if(alldiff.contains(v)) hash += 1;
            else if(v == value) hash += 1;
            hash += hash;
        }
        return hash;
    }

    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getSingle

    @Override
    public int getSingle(){
        return result;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty

    @Override
    public NodeProperty createProperty(int value) {
        PropertyAllDiff allDiff = PMemory.PropertyAllDiff(values);
        for(int v : this.alldiff) allDiff.alldiff.add(v);
        allDiff.result = allDiff.alldiff.contains(value) ? 0 : 1;

        if(values.contains(value)) allDiff.alldiff.add(value);
        return allDiff;
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isDegenerate(int v) {
        return alldiff.contains(v);
    }

    @Override
    public String toString(){
        return alldiff.toString();
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface
    @Override
    public void prepare() {
        this.alldiff = Memory.SetOfInteger();
        this.values = Memory.SetOfInteger();
    }

    @Override
    public void free(){
        values.clear();
        alldiff.clear();
        super.free();
    }
}
