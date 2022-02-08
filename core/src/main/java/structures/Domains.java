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

    /**
     * Add all domain up to the given index
     * @param index
     */
    public void add(int index){
        while(domains.size() <= index) domains.add(Memory.SetOfInteger());
    }

    /**
     * Add a value to the domain at given index
     * @param index Index of the domain
     * @param value Value to add
     */
    public void put(int index, int value){
        if(domains.size() <= index) add(index);
        domains.get(index).add(value);
    }

    /**
     * Remove the given value from the domain at given index
     * @param index Index of the domain
     * @param value Value to remove
     */
    public void remove(int index, int value){
        domains.get(index).remove(value);
    }

    /**
     * Get all values in the domain at given index
     * @param index Index of the domain
     * @return ALl values in the domain at given index
     */
    public SetOf<Integer> get(int index){
        return domains.get(index);
    }

    /**
     * The size of the domain at given index
     * @param index Index of the domain
     * @return Size of the domain at given index
     */
    public int size(int index){
        return domains.get(index).size();
    }

    /**
     * Number of variables
     * @return The number of variables
     */
    public int size(){
        return domains.size();
    }

    /**
     * Clear all domains
     */
    public void clear(){
        for(SetOf<Integer> domain : domains) Memory.free(domain);
        domains.clear();
    }

    /**
     * Perform the union with this domain and the given domain
     * @param D The domain to unify with
     */
    public void union(Domains D){
        for(int i = 0; i < domains.size(); i++) domains.get(i).add(D.get(i));
        if(D.domains.size() > domains.size()) {
            for(int i = domains.size(); i < D.domains.size(); i++) {
                add(i);
                domains.get(i).add(D.get(i));
            }
        }
    }

    /**
     * Perform the intersection of this domain with the given domain.
     * @param D To domain to intersect with
     */
    public void intersect(Domains D){
        for(int i = 0; i < domains.size(); i++) domains.get(i).intersect(D.get(i));
    }

    /**
     * Fill V with the values in the domain of all variables
     * @param V The set of values to fill
     */
    public void fillWithValues(SetOf<Integer> V){
        for(SetOf<Integer> values : domains) V.add(values);
    }

    /**
     * Fill the ith domain with values in V
     * @param i The index of the domain
     * @param V The set of values to add
     */
    public void fill(int i, SetOf<Integer> V) {
        for(int v : V) put(i, v);
    }

    /**
     * Fill the ith domain with values in [begin, end]
     * @param i The index of the domain
     * @param begin Starting value of the interval
     * @param end Ending value of the interval
     */
    public void fill(int i, int begin, int end){
        for(int v = begin; v <= end; v++) put(i, v);
    }

    /**
     * Fill all domains up to the given index with values in [begin, end]
     * @param idx The index of the domain
     * @param begin Starting value of the interval
     * @param end Ending value of the interval
     */
    public void fillAll(int idx, int begin, int end){
        for(int i = 0; i < idx; i++) fill(i, begin, end);
    }

    /**
     * Fill all domains up to the given index with values in [begin, end]
     * @param idx The index of the domain
     * @param V The set of values to add
     */
    public void fillAll(int idx, SetOf<Integer> V){
        for(int i = 0; i < idx; i++) fill(i, V);
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
