package builder.rules;

import dd.interfaces.NodeInterface;
import memory.AllocatorOf;
import memory.Memory;
import structures.generics.CollectionOf;

/**
 * The Legendary Chupapaya SuccessionRule. <br>
 * The successors are the possible successors of all in-going labels for a given node
 */
public class SuccessionRuleChupapaya extends SuccessionRule {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    //private Trie trie

    public SuccessionRuleChupapaya(int allocatedIndex) {
        super(allocatedIndex);
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

        for (int inLabel : x.iterateOnParentLabels()) {
            // successors.add(successeurs de inLabel)
        }

        return successors;
    }

    public void init(){
        //this.trie = trie;
    }

    public static SuccessionRuleChupapaya create(){
        SuccessionRuleChupapaya object = allocator().allocate();
        //object.init(trie);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        //this.trie = null;
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the SuccessionRuleChupapaya type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<SuccessionRuleChupapaya> {

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
        protected SuccessionRuleChupapaya[] arrayCreation(int capacity) {
            return new SuccessionRuleChupapaya[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SuccessionRuleChupapaya createObject(int index) {
            return new SuccessionRuleChupapaya(index);
        }
    }
}
