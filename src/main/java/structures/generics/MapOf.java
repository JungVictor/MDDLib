package structures.generics;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <b>Class to symbolize a map of something.</b> <br>
 * Use this over the Java's map because this one can be "free" from the memory and reused. <br>
 * <b>ATTENTION : </b> if you want to free the elements in the map, you have to do it manually !
 * @param <K> The type of element used as keys
 * @param <V> The type of element used as values
 */
public class MapOf<K, V> implements MemoryObject, Iterable<K> {

    // MemoryObject variables
    private final MemoryPool<MapOf<K, V>> pool;
    private int ID = -1;
    //

    private final HashMap<K, V> map = new HashMap<>();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public MapOf(MemoryPool<MapOf<K, V>> pool){
        this.pool = pool;
    }

    @Override
    public String toString(){
        return map.toString();
    }

    //**************************************//
    //            MAP MANAGEMENT            //
    //**************************************//

    /**
     * Add an element to the map
     * @param key Value of the key
     * @param value The value of the element
     */
    public void put(K key, V value){
        map.put(key, value);
    }

    /**
     * Get the value corresponding to the given key
     * @param key The value of the key
     * @return  the value corresponding to the given key (null if none)
     */
    public V get(K key){
        return map.get(key);
    }

    /**
     * Remove the given key from the map
     * @param key The key to remove
     * @return the element corresponding to the given key (null if none)
     */
    public V remove(K key){
        return map.remove(key);
    }

    /**
     * Clear the map
     */
    public void clear(){
        map.clear();
    }

    /**
     * Get the set containing all the keys' values.
     * @return the set containing all the keys' values
     */
    public Set<K> keySet(){
        return map.keySet();
    }

    /**
     * Get the set containing all the values
     * @return the set containing all the values
     */
    public Collection<V> values(){
        return map.values();
    }

    /**
     * Check if the map contains the given key
     * @param key The key to check
     * @return true if the map contains the given key, false otherwise
     */
    public boolean contains(K key){
        return map.containsKey(key);
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        map.clear();
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        map.clear();
        this.pool.free(this, ID);
    }

    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<K> interface

    @Override
    public Iterator<K> iterator() {
        return map.keySet().iterator();
    }
}
