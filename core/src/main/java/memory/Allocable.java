package memory;

/**
 * Object that is manage by an AllocatorOf
 */
public interface Allocable extends Freeable {

    /**
     * Get the index of the object in the allocator
     * @return The index of the object in the allocator
     */
    int allocatedIndex();

}