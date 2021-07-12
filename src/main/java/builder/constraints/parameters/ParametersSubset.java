package builder.constraints.parameters;

import memory.Memory;
import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;

import java.util.ArrayList;
import java.util.HashMap;

public class ParametersSubset implements MemoryObject {


    // MemoryObject variables
    private final MemoryPool<ParametersSubset> pool;
    private int ID = -1;
    //

    private final ArrayList<SubsetData> datas = new ArrayList<>();

    public ParametersSubset(MemoryPool<ParametersSubset> pool) {
        this.pool = pool;
    }

    public void init(ArrayOfInt word, int alphabetSize, int maxSequenceSize){
        for(int i = 0; i < word.length+1; i++){
            datas.add(i, Memory.SubsetData(i, word, alphabetSize, maxSequenceSize));
        }
    }

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

    @Override
    public void prepare() {}

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        for(SubsetData data : datas) Memory.free(data);
        datas.clear();
        this.pool.free(this, ID);
    }


    public static class SubsetData implements MemoryObject {

        // MemoryObject variables
        private final MemoryPool<SubsetData> pool;
        private int ID = -1;
        //

        private MapOf<Integer, Integer> next;

        public SubsetData(MemoryPool<SubsetData> pool) {
            this.pool = pool;
        }

        public void init(int position, ArrayOfInt word, int alphabetSize, int maxSequenceSize){
            this.next = Memory.MapOfIntegerInteger();
            for(int i = position; i < maxSequenceSize; i++){
                if(!next.contains(word.get(i))) next.put(word.get(i), i+1);
                if(next.size() == alphabetSize) break;
            }
            next.put(-1,-1);
        }

        @Override
        public void prepare() {}

        @Override
        public void setID(int ID) {
            this.ID = ID;
        }

        @Override
        public void free() {
            Memory.free(next);
            this.pool.free(this, ID);
        }
    }

}
