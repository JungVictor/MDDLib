package builder.rules;

import dd.interfaces.NodeInterface;
import memory.AllocatorOf;
import structures.Domains;
import structures.generics.CollectionOf;

/**
 * The default SuccessionRule. <br>
 * The successors are the values in the domain for the given variable.
 */
public class SuccessionRuleDefault extends SuccessionRule {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    private Domains D;

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected SuccessionRuleDefault(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a SuccessionRuleDefault.
     * The object is managed by the allocator.
     * @param D The domain
     * @return A fresh SuccessionRuleDefault
     */
    public static SuccessionRuleDefault create(Domains D){
        SuccessionRuleDefault object = allocator().allocate();
        object.init(D);
        return object;
    }

    /**
     * Initialise the domain
     * @param D The domain
     */
    private void init(Domains D){
        this.D = D;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> successors(CollectionOf<Integer> successors, int layer, NodeInterface x) {
        return D.get(layer);
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

    /**
     * {@inheritDoc}
     */
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

        /**
         * {@inheritDoc}
         */
        @Override
        protected SuccessionRuleDefault[] arrayCreation(int capacity) {
            return new SuccessionRuleDefault[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SuccessionRuleDefault createObject(int index) {
            return new SuccessionRuleDefault(index);
        }
    }
}
