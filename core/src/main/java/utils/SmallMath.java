package utils;

public class SmallMath {

    // base, base ^ 2, base ^ 4, base ^ 8;
    private static double[] pow = new double[4];

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
        if(number == 1) return 0;
        double logarithm = 0;
        double M = number;
        double shift = 1;
        double baseShift = Math.pow(10, -1);
        int lastDigit;
        cachePow(base);
        for(int i = 0; i < n; i++) {
            lastDigit = length((int) M, base);
            logarithm += lastDigit * shift;
            shift *= baseShift;
            M = M(M, lastDigit, base);
        }
        lastDigit = length(M, base);
        logarithm += lastDigit * shift;
        if(ceil) logarithm += shift;
        return logarithm;
    }

    public static strictfp double log(double number, int base, int n){
        return log(number, base, n, true);
    }

    public static strictfp double log(double number){
        return log(number, 10, 10);
    }

    private static strictfp double M(double number, int a, int base){
        double POW = Math.pow(base, a);
        return Math.pow(number / POW, 10);
    }

    private static void cachePow(int base) {
        if(pow[0] == base) return;
        pow[0] = base;
        pow[1] = Math.pow(base, 2);
        pow[2] = Math.pow(base, 4);
        pow[3] = Math.pow(base, 8);
    }

    private static int length(double number, int base){
        if(number <= 0) return -1;
        int length = 0;
        // base ^ 8
        if (number >= pow[3]) {
            length += 8;
            number /= pow[3];
        }
        // base ^ 4
        if (number >= pow[2]) {
            length += 4;
            number /= pow[2];
        }
        // base ^ 2
        if (number >= pow[1]) {
            length += 2;
            number /= pow[1];
        }
        if (number >= base) length += 1;
        return length;
    }

}
