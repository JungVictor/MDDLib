package structures.arrays;

import memory.Allocable;

import java.util.Arrays;
import java.util.Iterator;

public abstract class ArrayOf<E> implements Iterable<E>, Allocable {

    // Index in Memory
    private final int allocatedIndex;
    // Length of the array. The actual size of the array might be greater.
    public int length;
    // Iterator
    private final SuccessionOfIterator iterator = new SuccessionOfIterator();

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public ArrayOf(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(int capacity){
        if(array() == null || size() < capacity) arrayAllocation(capacity);
        this.length = capacity;
    }

    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    // toString

    @Override
    public String toString(){
        return Arrays.toString(array());
    }

    protected abstract E[] array();

    @Override
    public int allocatedIndex() {
        return allocatedIndex;
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
        array()[position] = value;
    }

    /**
     * Get the value of the element at the specified position. Similar to array[position]
     * @param position Position of the element
     * @return the value of the element at the specified position
     */
    public E get(int position){
        return array()[position];
    }

    /**
     * Get the length of the array. Similar to array.length
     * @return the length of the array
     */
    public int length(){
        return length;
    }

    protected abstract int size();

    /**
     * Clear the array.
     * That is to say, put all its elements to null and its size to 0.
     */
    public void clear(){
        Arrays.fill(array(), null);
        length = 0;
    }

    /**
     * Create an array of given capacity.
     * This is the array that will be returned by the function array()
     * @param capacity The capacity of the array
     */
    protected abstract void arrayAllocation(int capacity);

    /**
     * Set the length of the array. Similar to new E[length].
     * Create internally a new array if the current one isn't long enough.
     * @param length The length of the array
     */
    public void setLength(int length){
        if(length > array().length) arrayAllocation(length);
        this.length = length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    public void copy(ArrayOf<E> array){
        if(array.length > this.array().length) arrayAllocation(array.length);
        System.arraycopy(array.array(), 0, this.array(), 0, array.length);
        this.length = array.length;
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

    private class SuccessionOfIterator implements Iterator<E> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < length;
        }

        @Override
        public E next() {
            return array()[i++];
        }
    }
}