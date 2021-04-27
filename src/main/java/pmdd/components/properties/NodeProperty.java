package pmdd.components.properties;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;


/**
 * NODE PROPERTY
 * This is the abstract class to represent node properties.
 * Main functions are : createProperty, mergeWithProperty and getResult.
 *
 *    - createProperty allows to create a new property from an existing property. Basically, it is used
 *      to create a new property from a transition.
 *
 *    - mergeWithProperty allows to merge an existing property with a property from a transition WITHOUT creating the
 *      property from transition. This allows to combine results without creating new objects.
 *
 *    - getResult allows to retrieve the final result held by the node. Typically, it is the property itself ("this"),
 *      but sometimes it is something else (for instance : see sequences).
 *
 *
 * Other functions are for management and storage purposes.
 *
 */
public abstract class NodeProperty implements MemoryObject {

    // Names of the constraints
    public static final String
            SUM = "sum",
            ALLDIFF = "allDiff",
            GCC = "gcc",
            SEQ = "sequence",
            CONSECUTIVE = "consecutive";

    // What kind of data the NodeProperty holds

    protected enum DataType {
        UNDEFINED, SINGLE, ARRAY, ARRAY2
    }


    // MemoryObject variables
    private final MemoryPool<NodeProperty> pool;
    private int ID = -1;
    //

    // Type of data
    private DataType type = DataType.UNDEFINED;
    // Name of the property
    private String name;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public NodeProperty(MemoryPool<NodeProperty> pool) {
        this.pool = pool;
    }

    /**
     * Set the name of the property.
     * Used to store properties in a map.
     * @param name Name of the property
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Get the name of the property
     * @return Name of the property
     */
    public String getName(){
        return this.name;
    }


    //**************************************//
    //         PROPERTY PROPAGATION         //
    //**************************************//
    // createProperty   || mergeWithProperty
    // getResult

    /**
     * Create a new property from an existing property and a label
     * @param value Value of the label
     * @return A new property corresponding to the transition from this property with given value
     */
    public NodeProperty createProperty(int value){return null;}

    /**
     * Merge the given property with the transition from this property with given label.
     * Does not create a new property.
     * @param value Value of the label
     * @param property Property to update
     */
    public void mergeWithProperty(int value, NodeProperty property){}

    /**
     * Merge the given property with this property.
     * Does not create a new property.
     * @param property Property to update
     */
    public void mergeWithProperty(NodeProperty property){}

    /**
     * Get the result of the propagation of the property.
     * Most of time, returns the current property.
     * @return The NodeProperty holding the real result
     */
    public NodeProperty getResult(){
        return this;
    }


    //**************************************//
    //               CHECKERS               //
    //**************************************//
    // isDegenerate

    /**
     * Check if the information held by the property is degenerate.
     * You need to create the property in a way that it acts as a "checker".
     * @return true if the information is degenerate, false otherwise.
     */
    public boolean isDegenerate(){
        return false;
    }

    /**
     * Check if the information held by the property is degenerate IF we consider the transition with given label.
     * You need to create the property in a way that it acts as a "checker".
     * @param v Value of the label
     * @return true if the information is degenerate after the transition, false otherwise.
     */
    public boolean isDegenerate(int v){
        return false;
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getType          || setType
    // getSingle        || getArray
    // getArray2

    /**
     * Get the type of data held by the property
     * Currently, it is the dimension of the result (0 = single value, 1 = array, 2 = matrix...)
     * @return Type of data held
     */
    public DataType getType(){
        return type;
    }

    /**
     * Set the type of data held by the property.
     * Currently, it is the dimension of the result (0 = single value, 1 = array, 2 = matrix...)
     * @param type Type of data held
     */
    protected void setType(DataType type){
        this.type = type;
    }

    /**
     * Return the data held by the property as a single value
     * @return A single value holding result
     */
    public int getSingle(){
        return -1;
    }

    /**
     * Return the data held by the property as an array
     * @return An array holding result
     */
    public ArrayOfInt getArray(){
        return null;
    }

    /**
     * Return the data held by the property as a 2D-array
     * @return A two dimensional array holding result
     */
    public MatrixOfInt getArray2(){ return null; }

    public MapOf getData(){return null;}


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        prepare();
        this.pool.free(this, ID);
    }

    @Override
    public void prepare(){}

}