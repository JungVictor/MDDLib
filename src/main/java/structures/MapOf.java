package structures;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.HashMap;

public class MapOf<K, V> implements MemoryObject {

    private final MemoryPool<MapOf<K, V>> pool;
    private int ID = -1;

    private final HashMap<K, V> map = new HashMap<>();

    public MapOf(MemoryPool<MapOf<K, V>> pool){
        this.pool = pool;
    }

    public void put(K key, V value){
        map.put(key, value);
    }

    public V get(K key){
        return map.get(key);
    }

    public V remove(K key){
        return map.remove(key);
    }

    @Override
    public void prepare() {

    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        this.pool.free(ID);
    }

    @Override
    public boolean isComposed() {
        return false;
    }
}
