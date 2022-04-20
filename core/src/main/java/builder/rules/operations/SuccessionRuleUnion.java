package builder.rules.operations;

import builder.rules.SuccessionRule;
import dd.interfaces.NodeInterface;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.CollectionOf;
import structures.successions.SuccessionOfNodeInterface;

/**
 * The SuccessionRule for the union operator. <br>
 * The successors are the values of all out-going labels of nodes associated to the given node.
 */
public class SuccessionRuleUnion extends SuccessionRule {

    public static final SuccessionRuleUnion RULE = new SuccessionRuleUnion(-1);

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected SuccessionRuleUnion(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a SuccessionRuleUnion.
     * The object is managed by the allocator.
     * @return A fresh SuccessionRuleUnion
     */
    public static SuccessionRuleUnion create(){
        return allocator().allocate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionOf<Integer> getCollection(){
        return Memory.SetOfInteger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Integer> successors(CollectionOf<Integer> successors, int layer, NodeInterface x) {
        successors.clear();
        SuccessionOfNodeInterface associations = x.getAssociations();
        for(int i = 0; i < associations.length(); i++) {
            NodeInterface n = associations.get(i);
            if(n != null) successors.add(n.iterateOnChildLabels());
        }
        return successors;
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
     * <b>The allocator that is in charge of the SuccessionRuleUnion type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<SuccessionRuleUnion> {

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
        protected SuccessionRuleUnion[] arrayCreation(int capacity) {
            return new SuccessionRuleUnion[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SuccessionRuleUnion createObject(int index) {
            return new SuccessionRuleUnion(index);
        }
    }
}
