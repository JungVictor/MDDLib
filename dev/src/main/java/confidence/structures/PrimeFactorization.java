package confidence.structures;

import memory.Memory;
import structures.generics.MapOf;
import structures.lists.ListOfInt;

/**
 * <b>Class which represent a integer by its decomposition into prime numbers</b><br>
 * This class helps to compress big integer to take less space in the memory, and potentially improve multiplication or equality performance.
 */
public class PrimeFactorization {


    // Static attributes
    private static ListOfInt primeNumbers = ListOfInt.create(); // List of prime number until maxNumberKnown
    private static int maxNumberKnown = 1; // Important to initialize to 1


    // Attributes
    private MapOf<Integer, Integer> decomposition = Memory.MapOfIntegerInteger(); // The representation by decomposition into prime numbers


    // Constructor
    public PrimeFactorization(int n){
        if (n < 2) throw new IllegalArgumentException("n must be greater than 1, given n = " + n);
        int tmp = n;

        // Only need to know the prime numbers until n/2
        int maxToCheck = n / 2;
        setPrimeNumbers(maxToCheck);

        int i = 0;
        int primeNumber = 2;

        // While all the prime numbers until maxToCheck are not check
        while(primeNumber <= maxToCheck && i < primeNumbers.size()){

            primeNumber = primeNumbers.get(i);

            if(tmp % primeNumber == 0){
                if(!decomposition.contains(primeNumber)) decomposition.put(primeNumber, 1);
                else decomposition.put(primeNumber, decomposition.get(primeNumber) + 1);
                tmp /= primeNumber;
            }
            else i++;
        }

        // If there is no decomposition with these prime numbers then n is a prime number
        if(decomposition.size() == 0) decomposition.put(n, 1);
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
     * Extend the list of prime numbers until max
     * @param max
     */
    public static void setPrimeNumbers(int max){
        int oldMax = maxNumberKnown;
        for(int i = oldMax+1; i <= max; i++){
            boolean isPrimeNumber = true;

            for(int e : primeNumbers){
                if( i % e == 0) isPrimeNumber = false;
            }

            if(isPrimeNumber) primeNumbers.add(i);
        }
        maxNumberKnown = max;
    }


    // Methods
    @Override
    public String toString(){
        return decomposition.toString();
    }



    /*
    TODO :
    Méthodes :
    Multiplication
    Test d'égalité
    Comparaison
    Logarithme
     */
}
