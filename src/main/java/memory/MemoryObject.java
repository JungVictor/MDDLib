package memory;

public interface MemoryObject {

    void prepare();
    void setID(int ID);
    void free();
    boolean isComposed();

}
