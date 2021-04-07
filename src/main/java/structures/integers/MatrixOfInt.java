package structures.integers;

import memory.MemoryObject;
import memory.MemoryPool;

import java.util.Arrays;

public class MatrixOfInt implements MemoryObject {

    // MemoryObject variables
    private final MemoryPool<MatrixOfInt> pool;
    private int ID = -1;
    //

    private int[] matrix;
    private int height, length;

    public MatrixOfInt(MemoryPool<MatrixOfInt> pool, int height, int length){
        this.pool = pool;
        matrix = new int[height*length];
        this.height = height;
        this.length = length;
    }

    public void setSize(int height, int length){
        int size = height * length;
        if(size > matrix.length) matrix = new int[size];
        this.length = length;
        this.height = height;
        prepare();
    }

    public int get(int row, int column){
        return matrix[row * length + column];
    }

    public void set(int row, int column, int value){
        matrix[row * length + column] = value;
    }

    public void set(int row, int[] values){
        for(int i = 0, start = row * length; i < values.length; i++) matrix[start + i] = values[i];
    }

    public void incr(int row, int column, int value){
        matrix[row * length + column] += value;
    }

    public int getHeight(){
        return height;
    }

    public int getLength(){
        return length;
    }

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
            if(i == height - 1) builder.append("],\n");
            else builder.append("]\n");
        }
        builder.append("]");
        return builder.toString();
    }

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
