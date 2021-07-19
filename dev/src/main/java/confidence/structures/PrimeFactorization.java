package confidence.structures;

import confidence.parameters.ParametersMul;
import memory.Allocable;
import memory.AllocatorOf;
import structures.lists.ListOfInt;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * <b>Class which represent a integer by its decomposition into prime numbers</b><br>
 * This class helps to compress big integer to take less space in the memory, and potentially improve multiplication or equality performance.
 */
public class PrimeFactorization implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Index in Memory
    private final int allocatedIndex;

    // Static attributes
    private static ListOfInt primeNumbers = ListOfInt.create(); // List of prime number until maxNumberKnown
    private static int maxNumberKnown = 1; // Important to initialize to 1


    // Attributes
    private LinkedHashMap<Integer, Integer> decomposition = new LinkedHashMap<>(); // The representation by decomposition into prime numbers


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

    private PrimeFactorization(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void init(int n){
        if(n < 1) throw new IllegalArgumentException("n must be greater than 0, given n = " + n);

        // 1 is representing by an empty LinkedHashMap
        if(n != 1) {
            int tmp = n;

            // Only need to know the prime numbers until n/2
            int maxToCheck = n / 2;
            setPrimeNumbers(maxToCheck);

            int i = 0;
            int primeNumber = 2;

            // While all the prime numbers until maxToCheck are not check
            while (primeNumber <= maxToCheck && i < primeNumbers.size()) {

                primeNumber = primeNumbers.get(i);

                if (tmp % primeNumber == 0) {
                    if (!decomposition.containsKey(primeNumber)) decomposition.put(primeNumber, 1);
                    else decomposition.put(primeNumber, decomposition.get(primeNumber) + 1);
                    tmp /= primeNumber;
                } else i++;
            }

            // If there is no decomposition with these prime numbers then n is a prime number
            if (decomposition.size() == 0) decomposition.put(n, 1);
        }
    }

    public static PrimeFactorization create(int max){
        PrimeFactorization object = allocator().allocate();
        object.init(max);
        return object;
    }


    // Static methods
    public static int[] getPrimeNumbers(){
        int size = primeNumbers.size();
        int[] result = new int[size];
        for(int i = 0; i < size; i++) result[i] = primeNumbers.get(i);
        return result;
    }


    public static String PrimeNumbersToString(){
        int size = primeNumbers.size();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < size - 1; i++) {
            builder.append(primeNumbers.get(i));
            builder.append(", ");
        }
        if(size > 0) builder.append(primeNumbers.get(size - 1));
        builder.append("]");
        return builder.toString();
    }


    /**
     * <b>Extend the list of prime numbers until max</b>
     * @param max An integer
     */
    public static void setPrimeNumbers(int max){
        try {
            // To avoid unnecessary computation, prime numbers are safe in a file
            File file = new File("primeNumbers.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileReader fr = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);
            String primeNumber = br.readLine();
            int last = -1;

            while(primeNumber != null){
                last = Integer.parseInt(primeNumber);
                primeNumbers.add(last);
                primeNumber = br.readLine();
            }
            br.close();

            maxNumberKnown = Math.max(last, 1);
            int oldMax = maxNumberKnown;

            // The algorithm compute prime numbers from the bigger prime number known...
            if(oldMax < max) {
                for (int i = oldMax + 1; i <= max; i++) {
                    boolean isPrimeNumber = true;

                    for (int e : primeNumbers) {
                        if (i % e == 0) isPrimeNumber = false;
                    }

                    if (isPrimeNumber) primeNumbers.add(i);
                }
                maxNumberKnown = max;

                // ...and add them in the file
                StringBuilder builder = new StringBuilder();
                for(int e : primeNumbers){
                    builder.append(e);
                    builder.append("\n");
                }
                String content = builder.toString();

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Methods
    public PrimeFactorization multiply(PrimeFactorization other){
        PrimeFactorization result = create(1);

        Iterator<Integer> iteratorI = this.decomposition.keySet().iterator();
        Iterator<Integer> iteratorJ = other.decomposition.keySet().iterator();

        int i = -1;
        int j = -1;
        if(iteratorI.hasNext()) i = iteratorI.next();
        if(iteratorJ.hasNext()) j = iteratorJ.next();

        // While neither of the end of iteratorI or iteratorJ is reached
        while(i != -1 || j != -1){

            // If the end of iteratorI is reached
            if(i == -1){
                result.decomposition.put(j, other.decomposition.get(j));
                j = iteratorNext(iteratorJ);
            }
            // If the end of iteratorJ is reached
            else if(j == -1){
                result.decomposition.put(i, this.decomposition.get(i));
                i = iteratorNext(iteratorI);
            }
            else {
                if(i < j){
                    result.decomposition.put(i, this.decomposition.get(i));
                    i = iteratorNext(iteratorI);
                }
                else if(j < i){
                    result.decomposition.put(j, other.decomposition.get(j));
                    j = iteratorNext(iteratorJ);
                }
                else {
                    result.decomposition.put(i, this.decomposition.get(i) + other.decomposition.get(j));
                    i = iteratorNext(iteratorI);
                    j = iteratorNext(iteratorJ);
                }
            }
        }
        return result;
    }

    private int iteratorNext(Iterator<Integer> i){
        if(i.hasNext()) return i.next();
        return -1;
    }

    public double toLog10(){
        double result = 0;
        for(int k : decomposition.keySet()) result += Math.log10(k) * decomposition.get(k);
        return result;
    }

    public PrimeFactorization copy(){
        PrimeFactorization result = new PrimeFactorization(1);
        for(int k : this.decomposition.keySet()) result.decomposition.put(k, this.decomposition.get(k));
        return result;
    }

    @Override
    public String toString(){
        return decomposition.toString();
    }

    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//


    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free() {
        this.decomposition.clear();
        allocator().free(this);
    }

    /**
     * <b>The allocator that is in charge of the PrimeFactorization type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<PrimeFactorization> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected PrimeFactorization[] arrayCreation(int capacity) { return new PrimeFactorization[capacity]; }

        @Override
        protected PrimeFactorization createObject(int index) {
            return new PrimeFactorization(index);
        }
    }
}
