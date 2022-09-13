package ui;

public class Variable {

    // Order of the variable in the MDD
    private int order = -1;
    // Domain of the variable
    private int[] domain;

    // ---------------------
    // Constructors
    // ---------------------

    public Variable(){}

    /**
     * Create a Variable with specified order
     * @param order Order of the variable (in the MDD)
     */
    public Variable(int order){
        this.order = order;
    }

    /**
     * Create a Variable with specified order and domain
     * @param order Order of the variable (in the MDD)
     * @param domain Domain of the variable
     */
    public Variable(int order, int... domain){
        this.order = order;
        this.domain = domain;
    }


    // ---------------------
    // Domain management
    // ---------------------

    /**
     * Set the domain of the variable to the given one
     * @param domain New domain of the variable
     */
    public void setDomain(int[] domain){
        this.domain = domain;
    }

    /**
     * Set the domain of the variable.
     * The domain is created [start, start + step...] until reaching max value stop.
     * @param start First value of the domain
     * @param stop Last value of the domain is at most stop
     * @param step The value of the step while building the domain
     */
    public void setDomain(int start, int stop, int step){
        int[] domain = new int[(stop-start)/step];
        int i = 0;
        for(int v = start; v <= stop; v+=step) domain[i++] = v;
        this.domain = domain;
    }

    /**
     * Set the domain of the variable.
     * The domain is [start, stop], with a step of 1.
     * @param start First value of the domain
     * @param stop Last value of the domain
     */
    public void setDomain(int start, int stop){
        setDomain(start, stop, 1);
    }


    // ---------------------
    // Order
    // ---------------------

    /**
     * Get the order of the variable
     * @return The order of the variable
     */
    public int getOrder(){
        return this.order;
    }

    /**
     * Set the order of the variable
     * @param order New order of the variable
     */
    public void setOrder(int order){
        this.order = order;
    }

    public int[] getDomain() {
        return domain;
    }
}
