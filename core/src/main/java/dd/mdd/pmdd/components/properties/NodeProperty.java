package dd.mdd.pmdd.components.properties;

import memory.Allocable;
import structures.generics.MapOf;


/**
 * <b>NodeProperty</b><br>
 * This is the abstract class to represent node properties.
 * Main functions are : createProperty, mergeWithProperty and getResult.
 * <ul>
 *    <li> <b>createProperty</b> allows to create a new property from an existing property. Basically, it is used
 *      to create a new property from a transition.
 *
 *    <li> <b>mergeWithProperty</b> allows to merge an existing property with a property from a transition WITHOUT creating the
 *      property from transition. This allows to combine results without creating new objects.
 *
 *    <li> <b>getResult</b> allows to retrieve the final result held by the node. Typically, it is the property itself ("this"),
 *      but sometimes it is something else (for instance : see sequences).
 *</ul>
 *
 * Other functions are for management and storage purposes.
 *
 */
public abstract class NodeProperty implements Allocable {

    // Names of the constraints
    public static final String
            SUM = "sum",
            ALLDIFF = "allDiff",
            GCC = "gcc",
            SEQ = "sequence",
            CONSECUTIVE = "consecutive",
            AMONG = "among";

    private int allocatedIndex;

    // Name of the property
    private String name;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Constructor. Initialise the index in the allocator.
     * @param allocatedIndex Index of the object in the allocator
     */
    public NodeProperty(int allocatedIndex) {
        this.allocatedIndex = allocatedIndex;
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
    public boolean isValid(){
        return false;
    }

    /**
     * Check if the information held by the property is degenerate IF we consider the transition with given label.
     * You need to create the property in a way that it acts as a "checker".
     * @param v Value of the label
     * @return true if the information is degenerate after the transition, false otherwise.
     */
    public boolean isValid(int v){
        return false;
    }

    /**
     * Check if the information held by the property is degenerate IF we consider the transition with given label
     * specifying if it is for the final layer
     * You need to create the property in a way that it acts as a "checker".
     * @param v Value of the label
     * @param layer Layer of the node holding the property
     * @param size Size of the MDD
     * @return true if the information is degenerate after the transition, false otherwise.
     */
    public boolean isValid(int v, int layer, int size){
        return isValid(v);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free(){

    }

    public void prepare(){}

}