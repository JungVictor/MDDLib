package structures.integers;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Arrays;

/**
 * <b>The class representing a matrix of int</b><br>
 * This prevent the creation of many arrays of int as it can be free and reused.
 */
public class MatrixOfInt implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<MatrixOfInt> pool;
    private int ID = -1;
    //

    private int[] matrix;
    private int height, length;


    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    public MatrixOfInt(MemoryPool<MatrixOfInt> pool, int height, int length){
        this.pool = pool;
        matrix = new int[height*length];
        this.height = height;
        this.length = length;
    }


    //**************************************//
    //         SPECIAL FUNCTIONS            //
    //**************************************//
    // toString

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("\n");
        for(int i = 0; i < height; i++){
            builder.append("\t");
            builder.append(i);
            builder.append(" : [");
            for(int j = 0; j < length; j++) {
                builder.append(get(i, j));
                if(j < length - 1) builder.append(", ");
            }
            builder.append("]\n");
        }
        builder.append("]");
        return builder.toString();
    }


    //**************************************//
    //          MATRIX MANAGEMENT           //
    //**************************************//

    /**
     * Set the size of the matrix
     * @param height Height of the matrix (number of rows)
     * @param length Length of the matrix (number of columns)
     */
    public void setSize(int height, int length){
        int size = height * length;
        if(size > matrix.length) matrix = new int[size];
        this.length = length;
        this.height = height;
        prepare();
    }

    /**
     * Get the value of the element at the specified position
     * @param row Index of the rox
     * @param column Index of the column
     * @return the value of the element at the specified position
     */
    public int get(int row, int column){
        return matrix[row * length + column];
    }

    /**
     * Set the value of the element at the specified position
     * @param row Index of the rox
     * @param column Index of the column
     * @param value Value of the element
     */
    public void set(int row, int column, int value){
        matrix[row * length + column] = value;
    }

    /**
     * Set the values of the specified row
     * @param row Index of the row
     * @param values Array of values
     */
    public void set(int row, int[] values){
        for(int i = 0, start = row * length; i < values.length; i++) matrix[start + i] = values[i];
    }

    /**
     * Increase the value at the specified position by the given amount
     * @param row Index of the row
     * @param column Index of the column
     * @param value Amount to add
     */
    public void incr(int row, int column, int value){
        matrix[row * length + column] += value;
    }

    /**
     * Get the height of the matrix (number of rows)
     * @return the height of the matrix (number of rows)
     */
    public int getHeight(){
        return height;
    }

    /**
     * Get the length of the matrix (number of columns)
     * @return the length of the matrix (number of columns)
     */
    public int getLength(){
        return length;
    }


    //**************************************//
    //           MEMORY FUNCTIONS           //
    //**************************************//
    // Implementation of MemoryObject interface

    @Override
    public void prepare() {
        Arrays.fill(matrix, 0);
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void free() {
        pool.free(this, ID);
    }
}
