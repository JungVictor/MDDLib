package memory;

import structures.integers.StackOfInt;

/**
 * <b>AllocatorOf</b><br>
 * Memory Manager.<br>
 * @param <T> Type of the object managed by the AllocatorOf. Must be Allocable.
 */
public abstract class AllocatorOf<T extends Allocable> {

    // The array of elements
    private T[] elements;
    // The stack containing all indices that are not currently in use
    private StackOfInt inactiveIndices;
    // The number of elements in the allocator
    private int size = 0;

    /**
     * Create an array of the type T, with given capacity.
     * @param capacity The capacity of the array
     * @return Array T[] of length = capacity.
     */
    protected abstract T[] arrayCreation(int capacity);

    /**
     * Create an object of type T in the allocator at the specified index.
     * @param index Position of the object in memory
     * @return Object of type T.
     */
    protected abstract T createObject(int index);

    /**
     * Initialise the capacity of the allocator
     * @param capacity Capacity of the allocator
     */
    final protected void init(int capacity){
        elements = arrayCreation(capacity);
        inactiveIndices = new StackOfInt(capacity);
    }

    final protected void init(){
        init(10);
    }

    /**
     * Get the element located at the given index.
     * @param index Index of the wanted element
     * @return The element at given index (null if in use OR not allocated)
     */
    final public T get(int index){
        T element = elements[index];
        elements[index] = null;
        return element;
    }

    /**
     * Allocate an element of type T. <br>
     * If there are some free element, return a free element. Otherwise, create a new one.
     * The index of the element is automatically decided by the allocator.
     * @return Object of type T.
     */
    final public T allocate(){
        if(inactiveIndices.isEmpty()) {
            if(size == elements.length) expand();
            return createObject(size++);
        }
        return get(inactiveIndices.pop());
    }

    /**
     * The number of created elements.
     * @return The number of created elements.
     */
    final public int numberOfFreeElements(){
        return inactiveIndices.size();
    }

    /**
     * The maximum number of elements the allocator can hold.
     * @return The capacity of the allocator.
     */
    final public int capacity(){
        return elements.length;
    }

    /**
     * Expand the size of the allocator.
     */
    private void expand() {
        T[] e = arrayCreation(size + size);
        System.arraycopy(elements, 0, e, 0, size);
        elements = e;
    }

    /**
     * Free the given element. That is to say, put it back in the allocator
     * for later use.
     * @param element The element to push back in the memory
     */
    final public void free(T element){
        if(elements[element.allocatedIndex()] != null) return;
        inactiveIndices.push(element.allocatedIndex());
        elements[element.allocatedIndex()] = element;
    }
}

