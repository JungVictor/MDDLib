package structures.generics;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.HashSet;
import java.util.Iterator;

public class SetOf<E> implements MemoryObject, Iterable<E> {

    // MemoryObject variables
    private final MemoryPool<SetOf<E>> pool;
    private int ID = -1;
    //

    private final HashSet<E> set = new HashSet<>();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public SetOf(MemoryPool<SetOf<E>> pool){
        this.pool = pool;
    }

    @Override
    public String toString(){
        return set.toString();
    }

    //**************************************//
    //            SET MANAGEMENT            //
    //**************************************//

    public void add(E object){
        set.add(object);
    }

    public void add(SetOf<E> set){
        this.set.addAll(set.set);
    }
    public void add(Iterable<E> iterable){
        for (E e : iterable) this.set.add(e);
    }

    public void intersect(SetOf<E> set){
        this.set.retainAll(set.set);
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



    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

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
        set.clear();
        pool.free(this, ID);
    }


    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<E> interface
    @Override
    public Iterator<E> iterator() {
        return set.iterator();
    }
}
