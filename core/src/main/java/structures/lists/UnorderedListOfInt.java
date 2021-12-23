package structures.lists;

import memory.Allocable;
import memory.AllocatorOf;

import java.util.Arrays;
import java.util.Iterator;

public class UnorderedListOfInt implements Allocable, Iterable<Integer> {

    // Thread safe allocator
    private final static ThreadLocal<UnorderedListOfInt.Allocator> localStorage = ThreadLocal.withInitial(UnorderedListOfInt.Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private final UnorderedListOfIntIterator iterator = new UnorderedListOfIntIterator();
    private int[] list = new int[16];
    private int size;

    private static Allocator allocator(){
        return localStorage.get();
    }

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private UnorderedListOfInt(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create an UnorderedListOfInt with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return An UnorderedListOfInt with given initial capacity
     */
    public static UnorderedListOfInt create(int capacity){
        UnorderedListOfInt object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new int[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfInt with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfInt with initial capacity of 16.
     */
    public static UnorderedListOfInt create(){
        return create(16);
    }

    @Override
    public String toString(){
        return Arrays.toString(list);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    public int[] list(){
        return list;
    }

    /**
     * Get the value of the element at the specified position.
     * @param index Position of the element
     * @return the value of the element at the specified position
     */
    public int get(int index){
        return list[index];
    }

    /**
     * Add the element at the end of the list
     * @param element Element to add
     */
    public void add(int element){
        if(size >= list.length) {
            int[] newlist = new int[size + size / 3];
            System.arraycopy(list, 0, newlist, 0, list.length);
            list = newlist;
        }
        list[size++] = element;
    }

    /**
     * Set the element in the given position to the given value.
     * @param index Position in the list
     * @param element Value of the element
     */
    public void set(int index, int element){
        list[index] = element;
    }

    /**
     * Add all elements in the given Iterable data structure at the end of this list.
     * @param iterable The data structure containing the elements to add
     */
    public void add(Iterable<Integer> iterable) {
        for(int e : iterable) add(e);
    }

    /**
     * Remove the element at the specified index
     * @param index Index of the element to remove
     * @return The element removed (null if none)
     */
    public int remove(int index){
        int element = list[index];
        list[index] = list[size-1];
        size--;
        return element;
    }

    public int indexOf(int element){
        for(int i = 0; i < size; i++) if(list[i] == element) return i;
        return -1;
    }

    /**
     * Remove the given element from the list.
     * @param element Element to remove
     * @return true if the element was in the list, false otherwise.
     */
    public boolean removeElement(int element){
        int index = indexOf(element);
        if(index < 0) return false;
        list[index] = list[size-1];
        size--;
        return true;
    }

    /**
     * Check if the element is in the list
     * @param element Element to check
     * @return true if the element is in the list, false otherwise
     */
    public boolean contains(int element){
        return indexOf(element) >= 0;
    }

    /**
     * Clear all elements in the list
     */
    public void clear(){
        for(int i = 0; i < size; i++) list[i] = 0;
        size = 0;
    }

    /**
     * Get the number of elements in the list
     * @return the number of elements in the list
     */
    public int size(){
        return size;
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
        clear();
        allocator().free(this);
    }

    static final class Allocator extends AllocatorOf<UnorderedListOfInt> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected UnorderedListOfInt[] arrayCreation(int capacity) {
            return new UnorderedListOfInt[capacity];
        }

        @Override
        protected UnorderedListOfInt createObject(int index) {
            return new UnorderedListOfInt(index);
        }
    }




    @Override
    public Iterator<Integer> iterator() {
        iterator.i = 0;
        return iterator;
    }

    private class UnorderedListOfIntIterator implements Iterator<Integer> {

        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < size;
        }

        @Override
        public Integer next() {
            return list[i++];
        }
    }

}
