package structures.generics;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.ArrayList;
import java.util.Iterator;

public class ListOf<E> implements MemoryObject, Iterable<E> {

    // MemoryObject variables
    private final MemoryPool<ListOf<E>> pool;
    private int ID = -1;
    //

    private final ArrayList<E> list = new ArrayList<>();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    public ListOf(MemoryPool<ListOf<E>> pool){
        this.pool = pool;
    }


    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    public E get(int index){
        return this.list.get(index);
    }

    public void add(E element){
        this.list.add(element);
    }

    public void set(int index, E element){
        this.list.set(index, element);
    }

    public void add(ListOf<E> list){
        this.list.addAll(list.list);
    }

    public void add(Iterable<E> iterable) {
        for(E e : iterable) list.add(e);
    }

    public E remove(int index){
        return this.list.remove(index);
    }

    public boolean remove(E element){
        return this.list.remove(element);
    }

    public boolean contains(E element){
        return this.list.contains(element);
    }

    public void clear(){
        this.list.clear();
    }

    public int size(){
        return list.size();
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        clear();
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        list.clear();
        this.pool.free(this, ID);
    }


    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<E> interface

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }
}
