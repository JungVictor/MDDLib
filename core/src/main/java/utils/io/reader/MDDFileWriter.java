package utils.io.reader;

import java.io.FileOutputStream;
import java.io.IOException;

public class MDDFileWriter {

    private final FileOutputStream file;
    private final byte[] DATA;
    private final int capacity;

    private int position;

    public MDDFileWriter(FileOutputStream file, int bufferSize) throws IOException {
        this.capacity = bufferSize;
        this.file = file;
        DATA = new byte[capacity];
    }

    /**
     * Flush the buffer into the file
     * @throws IOException
     */
    public void flush() throws IOException {
        file.write(DATA, 0, position);
        position = 0;
    }

    /**
     * Fill the array b by consuming data from the file.
     * @param b The array to fill
     * @return The filled array
     */
    public void write(byte[] b) throws IOException {
        // If we are trying to write more than we can, flush the buffer
        if(position + b.length >= capacity) flush();
        for(int i = 0; i < b.length; i++) {
            DATA[position+i] = b[i];
            if(position+i >= capacity) flush();
        }
        position += b.length;
    }

    public void write(byte b) throws IOException {
        if(position + 1 >= capacity) flush();
        DATA[position++] = b;
    }

    public void write(int b) throws IOException {
        if(position + 1 >= capacity) flush();
        DATA[position++] = (byte) b;
    }

    public void close() throws IOException {
        flush();
        this.file.close();
    }

}
