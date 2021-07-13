package structures;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.SetOf;

import java.util.ArrayList;

public class Domains implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<Domains> pool;
    private int ID;
    //

    private final ArrayList<SetOf<Integer>> domains = new ArrayList<>();

    public Domains(MemoryPool<Domains> pool) {
        this.pool = pool;
    }

    public void add(int index){
        domains.add(index, Memory.SetOfInteger());
    }

    public void put(int index, int value){
        if(domains.size() <= index) add(index);
        domains.get(index).add(value);
    }

    public void remove(int index, int value){
        domains.get(index).remove(value);
    }

    public SetOf<Integer> get(int index){
        return domains.get(index);
    }

    public int size(int index){
        return domains.get(index).size();
    }

    public int size(){
        return domains.size();
    }

    public void clear(){
        for(SetOf<Integer> domain : domains) Memory.free(domain);
        domains.clear();
    }

    public void union(Domains D){
        for(int i = 0; i < domains.size(); i++) domains.get(i).add(D.get(i));
        if(D.domains.size() > domains.size()) {
            for(int i = domains.size(); i < D.domains.size(); i++) {
                add(i);
                domains.get(i).add(D.get(i));
            }
        }
    }

    public void intersect(Domains D){
        for(int i = 0; i < domains.size(); i++) domains.get(i).intersect(D.get(i));
    }

    public void fillWithValues(SetOf<Integer> V){
        for(SetOf<Integer> values : domains) V.add(values);
    }

    @Override
    public void prepare() {}

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        clear();
        this.pool.free(this, ID);
    }
}
