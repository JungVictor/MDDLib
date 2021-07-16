package memory;

public interface Allocable {

    /**
     * Get the index of the object in the allocator
     * @return The index of the object in the allocator
     */
    int allocatedIndex();

    /**
     * Free the object
     */
    void free();

}