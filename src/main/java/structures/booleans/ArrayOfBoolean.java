package structures.booleans;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Class to symbolize an array of int. Similar to the ArrayOf class, this one is
 * specifically for the primitive type int. Works similarly.
 */
public class ArrayOfBoolean implements Iterable<Boolean>, MemoryObject {

    // MemoryObject variables
    private MemoryPool<ArrayOfBoolean> pool;
    private int ID = -1;
    //

    private boolean[] array;
    public int length;
    private final ArrayOfBooleanIterator iterator = new ArrayOfBooleanIterator();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public ArrayOfBoolean(MemoryPool<ArrayOfBoolean> pool, int capacity){
        this.pool = pool;
        this.array = new boolean[capacity];
        this.setLength(capacity);
    }


    //**************************************//
    //          SPECIAL FUNCTIONS           //
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
    // contains

    public void set(int position, boolean value){
        array[position] = value;
    }
    public boolean get(int position){
        return array[position];
    }
    public int length(){
        return length;
    }

    public void clear(){
        length = 0;
        prepare();
    }
    public void setLength(int length){
        if(length > array.length) this.array = new boolean[length];
        this.length = length;
    }

    public void copy(boolean[] array){
        if(array.length > this.array.length) this.array = array;
        else System.arraycopy(array, 0, this.array, 0, array.length);
        this.length = array.length;
    }
    public void copy(ArrayOfBoolean array){
        copy(array.array);
    }

    public boolean contains(boolean value){
        for(boolean v : this) if(v == value) return true;
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
        Arrays.fill(array, false);
    }

    @Override
    public void free(){
        pool.free(this, ID);
    }

    @Override
    public boolean isAtomic() {
        return false;
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
}
