package utils.io.reader;

import utils.SmallMath;

import java.io.FileInputStream;
import java.io.IOException;

public class MDDFileReader {

    private final FileInputStream file;
    private final byte[] DATA;
    private final int capacity;
    private int position;

    public MDDFileReader(FileInputStream file, int bufferSize) throws IOException {
        this.capacity = bufferSize;
        this.file = file;
        DATA = new byte[capacity];
        fill(0);
    }

    /**
     * Fill the DATA by reading the file
     * @param offset Offset of the array
     * @throws IOException
     */
    private void fill(int offset) throws IOException {
        file.read(DATA, offset, capacity-offset);
    }

    /**
     * Fill the array b by consuming data from the file.
     * @param b The array to fill
     * @return The filled array
     */
    public byte[] read(byte[] b) throws IOException {
        // If we are trying to read more than we can
        if(position + b.length >= capacity) {
            int offset = capacity - position;
            for(int i = 0; i < offset; i++) DATA[i] = DATA[position+i];
            fill(offset);
            position = 0;
        }
        for(int i = 0; i < b.length; i++) b[i] = DATA[position+i];
        position += b.length;
        return b;
    }

    public byte nextByte() throws IOException {
        if(position + 1 >= capacity) {
            fill(0);
            position = 0;
        }
        return DATA[position++];
    }

    public void close() throws IOException {
        this.file.close();
    }

}
