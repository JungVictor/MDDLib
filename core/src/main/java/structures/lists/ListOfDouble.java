package structures.lists;

import memory.Allocable;
import memory.AllocatorOf;

import java.util.Arrays;
import java.util.Iterator;

public class ListOfDouble implements Allocable, Iterable<Double> {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private final OrderedListOfDoubleIterator iterator = new OrderedListOfDoubleIterator();
    private double[] list = new double[16];
    private int size;
    private boolean fIsSorted = false;

    private static Allocator allocator(){
        return localStorage.get();
    }

    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private ListOfDouble(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create an UnorderedListOfInt with specified initial capacity.
     * The object is managed by the allocator.
     * @param capacity Initial capacity of the array
     * @return An UnorderedListOfInt with given initial capacity
     */
    public static ListOfDouble create(int capacity){
        ListOfDouble object = allocator().allocate();
        if(object.list == null || object.list.length < capacity) object.list = new double[capacity];
        return object;
    }

    /**
     * Create an UnorderedListOfInt with initial capacity of 16.
     * The object is managed by the allocator.
     * @return An UnorderedListOfInt with initial capacity of 16.
     */
    public static ListOfDouble create(){
        return create(16);
    }

    @Override
    public String toString(){
        return Arrays.toString(list);
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    public double[] list(){
        return list;
    }

    public void sort(){
        if(fIsSorted) return;
        Arrays.sort(list, 0, size);
        fIsSorted = true;
    }

    /**
     * Get the value of the element at the specified position.
     * @param index Position of the element
     * @return the value of the element at the specified position
     */
    public double get(int index){
        return list[index];
    }

    /**
     * Add the element at the end of the list
     * @param element Element to add
     */
    public void add(double element){
        if(size >= list.length) {
            double[] newlist = new double[size + size / 3];
            System.arraycopy(list, 0, newlist, 0, list.length);
            list = newlist;
        }
        fIsSorted = false;
        list[size++] = element;
    }

    /**
     * Set the element in the given position to the given value.
     * @param index Position in the list
     * @param element Value of the element
     */
    public void set(int index, double element){
        list[index] = element;
    }

    /**
     * Add all elements in the given Iterable data structure at the end of this list.
     * @param iterable The data structure containing the elements to add
     */
    public void add(Iterable<Double> iterable) {
        for(double e : iterable) add(e);
    }

    /**
     * Remove the element at the specified index
     * @param index Index of the element to remove
     * @return The element removed (null if none)
     */
    public double remove(int index){
        double element = list[index];
        for(int i = index; i < size-1; i++) list[i] = list[i+1];
        size--;
        return element;
    }

    public int indexOf(double element){
        for(int i = 0; i < size; i++) if(list[i] == element) return i;
        return -1;
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(list);
    }

    /**
     * Remove the given element from the list.
     * @param element Element to remove
     * @return true if the element was in the list, false otherwise.
     */
    public boolean removeElement(double element){
        int index = indexOf(element);
        if(index < 0) return false;
        remove(index);
        return true;
    }

    /**
     * Check if the element is in the list
     * @param element Element to check
     * @return true if the element is in the list, false otherwise
     */
    public boolean contains(double element){
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

    static final class Allocator extends AllocatorOf<ListOfDouble> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected ListOfDouble[] arrayCreation(int capacity) {
            return new ListOfDouble[capacity];
        }

        @Override
        protected ListOfDouble createObject(int index) {
            return new ListOfDouble(index);
        }
    }




    @Override
    public Iterator<Double> iterator() {
        iterator.i = 0;
        return iterator;
    }

    private class OrderedListOfDoubleIterator implements Iterator<Double> {

        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < size;
        }

        @Override
        public Double next() {
            return list[i++];
        }
    }

}
