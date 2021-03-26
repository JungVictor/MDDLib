package structures.integers;


import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Class to symbolize an array of int. Similar to the ArrayOf class, this one is
 * specifically for the primitive type int. Works similarly.
 */
public class ArrayOfInt implements Iterable<Integer>, MemoryObject {

    // MemoryObject variables
    private MemoryPool<ArrayOfInt> pool;
    private int ID = -1;
    //

    private int[] array;
    public int length;
    private final ArrayOfIntIterator iterator = new ArrayOfIntIterator();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public ArrayOfInt(MemoryPool<ArrayOfInt> pool){
        this.pool = pool;
        this.array = new int[10];
    }
    public ArrayOfInt(int size){
        this.array = new int[size];
        this.length = size;
    }
    public ArrayOfInt(int[] array){
        this.array = array;
        this.length = array.length;
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

    public void set(int position, int value){
        array[position] = value;
    }
    public int get(int position){
        return array[position];
    }
    public int length(){
        return length;
    }

    public void clear(){
        length = 0;
    }
    public void setLength(int length){
        if(length > array.length) this.array = new int[length];
        this.length = length;
    }

    public void copy(int[] array){
        if(array.length > this.array.length) this.array = array;
        else System.arraycopy(array, 0, this.array, 0, array.length);
        this.length = array.length;
    }
    public void copy(ArrayOfInt array){
        copy(array.array);
    }

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

    @Override
    public boolean isAtomic() {
        return false;
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
