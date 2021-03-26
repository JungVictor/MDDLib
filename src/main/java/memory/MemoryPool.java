package memory;

import structures.integers.StackOfInt;

public class MemoryPool<E extends MemoryObject> {

    // DEBUG
    public static int objects = 0;

    // The array
    private E[] pool;
    // The Stack of free indices (= the index of a free object)
    private final StackOfInt freeIndices;
    // The last value in the array and the size of the array
    private int last, size;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    @SuppressWarnings("unchecked")
    public MemoryPool(int capacity){
        pool = (E[]) new MemoryObject[capacity];
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
    public E get(){
        last++;
        if(freeIndices.isEmpty()) return null;
        int index = freeIndices.pop();
        E object = pool[index];
        pool[index] = null;
        return object;
    }

    /**
     * Get the last object of the pool
     * @return The last object of the pool. Might be null.
     */
    public E last(){
        return pool[last];
    }

    /**
     * Add an element to the pool
     * @param element element to push into the pool
     */
    public void add(E element){
        objects++;
        if(size == pool.length) expand();
        pool[size] = element;
        element.setID(size++);
    }

    /**
     * Free the object at the given position.
     * @param position The position of the object to free.
     */
    public void free(E object, int position){
        freeIndices.push(position);
        pool[position] = object;
        last--;
    }

    /**
     * Expand the capacity of the pool
     */
    private void expand(){
        E[] arr = (E[]) new MemoryObject[pool.length + pool.length];
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