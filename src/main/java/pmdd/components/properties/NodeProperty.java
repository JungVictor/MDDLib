package pmdd.components.properties;

import memory.MemoryObject;
import memory.MemoryPool;
import structures.generics.MapOf;


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
            CONSECUTIVE = "consecutive",
            AMONG = "among";


    // MemoryObject variables
    private final MemoryPool<NodeProperty> pool;
    private int ID = -1;
    //

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

    /**
     * Check if the information held by the property is degenerate IF we consider the transition with given label
     * specifying if it is for the final layer
     * You need to create the property in a way that it acts as a "checker".
     * @param v Value of the label
     * @return true if the information is degenerate after the transition, false otherwise.
     */
    public boolean isDegenerate(int v, boolean finalLayer){
        return isDegenerate(v);
    }


    //**************************************//
    //             RAW RESULTS              //
    //**************************************//
    // getData

    /**
     * Return the raw data contained in the NodeProperty as a MapOf.
     * The map can be anything.
     * @return Raw data held by the NodeProperty
     */
    public MapOf getData(){ return null; }


    //**************************************//
    //           HASH FUNCTIONS             //
    //**************************************//
    // hash

    /**
     * Hash function on the current NodeProperty.
     * Used to compare properties.
     * @return hash code of the object
     */
    public int hash(){
        return 0;
    }

    /**
     * Hash function of the NodeProperty obtained by doing the transition with the given value.
     * Used to compare properties.
     * @param value Value of the transition
     * @return hash code of the object
     */
    public int hash(int value){
        return 0;
    }


    public String hashstr(int value) {
        return "";
    }

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