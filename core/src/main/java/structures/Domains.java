package structures;

import memory.*;
import structures.generics.SetOf;

import java.util.ArrayList;

public class Domains implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Allocated index
    private final int allocatedIndex;
    //

    private final ArrayList<SetOf<Integer>> domains = new ArrayList<>();


    //**************************************//
    //           INITIALISATION             //
    //**************************************//
    private Domains(int allocatedIndex) {
        this.allocatedIndex = allocatedIndex;
    }

    /**
     * Create an Domains.
     * The object is managed by the allocator.
     * @return A Domains.
     */
    public static Domains create(){
        return allocator().allocate();
    }
    public static Domains create(int size){
        Domains D = allocator().allocate();
        D.add(size);
        return D;
    }

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }

    //**************************************//
    //             OPERATIONS               //
    //**************************************//

    public void add(int index){
        while(domains.size() <= index) domains.add(Memory.SetOfInteger());
    }

    public void put(int index, int value){
        if(domains.size() <= index) add(index);
        domains.get(index).add(value);
    }

    public void remove(int index, int value){
        domains.get(index).remove(value);
    }

    public SetOf<Integer> get(int index){
        return domains.get(index);
    }

    public int size(int index){
        return domains.get(index).size();
    }

    public int size(){
        return domains.size();
    }

    public void clear(){
        for(SetOf<Integer> domain : domains) Memory.free(domain);
        domains.clear();
    }

    public void union(Domains D){
        for(int i = 0; i < domains.size(); i++) domains.get(i).add(D.get(i));
        if(D.domains.size() > domains.size()) {
            for(int i = domains.size(); i < D.domains.size(); i++) {
                add(i);
                domains.get(i).add(D.get(i));
            }
        }
    }

    public void intersect(Domains D){
        for(int i = 0; i < domains.size(); i++) domains.get(i).intersect(D.get(i));
    }

    public void fillWithValues(SetOf<Integer> V){
        for(SetOf<Integer> values : domains) V.add(values);
    }

    public void fill(int i, int begin, int end){
        for(int v = begin; v <= end; v++) domains.get(i).add(v);
    }

    public void fillAll(int idx, int begin, int end){
        for(int i = 0; i < idx; i++) fill(i, begin, end);
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public void free() {
        clear();
        allocator().free(this);
    }


    /**
     * <b>The allocator that is in charge of the Domains type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<Domains> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected Domains[] arrayCreation(int capacity) {
            return new Domains[capacity];
        }

        @Override
        protected Domains createObject(int index) {
            return new Domains(index);
        }
    }
}
