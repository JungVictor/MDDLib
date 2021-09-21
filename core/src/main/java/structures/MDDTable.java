package structures;

// TODO : Supprimer pour le merge avec Talos, et remplacer par TableOfTuples dans le code !
/* APPELS :
    • public MDD.createFromTable
    • public MDD.createFromSortedTable
    • private MDD.fillTupleValue 
*/
public interface MDDTable {

    /**
     * The size of the table is the number of rows in a matrix representation.
     * @return The size of the Table
     */
    int numberOfTuples();

    /**
     * The size of the tuple is the number of columns in a matrix representation.
     * All tuples are the same size.
     * @return The size of a Tuple
     */
    int tupleSize();

    /**
     * Get the element corresponding to the given position in the table
     * @param row The index of the row
     * @param column The index of the column
     * @return The element at position (row, column) in the table
     */
    int valueOfIndex(int row, int column);

    /**
     * Get the value corresponding to "no value".
     * This is used to store the value corresponding to the "root" when building a MDD from a table
     * @return The "None" value
     */
    default int noneValue(){
        return Integer.MIN_VALUE;
    }


}
