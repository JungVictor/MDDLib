package mdd.operations;

import mdd.MDD;
import memory.Memory;

public class Operation {

    private enum Operator {
        UNION, INTERSECTION, DIAMOND, MINUS, INCLUSION;
    }

    public static MDD union(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.UNION);
    }

    public static MDD intersection(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.INTERSECTION);
    }

    public static MDD diamond(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.DIAMOND);
    }

    public static MDD minus(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.MINUS);
    }

    public static MDD negation(MDD mdd){
        MDD universal = Memory.MDD();
        return minus(universal, mdd);
    }

    public static boolean inclusion(MDD mdd1, MDD mdd2){
        return perform(mdd1, mdd2, Operator.INCLUSION) != null;
    }

    private static boolean apply(){
        return true;
    }

    private static MDD perform(MDD mdd1, MDD mdd2, Operator op){
        MDD result = Memory.MDD();



        return result;
    }

}
