package utils.io;

import dd.DecisionDiagram;
import dd.bdd.BDD;
import dd.mdd.MDD;
import dd.mdd.costmdd.CostMDD;
import structures.generics.MapOf;
import utils.Logger;
import utils.io.reader.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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

    public static final String DIRECTORY = "./data/mdds/";

    // Indices of elements in the array
    public static final byte NODE = 0, VALUE = 1, PARENT_NUMBER = 2, VALUE_NUMBER = 3, SIZE = 4, MAX_OUT_DEGREE = 5, COST = 6;
    // Type of representation
    public static final byte BOTTOM_UP = 0, TOP_DOWN = 1;
    // Type of DD
    public static final byte MDD = 0, BDD = 1, COST_MDD = 2;

    private static final DDReaderBottomUp readerBottomUp = new DDReaderBottomUp();
    private static final DDReaderTopDown readerTopDown = new DDReaderTopDown();
    private static final DDReaderBottomUp costReaderBottomUp = new CostDDReaderBottomUp();
    private static final DDReaderTopDown costReaderTopDown = new CostDDReaderTopDown();
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
            MDDFileWriter fileWriter = new MDDFileWriter(new RandomAccessFile(filename, "rw"), bufferSize);
            if(dd instanceof CostMDD) {
                if(reader == readerBottomUp) reader = costReaderBottomUp;
                else reader = costReaderTopDown;
            }
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
            RandomAccessFile file = new RandomAccessFile(filename, "r");

            // Type of DD loaded
            byte[] MODE = new byte[1];

            file.read(MODE, 0, 1);

            byte TYPE = MODE[0];
            if((TYPE == MDD || TYPE == COST_MDD) && !(dd instanceof dd.mdd.MDD)) throw new ClassCastException("Given DD cannot be cast as an MDD!");
            if(TYPE == BDD && !(dd instanceof dd.bdd.BDD)) Logger.out.information("WARNING : Loading a BDD in a MDD.");
            if(TYPE == COST_MDD && !(dd instanceof CostMDD)) Logger.out.information("WARNING : Loading a CostMDD in a MDD.");

            // Set the reader corresponding to the mode used to write the file
            file.read(MODE, 0, 1);
            MDDFileReader mddFile = new MDDFileReader(file, bufferSize);
            if(MODE[0] == BOTTOM_UP) {
                if(TYPE == COST_MDD) costReaderBottomUp.load(dd, mddFile);
                else readerBottomUp.load(dd, mddFile);
            }
            else if(MODE[0] == TOP_DOWN) {
                if(TYPE == COST_MDD) costReaderTopDown.load(dd, mddFile);
                else readerTopDown.load(dd, mddFile);
            }
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



    /**
     * Load a DecisionDiagram from a file using a buffer of given capacity. <br>
     * Automatically detect the good loading mode.
     * @param dd The DecisionDiagram used to load the file
     * @param filename The name of the file to load
     * @param bufferSize Size of the buffer
     * @return True if the operation succeed, false otherwise
     */
    public static boolean loadAndReduce(DecisionDiagram dd, String filename, int bufferSize){
        try {
            // Open the file to read
            RandomAccessFile file = new RandomAccessFile(filename, "r");

            // Type of DD loaded
            byte[] MODE = new byte[1];

            file.read(MODE, 0, 1);

            byte TYPE = MODE[0];
            if((TYPE == MDD || TYPE == COST_MDD) && !(dd instanceof dd.mdd.MDD)) throw new ClassCastException("Given DD cannot be cast as an MDD!");
            if(TYPE == BDD && !(dd instanceof dd.bdd.BDD)) Logger.out.information("WARNING : Loading a BDD in a MDD.");
            if(TYPE == COST_MDD && !(dd instanceof CostMDD)) Logger.out.information("WARNING : Loading a CostMDD in a MDD.");

            // Set the reader corresponding to the mode used to write the file
            file.read(MODE, 0, 1);
            MDDFileReader mddFile = new MDDFileReader(file, bufferSize);
            if(MODE[0] == BOTTOM_UP) {
                Logger.out.information("WARNING : Unsupported operation for Bottom Up ! Switching to Top Down");
            }
            if(TYPE == COST_MDD) Logger.out.information("WARNING : Unsupported operation for Cost MDD !");
            else readerTopDown.loadAndReduce(dd, mddFile);
            mddFile.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
