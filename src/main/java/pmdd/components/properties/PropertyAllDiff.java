package pmdd.components.properties;

import memory.Memory;
import memory.MemoryPool;
import pmdd.memory.PMemory;
import structures.generics.MapOf;
import structures.generics.SetOf;

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


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public PropertyAllDiff(MemoryPool<NodeProperty> pool){
        super(pool);
        super.setName(ALLDIFF);
    }

    /**
     * Add all values contained in the given set to the property
     * @param values Set of values
     */
    public void addValues(SetOf<Integer> values){
        this.values.add(values);
    }

    @Override
    public String toString(){
        return alldiff.toString();
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getSingle

    @Override
    public MapOf getData(){
        return null;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty

    @Override
    public NodeProperty createProperty(int value) {
        PropertyAllDiff allDiff = PMemory.PropertyAllDiff(values);
        for(int v : this.alldiff) allDiff.alldiff.add(v);

        if(values.contains(value)) allDiff.alldiff.add(value);
        return allDiff;
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    @Override
    public boolean isValid(int v, int layer, int size) {
        return !alldiff.contains(v);
    }


    //**************************************//
    //           HASH FUNCTIONS             //
    //**************************************//
    // hash

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

    @Override
    public String hashstr(int value){
        StringBuilder builder = new StringBuilder();
        for(int v : values) {
            if(v == value || alldiff.contains(v)) builder.append("1");
            else builder.append("0");
        }
        return builder.toString();
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
        Memory.free(values);
        Memory.free(alldiff);
        super.free();
    }
}
