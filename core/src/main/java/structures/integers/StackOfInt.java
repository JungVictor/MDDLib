package structures.integers;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * <b>Stack structure to store primitive type int.</b> <br>
 * Used to store free indices for the memory management system.
 */
public class StackOfInt {

    // The proper array
    private AtomicIntegerArray array;
    // The index of the last element
    private final AtomicInteger pointer = new AtomicInteger(-1);


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public StackOfInt(int initial_capacity){
        this.array = new AtomicIntegerArray(initial_capacity);
    }

    @Override
    public String toString(){
        return array.toString();
    }

    //**************************************//
    //           COMMON FUNCTIONS           //
    //**************************************//
    // push             || pop
    // isEmpty          || -expand

    /**
     * Push an element at the top of the stack
     * @param element int
     */
    public void push(int element){
        if(pointer.get()+1 == array.length()) expand(array.length() + array.length() / 3);
        array.set(pointer.incrementAndGet(), element);
    }

    /**
     * Pop the element that is on the top of the stack
     * @return last element pushed into the stack
     */
    public int pop(){
        return array.get(pointer.getAndDecrement());
    }

    /**
     * Test if the stack if empty
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty(){
        return pointer.get() == -1;
    }

    /**
     * Get the size of the stack
     * @return The number of elements in the stack
     */
    public int size(){
        return pointer.get()+1;
    }

    /**
     * Increase the capacity of the stack
     * @param new_capacity The new capacity of the stack
     */
    private void expand(int new_capacity){
        AtomicIntegerArray nArray = new AtomicIntegerArray(new_capacity);
        for(int i = 0; i < array.length(); i++) nArray.set(i, array.get(i));
        array = nArray;
    }

}
