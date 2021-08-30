package confidence.utils;

public strictfp class SpecialOperations {

    public static double multiplyCeil(double x, double y, double divisor){
        return Math.ceil((x * y) / divisor);
    }

    public static double multiplyFloor(double x, double y, double divisor){
        return Math.floor((x * y) / divisor);
    }
}

