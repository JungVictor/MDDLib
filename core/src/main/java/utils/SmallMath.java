package utils;

public class SmallMath {

    private static double negBase;
    // base, base ^ 2, base ^ 4, base ^ 8, base ^ 16;
    private static final int[] ex = {0, 2, 4, 8, 16, 32};
    private static final double[] pow = new double[ex.length];

    private SmallMath(){}

    /**
     * Convert the given integer to an array of bytes
     * @param array The array to fill
     * @param data The integer
     * @return The array of bytes corresponding to the given integer
     */
    public static byte[] intToBytes(byte[] array, final int data){
        for(int i = array.length - 1, offset = 0; i >= 0; i--, offset += 8){
            array[i] = (byte)((data >> offset) & 0xff);
        }
        return array;
    }

    /**
     * Convert an array of bytes to its integer value
     * @param array The array of bytes to convert
     * @return The integer corresponding to the given array
     */
    public static int bytesToInt(byte[] array){
        int n = 0;
        for(int i = array.length - 1, offset = 0; i >= 0; i--, offset+=8) n |= (array[i] & 0xFF) << offset;
        return n;
    }

    /**
     * Computes the number of bytes needed to represent the given integer
     * @param value
     * @return The number of bytes needed to represent the given integer
     */
    public static byte nBytes(int value){
        byte b = 0;
        while(value > 0) {
            value >>= 8;
            b++;
        }
        return b;
    }

    public static double multiplyCeil(double x, double y, double divisor){
        return Math.ceil((x * y) / divisor);
    }

    public static double multiplyFloor(double x, double y, double divisor){
        return Math.floor((x * y) / divisor);
    }

    /**
     * Compute the logarithm as a long
     * @param number The number
     * @param precision The precision of the number
     * @param base The base of the logarithm
     * @param n The precision of the logarithm
     * @param ceil True if we compute the ceil of the logarithm, false otherwise
     * @return The logarithm as a long of the given number, computed to the given base with given precision.
     */
    public static long log(long number, int precision, int base, int n, boolean ceil){
        if(number == Math.pow(10, precision)) return 0;
        long logarithm = 0;
        double num = number / Math.pow(10, precision);
        int lastDigit;
        int firstDigit = 0;
        cachePow(base);

        while (num < negBase) {
            firstDigit--;
            num *= base;
        }

        double M = num;
        for(int i = 0; i < n; i++) {
            lastDigit = length(Math.floor(M), base);
            if(i == 0) logarithm = logarithm * 10 + lastDigit + firstDigit;
            else logarithm = logarithm * 10 + lastDigit;
            M = M(M, lastDigit, base);
        }

        lastDigit = length(M, base);
        logarithm = logarithm * 10 + lastDigit;
        if(ceil) logarithm += 1;
        return logarithm;
    }

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
        double shift = 1;
        double baseShift = Math.pow(10, -1);
        int lastDigit = 0;
        cachePow(base);

        while (number < negBase) {
            lastDigit--;
            number *= base;
        }

        logarithm += lastDigit;

        double M = number;
        for(int i = 0; i < n; i++) {
            lastDigit = length(Math.floor(M), base);
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
        for(int i = 0; i < ex.length; i++) pow[i] = Math.pow(base, ex[i]);
        negBase = Math.pow(base, -1);
    }

    private static int length(double number, int base){
        if(number <= 0) return -1;
        int length = 0;
        for(int i = ex.length - 1; i >= 0; i--) {
            // base ^ ex[i]
            if (number >= pow[i]) {
                length += ex[i];
                number /= pow[i];
            }
        }
        if (number >= base) length += 1;
        return length;
    }

}
