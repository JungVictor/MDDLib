package builder.constraints.parameters;

import memory.*;
import structures.generics.MapOf;
import structures.arrays.ArrayOfInt;
import structures.generics.SetOf;

import java.util.ArrayList;

public class ParametersSubset extends ConstraintParameters{

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);


    private final ArrayList<SubsetData> datas = new ArrayList<>();


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

    private ParametersSubset(int allocatedIndex){
        super(allocatedIndex);
    }

    public void init(ArrayOfInt word, int alphabetSize, int maxSequenceSize, SetOf<Integer> variables){
        for(int i = 0; i < word.length+1; i++){
            datas.add(i, SubsetData.create(i, word, alphabetSize, maxSequenceSize));
        }
        super.setVariables(variables);
    }

    public static ParametersSubset create(ArrayOfInt word, int alphabetSize, int maxSequenceSize, SetOf<Integer> variables){
        ParametersSubset object = allocator().allocate();
        object.init(word, alphabetSize, maxSequenceSize, variables);
        return object;
    }

    //**************************************//

    public boolean isIn(int ID, int label){
        return datas.get(ID).next.contains(label);
    }

    public int getNext(int ID, int label){
        if(ID == -1) return -1;
        if(datas.get(ID).next.size() == 0) return -1;
        return datas.get(ID).next.get(label);
    }

    public boolean isEmpty(int ID){
        if(ID == -1) return true;
        return datas.get(ID).next.size() == 0;
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void free() {
        super.free();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the ParametersSubset type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<ParametersSubset> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected ParametersSubset[] arrayCreation(int capacity) {
            return new ParametersSubset[capacity];
        }

        @Override
        protected ParametersSubset createObject(int index) {
            return new ParametersSubset(index);
        }
    }


    //**************************************//
    //              SUBSET DATA             //
    //**************************************//

    public static class SubsetData implements Allocable {

        // Thread safe allocator
        private final static ThreadLocal<SubsetData.Allocator> localStorage = ThreadLocal.withInitial(SubsetData.Allocator::new);
        // Index in Memory
        private final int allocatedIndex;

        private MapOf<Integer, Integer> next;

        //**************************************//
        //           INITIALISATION             //
        //**************************************//

        /**
         * Get the allocator. Thread safe.
         * @return The allocator.
         */
        private static SubsetData.Allocator allocator(){
            return localStorage.get();
        }

        public SubsetData(int allocatedIndex) {
            this.allocatedIndex = allocatedIndex;
        }

        public void init(int position, ArrayOfInt word, int alphabetSize, int maxSequenceSize){
            this.next = Memory.MapOfIntegerInteger();
            for(int i = position; i < maxSequenceSize; i++){
                if(!next.contains(word.get(i))) next.put(word.get(i), i+1);
                if(next.size() == alphabetSize) break;
            }
            next.put(-1,-1);
        }

        public static SubsetData create(int position, ArrayOfInt word, int alphabetSize, int maxSequenceSize){
            SubsetData data = allocator().allocate();
            data.init(position, word, alphabetSize, maxSequenceSize);
            return data;
        }

        //**************************************//
        //           MEMORY FUNCTIONS           //
        //**************************************//
        // Implementation of MemoryObject interface

        @Override
        public int allocatedIndex(){
            return allocatedIndex;
        }

        @Override
        public void free() {
            Memory.free(next);
            allocator().free(this);
        }

        /**
         * <b>The allocator that is in charge of the SubsetData type.</b><br>
         * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
         * can be change if needed (might improve/decrease performance and/or memory usage).
         */
        static final class Allocator extends AllocatorOf<SubsetData> {

            Allocator(int capacity) {
                super.init(capacity);
            }

            Allocator(){
                this(16);
            }

            @Override
            protected SubsetData[] arrayCreation(int capacity) {
                return new SubsetData[capacity];
            }

            @Override
            protected SubsetData createObject(int index) {
                return new SubsetData(index);
            }
        }
    }

}
