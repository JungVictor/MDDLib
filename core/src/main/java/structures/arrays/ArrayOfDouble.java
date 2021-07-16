package structures.arrays;

import memory.Allocable;
import memory.AllocatorOf;

import java.util.Iterator;

/**
 * <b>Class to symbolize an array of int.</b> <br>
 * Similar to the SuccessionOf class, this one is specifically for the primitive type int. Works similarly.
 */
public class ArrayOfDouble implements Iterable<Double>, Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private double[] array;
    public int length;
    private final SuccessionOfDoubleIterator iterator = new SuccessionOfDoubleIterator();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public ArrayOfDouble(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(int capacity){
        this.array = new double[capacity];
        this.length = capacity;
    }

    /**
     * Create a SuccessionOfDouble with specified capacity.
     * The object is managed by the allocator.
     * @param capacity Capacity of the array
     * @return A SuccessionOfDouble with given capacity
     */
    public static ArrayOfDouble create(int capacity){
        ArrayOfDouble object = allocator().allocate();
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
    private static Allocator allocator(){
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
    public void set(int position, double value){
        array[position] = value;
    }

    /**
     * Get the value of the element at the specified position. Similar to array[position]
     * @param position Position of the element
     * @return the value of the element at the specified position
     */
    public double get(int position){
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
        if(length > array.length) this.array = new double[length];
        this.length = length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    public void copy(double[] array){
        if(array.length > this.array.length) this.array = new double[array.length];
        System.arraycopy(array, 0, this.array, 0, array.length);
        this.length = array.length;
    }

    /**
     * Copy all the values in the given array into this array.
     * Create internally a new array if the current one isn't long enough.
     * @param array The array to copy
     */
    public void copy(ArrayOfDouble array){
        if(array.length > this.array.length) this.array = new double[array.length];
        System.arraycopy(array.array, 0, this.array, 0, array.length);
        this.length = array.length;
    }

    /**
     * Check if the value is contained in the array
     * @param value The value to check
     * @return true if the value is contained in the array, false otherwise
     */
    public boolean contains(double value){
        for(double v : this) if(v == value) return true;
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
        for(int i = 0; i < length; i++) array[i] = 0;
        allocator().free(this);
    }


    //**************************************//
    //               ITERATOR               //
    //**************************************//
    // Implementation of Iterable<Integer> interface

    @Override
    public Iterator<Double> iterator() {
        iterator.i = 0;
        return iterator;
    }

    private class SuccessionOfDoubleIterator implements Iterator<Double> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < length;
        }

        @Override
        public Double next() {
            return array[i++];
        }
    }


    /**
     * <b>The allocator that is in charge of the SuccessionOfInt type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ArrayOfDouble> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ArrayOfDouble[] arrayCreation(int capacity) {
            return new ArrayOfDouble[capacity];
        }

        @Override
        protected ArrayOfDouble createObject(int index) {
            return new ArrayOfDouble(index);
        }
    }
}
