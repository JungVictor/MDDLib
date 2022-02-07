package structures.arrays;

import memory.Allocable;
import memory.AllocatorOf;

import java.util.Iterator;

/**
 * <b>Class to symbolize an array of int.</b> <br>
 * Similar to the ArrayOf class, this one is specifically for the primitive type int. Works similarly.
 */
public class ArrayOfBoolean implements Iterable<Boolean>, Allocable {

    // Thread safe allocator
    private final static ThreadLocal<ArrayOfBoolean.Allocator> localStorage = ThreadLocal.withInitial(ArrayOfBoolean.Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private boolean[] array;
    public int length;
    private final ArrayOfBooleanIterator iterator = new ArrayOfBooleanIterator();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public ArrayOfBoolean(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Initialise the object.
     * @param capacity The capacity of the array
     * @return True if the array must be cleaned, false otherwise
     */
    private boolean init(int capacity){
        this.length = capacity;
        if(array != null && array.length >= capacity) return true;
        this.array = new boolean[capacity];
        return false;
    }

    /**
     * Create an ArrayOfBoolean with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return An ArrayOfBoolean with given capacity
     */
    public static ArrayOfBoolean create(int capacity){
        ArrayOfBoolean object = allocator().allocate();
        if(object.init(capacity)) object.clean();
        return object;
    }

    /**
     * Create a ArrayOfBoolean with specified capacity.
     * The object is managed by the allocator.<br>
     * The array is never cleaned.
     * @param capacity Capacity of the array
     * @return A ArrayOfBoolean with given capacity
     */
    public static ArrayOfBoolean fastCreate(int capacity){
        ArrayOfBoolean object = allocator().allocate();
        object.init(capacity);
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
    private static ArrayOfBoolean.Allocator allocator(){
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
    public void set(int position, boolean value){
        array[position] = value;
    }

    /**
     * Get the value of the element at the specified position. Similar to array[position]
     * @param position Position of the element
     * @return the value of the element at the specified position
     */
    public boolean get(int position){
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
     * Put all elements of the array to false
     */
    public void clean(){
        for(int i = 0; i < length; i++) array[i] = false;
    }

    /**
     * Put all n first elements of the array to false
     * @param n Number of elements to put to false
     */
    public void clean(int n){
        for(int i = 0; i < n; i++) array[i] = false;
    }

    /**
     * Set the length of the array. Similar to new E[length].
     * Create internally a new array if the current one isn't long enough.
     * @param length The length of the array
     */
    public void setLength(int length){
        if(length > array.length) this.array = new boolean[length];
        this.length = length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    public void copy(boolean[] array){
        if(array.length > this.array.length) this.array = new boolean[array.length];
        System.arraycopy(array, 0, this.array, 0, array.length);
        this.length = array.length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    public void copy(ArrayOfBoolean array){
        if(array.length > this.array.length) this.array = new boolean[array.length];
        System.arraycopy(array.array, 0, this.array, 0, array.length);
        this.length = array.length;
    }

    /**
     * Check if the value is contained in the array
     * @param value The value to check
     * @return true if the value is contained in the array, false otherwise
     */
    public boolean contains(boolean value){
        for(boolean v : this) if(v == value) return true;
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
    public Iterator<Boolean> iterator() {
        iterator.i = 0;
        return iterator;
    }

    private class ArrayOfBooleanIterator implements Iterator<Boolean> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < length;
        }

        @Override
        public Boolean next() {
            return array[i++];
        }
    }


    /**
     * <b>The allocator that is in charge of the ArrayOfBoolean type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ArrayOfBoolean> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ArrayOfBoolean[] arrayCreation(int capacity) {
            return new ArrayOfBoolean[capacity];
        }

        @Override
        protected ArrayOfBoolean createObject(int index) {
            return new ArrayOfBoolean(index);
        }
    }
}
