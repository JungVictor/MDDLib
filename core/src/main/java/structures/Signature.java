package structures;

import memory.Allocable;
import memory.AllocatorOf;
import memory.Memory;
import structures.lists.ListOfDouble;

/**
 * This class is used as a way to represent the hash of a state.
 */
public class Signature implements Allocable {

    public final static Signature EMPTY = new Signature(-1);

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    private ListOfDouble signature;

    private Signature(int allocatedIndex) {
        this.allocatedIndex = allocatedIndex;
        if(allocatedIndex == -1) signature = ListOfDouble.create();
    }

    public void init(){
        signature = ListOfDouble.create();
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }


    public static Signature create(){
        Signature object = allocator().allocate();
        object.init();
        return object;
    }


    public void add(double n){
        signature.add(n);
    }

    public boolean equals(Signature other){
        if(signature == other.signature) return true;
        if(signature.size() != other.signature.size()) return false;
        for(int i = 0; i < signature.size(); i++) if(signature.get(i) != other.signature.get(i)) return false;
        return true;
    }

    @Override
    public int hashCode(){
        return signature.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Signature)) return false;
        return equals((Signature) other);
    }


    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public void free() {
        if(allocatedIndex < 0) return;
        Memory.free(signature);
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the Signature type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<Signature> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(256);
        }

        @Override
        protected Signature[] arrayCreation(int capacity) {
            return new Signature[capacity];
        }

        @Override
        protected Signature createObject(int index) {
            return new Signature(index);
        }
    }
}
