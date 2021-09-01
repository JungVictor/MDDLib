package utils;

public class SmallMath {

    private SmallMath(){}

    /**
     * Compute the logarithm of the given number in given base, up to given precision n.<br>
     * If you want to compute the log10(90.41) up to 5 digits : log(90.41, 10, 5).
     * @param number The number in integer form
     * @param base The base of the logarithm
     * @param n The number of digits for the log
     * @param ceil true if ceil, false for floor
     * @return The logarithm corresponding to the inputs
     */
    public static strictfp double log(double number, int base, int n, boolean ceil){
        double logarithm = 0;
        double M = number;
        double shift = 1;
        double baseShift = Math.pow(base, -1);
        int lastDigit;
        for(int i = 0; i < n; i++) {
            lastDigit = length((int) M);
            logarithm += lastDigit * shift;
            shift *= baseShift;
            M = M(M, lastDigit, base);
        }
        lastDigit = length((int) M);
        logarithm += lastDigit * shift;
        if(ceil) logarithm += shift;
        return logarithm;
    }

    public static strictfp double log(double number, int base, int n){
        return log(number, base, n, false);
    }

    public static strictfp double log(double number){
        return log(number, 10, 10);
    }

    private static strictfp double M(double number, int a, int base){
        double POW = Math.pow(base, a);
        return Math.pow(number / POW, base);
    }

    private static int length(int number){
        if(number == 0) return -1;
        int length = 0;
        if (number >= 100000000) {
            length += 8;
            number /= 100000000;
        }
        if (number >= 10000) {
            length += 4;
            number /= 10000;
        }
        if (number >= 100) {
            length += 2;
            number /= 100;
        }
        if (number >= 10) length += 1;
        return length;
    }

}
