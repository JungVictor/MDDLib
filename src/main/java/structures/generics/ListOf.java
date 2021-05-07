package structures.generics;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>Class to symbolize a list of something.</b> <br>
 * Use this over the Java's lists because this one can be "free" from the memory and reused.
 * @param <E> The type of object you want to store in the list
 */
public class ListOf<E> implements MemoryObject, Iterable<E> {

    // MemoryObject variables
    private final MemoryPool<ListOf<E>> pool;
    private int ID = -1;
    //

    private final List<E> list = new ArrayList<>();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    public ListOf(MemoryPool<ListOf<E>> pool){
        this.pool = pool;
    }

    @Override
    public String toString(){
        return list.toString();
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    /**
     * Get the value of the element at the specified position.
     * @param index Position of the element
     * @return the value of the element at the specified position
     */
    public E get(int index){
        return this.list.get(index);
    }

    /**
     * Add the element at the end of the list
     * @param element Element to add
     */
    public void add(E element){
        this.list.add(element);
    }

    /**
     * Set the element in the given position to the given value.
     * @param index Position in the list
     * @param element Value of the element
     */
    public void set(int index, E element){
        this.list.set(index, element);
    }

    /**
     * Add all elements in the given list at the end of this list
     * @param list The list of elements to add
     */
    public void add(ListOf<E> list){
        this.list.addAll(list.list);
    }

    /**
     * Add all elements in the given Iterable data structure at the end of this list.
     * @param iterable The data structure containing the elements to add
     */
    public void add(Iterable<E> iterable) {
        for(E e : iterable) list.add(e);
    }

    /**
     * Remove the element at the specified index
     * @param index Index of the element to remove
     * @return The element removed (null if none)
     */
    public E remove(int index){
        return this.list.remove(index);
    }

    /**
     * Remove the given element from the list.
     * @param element Element to remove
     * @return true if the element was in the list, false otherwise.
     */
    public boolean remove(E element){
        return this.list.remove(element);
    }

    /**
     * Check if the element is in the list
     * @param element Element to check
     * @return true if the element is in the list, false otherwise
     */
    public boolean contains(E element){
        return this.list.contains(element);
    }

    /**
     * Clear all elements in the list
     */
    public void clear(){
        this.list.clear();
    }

    /**
     * Get the number of elements in the list
     * @return the number of elements in the list
     */
    public int size(){
        return list.size();
    }

    /**
     * Get the internal list of elements
     * @return the internal list of elements
     */
    public List<E> getList(){
        return list;
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
