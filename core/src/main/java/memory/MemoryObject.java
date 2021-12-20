package memory;

/**
 * <b>MemoryObject is an interface that must be implemented if you want your object to be able to be reused.</b>
 */
public interface MemoryObject extends Freeable {

    /**
     * Prepare the object to be usable as a new one
     */
    void prepare();

    /**
     * Set the ID of the object in memory. That technically is its position in the stack.
     * @param ID The ID of the object in memory.
     */
    void setID(int ID);


}
