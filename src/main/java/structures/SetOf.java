package structures;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;

import java.util.HashSet;
import java.util.Iterator;

public class SetOf<E> implements MemoryObject, Iterable<E> {

    private final MemoryPool pool;
    private final HashSet<E> set = new HashSet<>();
    private int ID;

    public SetOf(MemoryPool pool){
        this.pool = pool;
        this.pool.add(this);
    }

    public void add(E object){
        set.add(object);
    }

    public boolean remove(E object){
        return set.remove(object);
    }

    public void clear(){
        set.clear();
    }

    public int size(){
        return set.size();
    }

    public boolean contains(E object){
        return set.contains(object);
    }

    @Override
    public void prepare() {
        set.clear();
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        pool.free(ID);
    }

    @Override
    public boolean isComposed() {
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return set.iterator();
    }
}
