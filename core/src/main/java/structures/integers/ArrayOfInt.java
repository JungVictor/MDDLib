package structures.integers;


import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Iterator;

/**
 * <b>Class to symbolize an array of int.</b> <br>
 * Similar to the ArrayOf class, this one is specifically for the primitive type int. Works similarly.
 */
public class ArrayOfInt implements Iterable<Integer>, MemoryObject {

    // MemoryObject variables
    private final MemoryPool<ArrayOfInt> pool;
    private int ID = -1;
    //

    private int[] array;
    public int length;
    private final ArrayOfIntIterator iterator = new ArrayOfIntIterator();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public ArrayOfInt(MemoryPool<ArrayOfInt> pool, int capacity){
        this.pool = pool;
        this.array = new int[capacity];
        this.length = capacity;
    }


    //**************************************//
    //          SPECIAL FUNCTIONS           //
    //**************************************//
    // toString

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if(length > 0) {
            for (int i = 0; i < length - 1; i++) {
                builder.append(array[i]);
                builder.append(", ");
            }
            builder.append(array[length - 1]);
        }
        builder.append("]");
        return builder.toString();
    }

    //**************************************//
    //          ARRAY MANAGEMENT            //
    //**************************************//
    // set              || get
    // length           || clear
    // setLength        || copy
    // contains

    /**
     * Set the element in the given position to the given value.
     * Similar to array[position] = value
     * @param position Position in the array
     * @param value Value of the element
     */
    public void set(int position, int value){
        array[position] = value;
    }

    /**
     * Get the value of the element at the specified position. Similar to array[position]
     * @param position Position of the element
     * @return the value of the element at the specified position
     */
    public int get(int position){
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
        length = 0;
    }

    /**
     * Set the length of the array. Similar to new E[length].
     * Create internally a new array if the current one isn't long enough.
     * @param length The length of the array
     */
    public void setLength(int length){
        if(length > array.length) this.array = new int[length];
        this.length = length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    public void copy(int[] array){
        if(array.length > this.array.length) this.array = new int[array.length];
        System.arraycopy(array, 0, this.array, 0, array.length);
        this.length = array.length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    public void copy(ArrayOfInt array){
        if(array.length > this.array.length) this.array = new int[array.length];
        System.arraycopy(array.array, 0, this.array, 0, array.length);
        this.length = array.length;
    }

    /**
     * Check if the value is contained in the array
     * @param value The value to check
     * @return true if the value is contained in the array, false otherwise
     */
    public boolean contains(int value){
        for(int v : this) if(v == value) return true;
        return false;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void prepare() {

    }

    @Override
    public void free(){
        pool.free(this, ID);
    }


    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Integer> interface

    @Override
    public Iterator<Integer> iterator() {
        iterator.i = 0;
        return iterator;
    }

    private class ArrayOfIntIterator implements Iterator<Integer> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < length;
        }

        @Override
        public Integer next() {
            return array[i++];
        }
    }
}
