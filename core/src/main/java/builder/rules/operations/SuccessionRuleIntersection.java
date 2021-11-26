package builder.rules.operations;

import builder.rules.SuccessionRule;
import mdd.components.Node;
import memory.AllocatorOf;
import structures.lists.ListOfInt;

/**
 * The SuccesionRule for intersection. <br/>
 * The successors are the out-going label of the first node associated.
 */
public class SuccessionRuleIntersection extends SuccessionRule {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    public SuccessionRuleIntersection(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public Iterable<Integer> successors(ListOfInt successors, int layer, Node x) {
        return x.getX1().getValues();
    }

    public static SuccessionRuleIntersection create(){
        return allocator().allocate();
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

        @Override
        protected SuccessionRuleIntersection[] arrayCreation(int capacity) {
            return new SuccessionRuleIntersection[capacity];
        }

        @Override
        protected SuccessionRuleIntersection createObject(int index) {
            return new SuccessionRuleIntersection(index);
        }
    }
}
