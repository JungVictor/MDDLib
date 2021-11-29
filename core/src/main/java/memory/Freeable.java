package memory;

/**
 * Object that can be freed from memory
 */
public interface Freeable {

    /**
     * Free the object
     */
    void free();

}
