package memory;

public interface MemoryObject {

    /**
     * Prepare the object to be usable as a new one
     */
    void prepare();

    /**
     * Set the ID of the object in memory. That technically is its position in the stack.
     * @param ID
     */
    void setID(int ID);

    /**
     * Free the object
     */
    void free();


    default boolean isAtomic() {
        return true;
    }

}
