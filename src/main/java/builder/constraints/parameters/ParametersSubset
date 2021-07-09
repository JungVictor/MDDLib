package builder.constraints.parameters;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.integers.ArrayOfInt;

import java.util.ArrayList;
import java.util.HashMap;

public class ParametersSubset implements MemoryObject {


    // MemoryObject variables
    private final MemoryPool<ParametersSubset> pool;
    private int ID = -1;
    //

    ArrayList<SubsetData> datas;

    public ParametersSubset(MemoryPool<ParametersSubset> pool) {
        this.pool = pool;
    }

    public void init(ArrayOfInt word, int alphabetSize){
        datas = new ArrayList<>(word.length+1);
        for(int i = 0; i < word.length+1; i++){
            datas.add(i, new SubsetData());
            datas.get(i).init(i, word, alphabetSize);
        }
    }

    public boolean isIn(int ID, int label){
        return datas.get(ID).next.containsKey(label);
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
    public void prepare() {

    }

    @Override
    public void setID(int ID) {

    }

    @Override
    public void free() {

    }


    private class SubsetData implements MemoryObject {

        HashMap<Integer, Integer> next = new HashMap<>();

        public void init(int position, ArrayOfInt word, int alphabetSize){
            this.next = new HashMap<>();
            for(int i = position; i < word.length; i++){
                if(!next.containsKey(word.get(i))) next.put(word.get(i), i+1);
                if(next.size() == alphabetSize) break;
            }
            next.put(-1,-1);
        }

        @Override
        public void prepare() {

        }

        @Override
        public void setID(int ID) {

        }

        @Override
        public void free() {

        }
    }

}
