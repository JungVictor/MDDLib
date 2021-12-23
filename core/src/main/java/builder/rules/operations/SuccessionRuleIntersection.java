package builder.rules.operations;

import builder.rules.SuccessionRule;
import dd.AbstractNode;
import memory.AllocatorOf;
import structures.generics.CollectionOf;

/**
 * The SuccessionRule for intersection. <br>
 * The successors are the out-going label of the first node associated.
 */
public class SuccessionRuleIntersection extends SuccessionRule {

    public static final SuccessionRuleIntersection RULE = new SuccessionRuleIntersection(-1);

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected SuccessionRuleIntersection(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a SuccessionRuleIntersection.
     * The object is managed by the allocator.
     * @return A fresh SuccessionRuleIntersection
     */
    public static SuccessionRuleIntersection create(){
        return allocator().allocate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> successors(CollectionOf<Integer> successors, int layer, AbstractNode x) {
        return x.getX1().iterateOnChildLabel();
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
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the SuccessionRuleIntersection type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<SuccessionRuleIntersection> {

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
        protected SuccessionRuleIntersection[] arrayCreation(int capacity) {
            return new SuccessionRuleIntersection[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SuccessionRuleIntersection createObject(int index) {
            return new SuccessionRuleIntersection(index);
        }
    }
}
