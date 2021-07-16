package memory;

import structures.integers.StackOfInt;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * <b>The MemoryPool is the structure that contains all objects in the pool of memory.</b>
 * It is implemented using an array to stock the objects, and a stack to stock the indices of free objects,
 * i.e their position in the array.
 * @param <E> The type of the object the MemoryPool will hold
 */
public class MemoryPool<E extends MemoryObject> {

    // DEBUG
    public static int objects = 0;

    // The array
    private volatile AtomicReferenceArray<E> pool;
    // The Stack of free indices (= the index of a free object)
    private final StackOfInt freeIndices;
    // The last value in the array and the size of the array
    private int size = 0;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public MemoryPool(int capacity){
        pool = new AtomicReferenceArray<>(capacity);
        freeIndices = new StackOfInt(capacity);
    }

    public MemoryPool(){
        this(10);
    }


    //**************************************//
    //   ACCESSORS                          //
    //**************************************//
    // get              || last
    // add              || free
    // freeLast         || -expand
    // size

    /**
     * Get a free object from the pool.
     * @return A free object from the pool if there is one available, null otherwise.
     */
    public E get(){
        if(freeIndices.isEmpty()) return null;
        int index = freeIndices.pop();
        E object = pool.get(index);
        pool.set(index, null);
        return object;
    }

    /**
     * Add an element to the pool
     * @param element element to push into the pool
     */
    public synchronized void add(E element){
        objects++;
        if(size == pool.length()) expand();
        element.setID(size++);
    }

    /**
     * Free the object at the given position.
     * @param position The position of the object to free.
     */
    public void free(E object, int position){
        if(pool.get(position) == null) {
            freeIndices.push(position);
            pool.set(position, object);
        }
    }

    /**
     * Expand the capacity of the pool
     */
    private void expand(){
        AtomicReferenceArray<E> arr = new AtomicReferenceArray<>(pool.length() + pool.length());
        for(int i = 0; i < pool.length(); i++) arr.set(i, pool.get(i));
        pool = arr;
    }

    /**
     * Get the size of the pool
     * @return The number of elements inside the pool
     */
    public int size(){
        return size;
    }

}