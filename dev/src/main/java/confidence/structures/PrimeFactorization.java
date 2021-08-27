package confidence.structures;

import memory.Allocable;
import memory.AllocatorOf;
import structures.lists.ListOfInt;

import java.io.*;
import java.util.Iterator;

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
    private ListOfInt factors = ListOfInt.create();
    private ListOfInt exponents = ListOfInt.create();

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

        // 1 is representing by two empty list
        if(n != 1) {
            int tmp = n;

            // Only need to know the prime numbers until n/2
            int maxToCheck = n / 2;
            setPrimeNumbers(maxToCheck);

            int i = 0;
            int primeNumber = 2;

            while (tmp != 1 && i < primeNumbers.size()) {

                primeNumber = primeNumbers.get(i);

                if (tmp % primeNumber == 0) {
                    //Si on a aucune valeur dans les listes ou que l'on divise par un nouveau nombre premier
                    if(factors.size() == 0 || factors.get(factors.size()-1) != primeNumber){
                        factors.add(primeNumber);
                        exponents.add(1);
                    }
                    //Si on continue de diviser par le même nombre premier
                    else{
                        exponents.set(exponents.size()-1, exponents.get(exponents.size()-1)+1);
                    }

                    tmp /= primeNumber;
                } else i++;
            }

            // If there is no decomposition with these prime numbers then n is a prime number
            if (factors.size() == 0){
                factors.add(n);
                exponents.add(1);
            }
        }
    }

    public static PrimeFactorization create(int n){
        PrimeFactorization object = allocator().allocate();
        object.init(n);
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
            int count = 0;

            while(primeNumber != null){
                count++;
                last = Integer.parseInt(primeNumber);
                if(count > primeNumbers.size()) { primeNumbers.add(last); }
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

        int i = 0;
        int j = 0;

        // While neither of the end of iteratorI or iteratorJ is reached
        while(i < this.factors.size() || j < other.factors.size()){

            // If the end of this is reached
            if(i >= factors.size()){
                result.factors.add(other.factors.get(j));
                result.exponents.add(other.exponents.get(j));
                j++;
            }
            // If the end of other is reached
            else if(j >= other.factors.size()){
                result.factors.add(this.factors.get(i));
                result.exponents.add(this.exponents.get(i));
                i++;
            }
            else {
                if(this.factors.get(i) < other.factors.get(j)){
                    result.factors.add(this.factors.get(i));
                    result.exponents.add(this.exponents.get(i));
                    i++;
                }
                else if(other.factors.get(j) < this.factors.get(i)){
                    result.factors.add(other.factors.get(j));
                    result.exponents.add(other.exponents.get(j));
                    j++;
                }
                else {
                    result.factors.add(this.factors.get(i));
                    result.exponents.add(this.exponents.get(i) + other.exponents.get(j));
                    i++;
                    j++;
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
        for(int i = 0; i < factors.size(); i++){
            result += Math.log10(factors.get(i)) * exponents.get(i);
        }
        return result;
    }

    public PrimeFactorization copy(){
        PrimeFactorization result = new PrimeFactorization(1);
        for(int i = 0; i < factors.size(); i++){
            result.factors.add(this.factors.get(i));
            result.exponents.add(this.exponents.get(i));
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < factors.size(); i++) {
            builder.append(factors.get(i));
            builder.append(":");
            builder.append(exponents.get(i));
            builder.append(",");

        }
        return builder.toString();
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
        this.factors.clear();
        this.exponents.clear();
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
