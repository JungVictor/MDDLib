package structures.lists;

import dd.interfaces.INode;
import memory.Allocable;

import java.util.Arrays;
import java.util.Iterator;

public abstract class AbstractUnorderedListOfNodes<E extends INode> implements Allocable, Iterable<E> {

    // Index in Memory
    private final int allocatedIndex;

    private final UnorderedListOfNodeIterator iterator = new UnorderedListOfNodeIterator();
    private int size;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    protected AbstractUnorderedListOfNodes(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    @Override
    public String toString(){
        return Arrays.toString(list());
    }

    //**************************************//
    //           LIST MANAGEMENT            //
    //**************************************//

    public abstract E[] list();
    protected abstract E[] createList(int capacity);
    protected abstract void setList(E[] list);

    /**
     * Get the number of elements in the list
     * @return the number of elements in the list
     */
    public int size(){
        return size;
    }

    /**
     * Get the capacity of the list
     * @return The capacity of the list
     */
    public int capacity(){
        return list().length;
    }

    /**
     * Get the value of the element at the specified position.
     * @param index Position of the element
     * @return the value of the element at the specified position
     */
    public E get(int index){
        return list()[index];
    }

    /**
     * Add the element at the end of the list
     * @param element Element to add
     */
    public void add(E element){
        if(size >= list().length) {
            E[] newlist = createList(size + size / 3);
            System.arraycopy(list(), 0, newlist, 0, list().length);
            setList(newlist);
        }
        list()[size++] = element;
    }

    /**
     * Set the element in the given position to the given value.
     * @param index Position in the list
     * @param element Value of the element
     */
    public void set(int index, E element){
        list()[index] = element;
    }

    /**
     * Add all elements in the given Iterable data structure at the end of this list.
     * @param iterable The data structure containing the elements to add
     */
    public void add(Iterable<E> iterable) {
        for(E e : iterable) add(e);
    }

    /**
     * Remove the element at the specified index
     * @param index Index of the element to remove
     * @return The element removed (null if none)
     */
    public E remove(int index){
        E[] list = list();
        E element = list[index];
        list[index] = list[size-1];
        size--;
        return element;
    }

    public int indexOf(E element){
        E[] list = list();
        for(int i = 0; i < size; i++) if(list[i] == element) return i;
        return -1;
    }

    /**
     * Remove the given element from the list.
     * @param element Element to remove
     * @return true if the element was in the list, false otherwise.
     */
    public boolean removeElement(E element){
        E[] list = list();
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
    public boolean contains(E element){
        return indexOf(element) >= 0;
    }

    /**
     * Clear all elements in the list
     */
    public void clear(){
        E[] list = list();
        for(int i = 0; i < size; i++) list[i] = null;
        size = 0;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public Iterator<E> iterator() {
        iterator.i = 0;
        return iterator;
    }

    private class UnorderedListOfNodeIterator implements Iterator<E> {

        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < size;
        }

        @Override
        public E next() {
            return list()[i++];
        }
    }

}
