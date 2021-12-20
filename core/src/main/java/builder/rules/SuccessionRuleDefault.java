package builder.rules;

import mdd.components.Node;
import memory.AllocatorOf;
import structures.Domains;
import structures.generics.CollectionOf;

/**
 * The default SuccesionRule. <br>
 * The successors are the values in the domain for the given variable.
 */
public class SuccessionRuleDefault extends SuccessionRule {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private Domains D;

    public SuccessionRuleDefault(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public Iterable<Integer> successors(CollectionOf<Integer> successors, int layer, Node x) {
        return D.get(layer);
    }

    public void init(Domains D){
        this.D = D;
    }

    public static SuccessionRuleDefault create(Domains D){
        SuccessionRuleDefault object = allocator().allocate();
        object.init(D);
        return object;
    }

    //**************************************//
    //          SPECIAL FUNCTIONS           //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    @Override
    public void free() {
        this.D = null;
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the SuccessionRuleDefault type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<SuccessionRuleDefault> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected SuccessionRuleDefault[] arrayCreation(int capacity) {
            return new SuccessionRuleDefault[capacity];
        }

        @Override
        protected SuccessionRuleDefault createObject(int index) {
            return new SuccessionRuleDefault(index);
        }
    }
}
