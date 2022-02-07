package structures.arrays;

import memory.Allocable;
import memory.AllocatorOf;

import java.util.Iterator;

/**
 * <b>Class to symbolize an array of int.</b> <br>
 * Similar to the ArrayOf class, this one is specifically for the primitive type int. Works similarly.
 */
public class ArrayOfInt implements Iterable<Integer>, Allocable {

    // Thread safe allocator
    private final static ThreadLocal<ArrayOfInt.Allocator> localStorage = ThreadLocal.withInitial(ArrayOfInt.Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private int[] array;
    public int length;
    private final ArrayOfIntIterator iterator = new ArrayOfIntIterator();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public ArrayOfInt(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    private boolean init(int capacity){
        this.length = capacity;
        if(array != null && array.length >= capacity) return true;
        this.array = new int[capacity];
        return false;
    }

    /**
     * Create an ArrayOfInt with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An ArrayOfInt with given capacity
     */
    public static ArrayOfInt create(int capacity){
        ArrayOfInt object = allocator().allocate();
        if(object.init(capacity)) object.clean();
        return object;
    }

    /**
     * Create an ArrayOfInt with specified capacity.
     * The object is managed by the allocator.<br>
     * The array is never cleaned.
     * @param capacity Capacity of the array
     * @return An ArrayOfInt with given capacity
     */
    public static ArrayOfInt fastCreate(int capacity){
        ArrayOfInt object = allocator().allocate();
        object.init(capacity);
        return object;
    }

    /**
     * Create a STATIC array, that can't be free from memory.
     * @param array The array
     * @return An ArrayOfInt corresponding to the given array
     */
    public static ArrayOfInt create(int... array){
        ArrayOfInt object = new ArrayOfInt(-1);
        object.array = array;
        object.length = array.length;
        return object;
    }

    //**************************************//
    //          SPECIAL FUNCTIONS           //
    //**************************************//
    // toString

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static ArrayOfInt.Allocator allocator(){
        return localStorage.get();
    }

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
     * Put all elements of the array to 0
     */
    public void clean(){
        for(int i = 0; i < length; i++) array[i] = 0;
    }

    /**
     * Put all n first elements of the array to 0
     * @param n Number of elements to put to 0
     */
    public void clean(int n){
        for(int i = 0; i < n; i++) array[i] = 0;
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


    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public void free() {
        allocator().free(this);
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


    /**
     * <b>The allocator that is in charge of the ArrayOfInt type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ArrayOfInt> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ArrayOfInt[] arrayCreation(int capacity) {
            return new ArrayOfInt[capacity];
        }

        @Override
        protected ArrayOfInt createObject(int index) {
            return new ArrayOfInt(index);
        }
    }
}
