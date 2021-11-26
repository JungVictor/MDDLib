package builder.rules.operations;

import builder.rules.SuccessionRule;
import mdd.components.Node;
import memory.AllocatorOf;
import structures.lists.ListOfInt;

/**
 * The SuccesionRule for the union operator. <br/>
 * The successors are the values of all out-going labels of nodes associated to the given node.
 */
public class SuccessionRuleUnion extends SuccessionRule {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    public SuccessionRuleUnion(int allocatedIndex) {
        super(allocatedIndex);
    }

    @Override
    public Iterable<Integer> successors(ListOfInt successors, int layer, Node x) {
        successors.clear();
        for(Node n : x.getAssociations()) if(n != null) successors.add(n.getValues());
        return successors;
    }

    public static SuccessionRuleUnion create(){
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

        @Override
        protected SuccessionRuleUnion[] arrayCreation(int capacity) {
            return new SuccessionRuleUnion[capacity];
        }

        @Override
        protected SuccessionRuleUnion createObject(int index) {
            return new SuccessionRuleUnion(index);
        }
    }
}
