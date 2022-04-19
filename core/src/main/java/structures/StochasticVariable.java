package structures;

import dd.operations.Stochastic;
import memory.Allocable;
import memory.AllocatorOf;
import memory.Memory;
import structures.tuples.TupleOfLong;

public class StochasticVariable implements Allocable {

    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);
    // Allocated index
    private final int allocatedIndex;
    //

    private TupleOfLong quantity;
    private TupleOfLong value;
    private int precision = -1;


    //**************************************//
    //              CONSTRUCTOR             //
    //**************************************//

    /**
     * Get the allocator. Thread safe.
     * @return The allocator.
     */
    private static Allocator allocator(){
        return localStorage.get();
    }


    private StochasticVariable(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public static StochasticVariable create(){
        return create(0,0,0,0);
    }

    public static StochasticVariable create(StochasticVariable var){
        StochasticVariable X = create(var.getQuantity(), var.getValue());
        X.precision = var.precision;
        return X;
    }

    public static StochasticVariable create(int precision){
        long one = (long) Math.pow(10, precision);
        StochasticVariable X = create(0, one, 0, one);
        X.precision = precision;
        return X;
    }

    public static StochasticVariable create(TupleOfLong quantity, TupleOfLong value){
        return create(quantity.getFirst(), quantity.getSecond(), value.getFirst(), value.getSecond());
    }

    public static StochasticVariable create(long qinf, long qsup, long vinf, long vsup){
        StochasticVariable var = allocator().allocate();
        var.prepare();
        var.quantity.set(qinf, qsup);
        var.value.set(vinf, vsup);
        return var;
    }

    //**************************************//
    //               SPECIALS               //
    //**************************************//

    @Override
    public String toString(){
        return "Q: ["+getMinQuantity()+", " + getMaxQuantity() + "] - V: ["+getMinValue()+", " + getMaxValue() + "]";
    }

    /**
     * Get the precision of the variable
     * @return The precision of the variable (-1) if not specified
     */
    public int getPrecision(){
        return precision;
    }

    /**
     * Set the precision of the variable
     * @param precision The precision of the variable
     */
    public void setPrecision(int precision){
        this.precision = precision;
    }

    //**************************************//
    //              OPERATIONS              //
    //**************************************//

    /**
     * Return the maximum amount of quantity that can be swapped using the max value of the variables.
     * @param x The variable from which we take some quantity
     * @param minValue The minimal value to obtain by swapping quantities
     * @param quantity The quantity that we can swap
     * @param precision The precision of the variables
     * @return The maximum amount of quantity that can be swapped from x to this
     */
    public long maxSwappingQuantityMax(StochasticVariable x, long minValue, long quantity, int precision){
        long one = (long) Math.pow(10, precision);
        long currentValue = (quantity * x.getMaxValue());
        if(getMaxValue() == x.getMaxValue()) return quantity;
        long res = (long) Math.ceil((currentValue - minValue * 1.0) / (x.getMaxValue() - getMaxValue()));
        if(res <= 0){
            if(quantity * getMaxValue() >= minValue) return quantity;
            // Error : -1 ?
            return 0;
        }
        if(res > quantity) return quantity;
        return res;
    }

    /**
     * Return the maximum amount of quantity that can be swapped using the min value of the variables.
     * @param x The variable from which we take some quantity
     * @param minValue The minimal value to obtain by swapping quantities
     * @param quantity The quantity that we can swap
     * @param precision The precision of the variables
     * @return The maximum amount of quantity that can be swapped from x to this
     */
    public long maxSwappingQuantityMin(StochasticVariable x, long minValue, long quantity, int precision){
        long one = (long) Math.pow(10, precision);
        long currentValue = (quantity * x.getMinValue());
        if(getMinValue() == x.getMinValue()) return quantity;
        long res = (long) Math.ceil((currentValue - minValue * one * 1.0) / (x.getMinValue() - getMinValue()));
        if(res <= 0){
            if(quantity * getMinValue() >= minValue * one) return quantity;
            // Error : -1 ?
            return 0;
        }
        if(res >= quantity) {
            if(quantity * getMinValue() >= minValue * one) return quantity;
            // Error : -1 ?
            return 0;
        }
        return res;
    }

    public long minSwappingQuantityMax(StochasticVariable x, long maxValue, long quantity, int precision){
        long one = (long) Math.pow(10, precision);
        long currentValue = (quantity * x.getMaxValue());
        if(getMaxValue() == x.getMaxValue()) return quantity;
        long res = (currentValue - maxValue * one) / (x.getMaxValue() - getMaxValue());
        if(res <= 0){
            if(quantity * getMaxValue() <= maxValue * one) return quantity;
            // Error : -1 ?
            return 0;
        }
        if(res >= quantity) {
            if(quantity * getMaxValue() <= maxValue * one) return quantity;
            // Error : -1 ?
            return 0;
        }
        return res;
    }

    /**
     * Check if the swap is worth between this variable and the given variable xi.
     * @param xi The variable to swap with
     * @param K The threshold
     * @param V The current value
     * @param p The quantity to swap
     * @param one The number one in the appropriate precision
     * @return True if the swap is worth it, false otherwise
     */
    public boolean worthSwappingWith(StochasticVariable xi, long K, long V, long p, long one) {
        return V + (xi.getMaxValue() * p) / one > K;
    }

    //**************************************//
    //              QUANTITY                //
    //**************************************//

    public void setQuantity(long min, long max){
        quantity.set(min, max);
    }

    public void setMinQuantity(long min){
        quantity.setFirst(min);
    }

    public void setMaxQuantity(long max){
        quantity.setSecond(max);
    }

    public TupleOfLong getQuantity(){
        return quantity;
    }

    public long getMinQuantity(){
        return quantity.getFirst();
    }

    public long getMaxQuantity(){
        return quantity.getSecond();
    }

    //**************************************//
    //                VALUE                 //
    //**************************************//


    public void setValue(long min, long max){
        value.set(min, max);
    }

    public void setMinValue(long min){
        value.setFirst(min);
    }

    public void setMaxValue(long max){
        value.setSecond(max);
    }

    public TupleOfLong getValue(){
        return value;
    }

    public long getMinValue(){
        return value.getFirst();
    }

    public long getMaxValue(){
        return value.getSecond();
    }


    //**************************************//
    //                MEMORY                //
    //**************************************//

    private void prepare(){
        quantity = TupleOfLong.create();
        value = TupleOfLong.create();
    }

    @Override
    public int allocatedIndex() {
        return allocatedIndex;
    }

    @Override
    public void free() {
        Memory.free(quantity);
        Memory.free(value);
    }


    /**
     * <b>The allocator that is in charge of the StochasticVariable type.</b><br>
     * When not specified, the allocator has an initial capacity of 16. This number is arbitrary, and
     * can be change if needed (might improve/decrease performance and/or memory usage).
     */
    static final class Allocator extends AllocatorOf<StochasticVariable> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            this(10);
        }

        @Override
        protected StochasticVariable[] arrayCreation(int capacity) {
            return new StochasticVariable[capacity];
        }

        @Override
        protected StochasticVariable createObject(int index) {
            return new StochasticVariable(index);
        }
    }
}
