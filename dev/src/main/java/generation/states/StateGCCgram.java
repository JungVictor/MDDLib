package generation.states;

import builder.constraints.states.NodeState;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import generation.utils.Reverso;
import memory.AllocatorOf;

public class StateGCCgram extends NodeState {
    private final static ThreadLocal<StateGCCgram.Allocator> localStorage = ThreadLocal.withInitial(StateGCCgram.Allocator::new);
    public int cardinality = 0;
    public static int maxCardinality =1;
    public static Reverso reverso;
    public static String tagPOS = "VER";



    private static StateGCCgram.Allocator allocator(){
        return localStorage.get();
    }
    /**
     * Constructor of NodeState.
     * Initialise the allocated index.
     *
     * @param allocatedIndex Index of the object in the allocator
     */
    protected StateGCCgram(int allocatedIndex) {
        super(allocatedIndex);
    }

    public static StateGCCgram create(){
        StateGCCgram object = allocator().allocate();
        return object;
    }
    public void compose(StateGCCgram parent,int label, int layer, int size){
        if(reverso.getIntToPOS().get(label).startsWith(tagPOS)){
            if(parent.cardinality == maxCardinality){
                cardinality=parent.cardinality;
            }
            else{
                cardinality=parent.cardinality+1;
            }
        }
        else{
            cardinality= parent.cardinality;
        }

    }
    @Override
    public NodeState createState(int label, int layer, int size) {
        StateGCCgram state = StateGCCgram.create(); // call parameters

        state.compose(this,label, layer,size); //  how to go from current state to newxt states.
        return state;    }

    @Override
    public boolean isValid(int label, int layer, int size) {

        if(size-layer < cardinality-maxCardinality){
            if(!(reverso.getIntToPOS().get(label).startsWith(tagPOS))){
                return false;
            }
        }
        if(reverso.getIntToPOS().get(label).startsWith(tagPOS)) {
            if (cardinality == maxCardinality) {
                return false;
            } else {
                
                return true;
            }
        }

        return true;
    }

    @Override
    public String signature(int label, int layer, int size) {
        if(reverso.getIntToPOS().get(label).startsWith(tagPOS)) {
            return Integer.toString(cardinality + 1);
        }
        else{
                return Integer.toString(cardinality);
            }
    }

    @Override
    public void free() {

        allocator().free(this);
    }

    static final class Allocator extends AllocatorOf<StateGCCgram> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateGCCgram[] arrayCreation(int capacity) {
            return new StateGCCgram[capacity];
        }

        @Override
        protected StateGCCgram createObject(int index) { return new StateGCCgram(index);
        }

    }
}
