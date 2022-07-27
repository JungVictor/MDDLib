package utils.io.reader;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MDDFileWriter {

    private final RandomAccessFile file;
    private final byte[] DATA;
    private final int capacity;
    private long ptr = 0;
    private int position;

    public MDDFileWriter(RandomAccessFile file, int bufferSize) throws IOException {
        this.capacity = bufferSize;
        this.file = file;
        DATA = new byte[capacity];
    }

    public long getPointer(){
        return ptr+position;
    }

    public void setPointer(long ptr){
        this.ptr = ptr;
        try {
            this.file.seek(ptr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Flush the buffer into the file
     * @throws IOException
     */
    public void flush() throws IOException {
        file.write(DATA, 0, position);
        ptr+=position;
        position = 0;
    }

    /**
     * Fill the array b by consuming data from the file.
     * @param b The array to fill
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

    /**
     * Write instantly the content of the array b in the file
     * @param b The array to write
     */
    public void instantWrite(byte[] b) throws IOException {
        file.write(b, 0, b.length);
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
