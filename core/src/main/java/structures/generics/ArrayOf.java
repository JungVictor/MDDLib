package structures.generics;


import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Arrays;
import java.util.Iterator;

/**
 * <b>Class to symbolize an array of something.</b> <br>
 * Why not simply use the basic array in Java ? Because this one is made to be reused,
 * thus reducing memory consumption and creation of objects.
 * Apart from set/get functions, this is used like a regular array (iterator / .length... )
 * @param <E> The type of object you want to store in the array
 */
public class ArrayOf<E> implements Iterable<E>, MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ArrayOf<E>> pool;
    private int ID = -1;
    //

    // The proper array
    private E[] array;
    // Length of the array. The actual size of the array might be greater.
    public int length;
    // Iterator
    private final ArrayOfIterator iterator = new ArrayOfIterator();

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    @SuppressWarnings("unchecked")
    public ArrayOf(MemoryPool<ArrayOf<E>> pool, int i){
        this.pool = pool;
        this.array = (E[]) new Object[10];
        setLength(i);
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    // toString

    @Override
    public String toString(){
        return Arrays.toString(array);
    }


    //**************************************//
    //          ARRAY MANAGEMENT            //
    //**************************************//
    // set              || get
    // length           || clear
    // setLength        || copy

    /**
     * Set the element in the given position to the given value.
     * Similar to array[position] = value
     * @param position Position in the array
     * @param value Value of the element
     */
    public void set(int position, E value){
        array[position] = value;
    }

    /**
     * Get the value of the element at the specified position. Similar to array[position]
     * @param position Position of the element
     * @return the value of the element at the specified position
     */
    public E get(int position){
        return array[position];
    }

    /**
     * Get the length of the array. Similar to array.length
     * @return the length of the array
     */
    public int length(){
        return length;
    }

    /**
     * Clear the array.
     * That is to say, put all its elements to null and its size to 0.
     */
    public void clear(){
        Arrays.fill(array, null);
        length = 0;
    }

    /**
     * Set the length of the array. Similar to new E[length].
     * Create internally a new array if the current one isn't long enough.
     * @param length The length of the array
     */
    @SuppressWarnings("unchecked")
    public void setLength(int length){
        if(length > array.length) this.array = (E[]) new Object[length];
        this.length = length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    @SuppressWarnings("unchecked")
    public void copy(ArrayOf<E> array){
        if(array.length > this.array.length) this.array = (E[]) new Object[array.length];
        System.arraycopy(array.array, 0, this.array, 0, array.length);
        this.length = array.length;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {}

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free(){
        for(int i = 0; i < array.length; i++) array[i] = null;
        pool.free(this, ID);
    }


    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<E> interface

    @Override
    public Iterator<E> iterator() {
        iterator.i = 0;
        return iterator;
    }

    private class ArrayOfIterator implements Iterator<E> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < length;
        }

        @Override
        public E next() {
            return array[i++];
        }
    }
}
