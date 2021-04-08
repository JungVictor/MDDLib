package structures.generics;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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

    public void put(K key, V value){
        map.put(key, value);
    }

    public V get(K key){
        return map.get(key);
    }

    public V remove(K key){
        return map.remove(key);
    }

    public void clear(){
        map.clear();
    }

    public Set<K> keySet(){
        return map.keySet();
    }

    public Collection<V> values(){
        return map.values();
    }

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

    @Override
    public boolean isAtomic() {
        return false;
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
