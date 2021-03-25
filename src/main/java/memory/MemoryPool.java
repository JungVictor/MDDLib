package memory;

import structures.integers.StackOfInt;

public class MemoryPool<E> {

    // DEBUG
    public static int objects = 0;

    // The array
    private MemoryObject[] pool;
    // The Stack of free indices (= the index of a free object)
    private final StackOfInt freeIndices;
    // The last value in the array and the size of the array
    private int last, size;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public MemoryPool(int capacity){
        pool = new MemoryObject[capacity];
        freeIndices = new StackOfInt(capacity);
        last = -1;
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
    @SuppressWarnings("unchecked")
    public E get(){
        last++;
        if(freeIndices.isEmpty()) return null;
        return (E) pool[freeIndices.pop()];
    }

    /**
     * Get the last object of the pool
     * @return The last object of the pool. Might be null.
     */
    @SuppressWarnings("unchecked")
    public E last(){
        return (E) pool[last];
    }

    /**
     * Add an element to the pool
     * @param element element to push into the pool
     */
    public void add(MemoryObject element){
        objects++;
        if(size == pool.length) expand();
        pool[size] = element;
        element.setID(size++);
    }

    /**
     * Free the last element of the pool.
     */
    public void free(){
        free(last);
    }

    /**
     * Free the last n element of the pool.
     * @param n Number of elements to free
     */
    public void freeLast(int n){
        for(int i = 0; i < n; i++) freeIndices.push(last-i);
        last -= n;
    }

    /**
     * Free the object at the given position.
     * @param position The position of the object to free.
     */
    public void free(int position){
        freeIndices.push(position);
        last--;
    }

    /**
     * Expand the capacity of the pool
     */
    private void expand(){
        MemoryObject[] arr = new MemoryObject[pool.length + pool.length];
        System.arraycopy(pool, 0, arr, 0, pool.length);
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