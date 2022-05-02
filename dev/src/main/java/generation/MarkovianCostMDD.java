package generation;

import dd.interfaces.INode;
import dd.mdd.components.Node;
import dd.mdd.costmdd.CostMDD;
import dd.mdd.costmdd.components.StateCostNode;
import generation.states.StateGram;
import generation.utils.Reverso;
import memory.AllocatorOf;
import utils.Logger;

import java.util.HashMap;
import java.util.Random;

public class MarkovianCostMDD extends CostMDD {

    public static HashMap<Integer, Double> probabilities = new HashMap<>();
    public final static ThreadLocal<MarkovianCostMDD.Allocator> localStorage = ThreadLocal.withInitial(MarkovianCostMDD.Allocator::new);

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    protected MarkovianCostMDD(int allocatedIndex) {
        super(allocatedIndex);
    }

    /**
     * Create a CostMDD with given node as root.
     * The object is managed by the allocator.
     * @param root Node to use as a root
     * @return A fresh CostMDD
     */
    public static MarkovianCostMDD create(Node root){
        MarkovianCostMDD mdd = allocator().allocate();
        mdd.setRoot(root);
        return mdd;
    }

    /**
     * Create a CostMDD.
     * The object is managed by the allocator.
     * @return A fresh CostMDD
     */
    public static MarkovianCostMDD create(){
        MarkovianCostMDD mdd = allocator().allocate();
        mdd.setRoot(mdd.Node());
        return mdd;
    }


    public void AddMarkovianCost(Reverso reverso) {
        int key=0;
        double probabilité=0;
        for (int i = 1; i < size(); i++) {
            Logger.out.information("\rLAYER " + i);
            for (INode node : super.getLayer(i - 1)) {
                StateCostNode Sn = (StateCostNode) node;
                StateGram myState = (StateGram) Sn.getState();
                for (int label : Sn.iterateOnChildLabels()) {
                    if(i == 0) {
                        probabilité=1;
                    }

                    else{System.out.println(myState); probabilité = myState.proba(label);}
                    key++;
                    probabilities.put(key,probabilité);
                    // TODO : transformer probabilité en int : (hashmap double -> integer)
                    System.out.println("\n");
                    System.out.println(label);
                    System.out.println(reverso.IntToWord(label));
                    System.out.println(probabilité);
                    Sn.setArcCost(label, key);
                }
            }
        }
    }

    /***
     *
     * @param probabilities hashmap integer -> probabilité
     * @return
     */
    public int[] stochasticRandomWalk(HashMap<Integer, Double> probabilities){
        StateCostNode current = (StateCostNode) super.getRoot();
        Random random = new Random();
        int[] path = new int[super.size()-1];
        int i=0;
        while (current != super.getTt()){
            double sumProba = 0.0;
            for(int idx = 0; idx < current.numberOfChildren(); idx++){
                //sumProba += probabilities.get(current.getValue(idx));
                sumProba += probabilities.get(current.getArcCost(current.getValue(idx)));
            }
            double randomSelection = random.nextDouble() * sumProba;
            double cumulativeProba = probabilities.get(current.getArcCost(current.getValue(0)));
            int idx = 1;
            while (cumulativeProba < randomSelection && idx < current.numberOfChildren()){
                cumulativeProba += probabilities.get(current.getArcCost(current.getValue(idx)));
                idx++;
            }
            path[i++] = current.getValue(idx-1);
            current = (StateCostNode) current.getChild(path[i-1]);
        }
        return path;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dealloc(){
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the CostMDD type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<MarkovianCostMDD> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected MarkovianCostMDD[] arrayCreation(int capacity) {
            return new MarkovianCostMDD[capacity];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected MarkovianCostMDD createObject(int index) {
            return new MarkovianCostMDD(index);
        }
    }
}
