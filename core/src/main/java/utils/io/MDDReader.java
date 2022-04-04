package utils.io;

import dd.DecisionDiagram;
import utils.io.reader.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <b>MDDReader</b><br>
 * This class is used to save and load MDD onto/from a .mdd file.<br>
 * You can write the DD in a bottom-up fashion (describing a node by its in-going arcs) or in
 * a top-down fashion (describing a node by its out-going arcs).<br>
 * Best mode may vary depending on the structure of the DD. <br>
 * <br>
 * <b>LEGACY CLASS : MDDReader_LEGACY</b> [LOAD USING THE LEGACY AND SAVE USING THE NEW CLASS TO CONVERT !]
 */
public abstract class MDDReader {

    public static final byte NODE = 0, VALUE = 1, PARENT_NUMBER = 2, VALUE_NUMBER = 3, SIZE = 4;
    public static final byte BOTTOM_UP = 0, TOP_DOWN = 1;

    private static final DDReaderBottomUp readerBottomUp = new DDReaderBottomUp();
    private static final DDReaderTopDown readerTopDown = new DDReaderTopDown();
    private static DDReaderAbstractClass reader = readerBottomUp;

    public static void setMode(byte MODE){
        if(MODE == BOTTOM_UP) reader = readerBottomUp;
        else if(MODE == TOP_DOWN) reader = readerTopDown;
    }

    /**
     * Save a DecisionDiagram as a file, with given filename.
     * @param dd The DecisionDiagram to save
     * @param filename The name of the created file
     * @param bufferSize The size of the buffer
     * @return True if the operation succeed, false otherwise
     */
    public static boolean save(DecisionDiagram dd, String filename, int bufferSize){
        try {
            // Open the file to write
            MDDFileWriter fileWriter = new MDDFileWriter(new FileOutputStream(filename+".mdd"), bufferSize);
            reader.save(dd, fileWriter);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Save a DecisionDiagram as a file, with given filename.
     * @param dd The DecisionDiagram to save
     * @param filename The name of the created file
     * @return True if the operation succeed, false otherwise
     */
    public static boolean save(DecisionDiagram dd, String filename){
        return save(dd, filename, 4096);
    }

    /**
     * Load a DecisionDiagram from a file using a buffer of given capacity. <br>
     * Automatically detect the good loading mode.
     * @param dd The DecisionDiagram used to load the file
     * @param filename The name of the file to load
     * @param bufferSize Size of the buffer
     * @return True if the operation succeed, false otherwise
     */
    public static boolean load(DecisionDiagram dd, String filename, int bufferSize){
        try {
            // Open the file to read
            FileInputStream file = new FileInputStream(filename+".mdd");

            // Set the reader corresponding to the mode used to write the file
            byte[] MODE = file.readNBytes(1);
            MDDFileReader mddFile = new MDDFileReader(file, bufferSize);
            if(MODE[0] == BOTTOM_UP) readerBottomUp.load(dd, mddFile);
            else if(MODE[0] == TOP_DOWN) readerTopDown.load(dd, mddFile);
            else {
                mddFile.close();
                return false;
            }
            mddFile.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Load a DecisionDiagram from a file using a buffer of size 4096 (2^12). <br>
     * Automatically detect the good loading mode.
     * @param dd The DecisionDiagram used to load the file
     * @param filename The name of the file to load
     * @return True if the operation succeed, false otherwise
     */
    public static boolean load(DecisionDiagram dd, String filename){
        return load(dd, filename, 4096);
    }

}
