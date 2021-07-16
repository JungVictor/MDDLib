package structures.integers;

import java.util.Arrays;

/**
 * <b>Stack structure to store primitive type int.</b> <br>
 * Used to store free indices for the memory management system.
 */
public class StackOfInt {

    // The proper array
    private int[] array;
    // The index of the last element
    private int pointer = -1;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public StackOfInt(int initial_capacity){
        this.array = new int[initial_capacity];
    }

    @Override
    public String toString(){
        return Arrays.toString(array);
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
        if(pointer+1 == array.length) expand(array.length + array.length / 3);
        array[++pointer] = element;
    }

    /**
     * Pop the element that is on the top of the stack
     * @return last element pushed into the stack
     */
    public int pop(){
        return array[pointer--];
    }

    /**
     * Test if the stack if empty
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty(){
        return pointer == -1;
    }

    /**
     * Get the size of the stack
     * @return The number of elements in the stack
     */
    public int size(){
        return pointer+1;
    }

    /**
     * Increase the capacity of the stack
     * @param new_capacity The new capacity of the stack
     */
    private void expand(int new_capacity){
        int[] nArray = new int[new_capacity];
        System.arraycopy(array, 0, nArray, 0, array.length);
        array = nArray;
    }

}
