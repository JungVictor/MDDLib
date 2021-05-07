package structures.generics;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.HashSet;
import java.util.Iterator;

/**
 * <b>Class to symbolize a set of something.</b> <br>
 * Use this over the Java's sets because this one can be "free" from the memory and reused.
 * @param <E> The type of object you want to store in the set
 */
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

    /**
     * Add an element to the set
     * @param object The element to add
     */
    public void add(E object){
        set.add(object);
    }

    /**
     * Add all elements in the given set to this set
     * @param set The set of elements to add
     */
    public void add(SetOf<E> set){
        this.set.addAll(set.set);
    }

    /**
     * Add all elements in the given iterable data structure
     * @param iterable The iterable data structure containing the elements
     */
    public void add(Iterable<E> iterable){
        for (E e : iterable) this.set.add(e);
    }

    /**
     * Perform a set intersection between this set and the given set.
     * That is to say, keep only the elements that are present in both sets.
     * @param set The set to intersect with
     */
    public void intersect(SetOf<E> set){
        this.set.retainAll(set.set);
    }

    /**
     * Remove the element from the set
     * @param object The element to remove
     * @return true if the element was in the set, false otherwise
     */
    public boolean remove(E object){
        return set.remove(object);
    }

    /**
     * Remove all elements from the set
     */
    public void clear(){
        set.clear();
    }

    /**
     * Get the number of elements in the set.
     * @return the number of elements in the set
     */
    public int size(){
        return set.size();
    }

    /**
     * Check if the set contains the given element
     * @param object The element to check
     * @return true if the set contains the given element, false otherwise
     */
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
