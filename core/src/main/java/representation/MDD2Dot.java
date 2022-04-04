package representation;

import dd.mdd.MDD;
import dd.mdd.components.Node;
import structures.generics.MapOf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * <b>MDD2Dot</b><br>
 * This class is used to convert an MDD to a .dot file that can be used to draw the MDD.
 */
public class MDD2Dot {

    private final static String path = "dot/", extension = ".dot";

    /**
     * Create the directory dot/ if it does not already exist
     */
    private static void mkdir(){
        File file = new File(path);
        if(!file.exists()) file.mkdir();
    }

    /**
     * Convert the given MDD to a .dot file with given name
     * @param mdd The MDD to convert
     * @param filename The name of the .dot file
     * @return The newly created .dot file
     * @throws IOException Error during the creation of the file
     */
    public static File convert(MDD mdd, String filename, HashMap<Integer, String> labelToString) throws IOException {
        int node_number = 0;
        HashMap<Node, Integer> names = new HashMap<>();
        mkdir();
        File file = new File(path+filename+extension);
        PrintWriter graph = new PrintWriter(new FileWriter(file));

        graph.println("digraph {");
        graph.println("\tgraph [nodesep=\"0.3\", ranksep=\"0.3\",fontsize=12]");
        graph.println("\tnode [shape=circle, fontsize=12]");
        graph.println("\tedge [arrowsize=0.6]");
        graph.println();

        for(int i = 0; i < mdd.size(); i++){
            for(Node node : mdd.getLayer(i)) {
                names.put(node, node_number);
                graph.print("\t");
                graph.print(node_number);
                graph.print(" [label=\"");
                if(node == mdd.getRoot()) graph.print("root");
                else if(node == mdd.getTt()) graph.print("tt");
                else graph.print(node_number);
                node_number++;
                graph.println("\"]");
            }
        }

        graph.println();

        int source;
        for(int i = 0; i < mdd.size(); i++){
            for(Node node : mdd.getLayer(i)) {
                source = names.get(node);
                for(int label : node.getChildren()) {
                    graph.print("\t");
                    graph.print(source);
                    graph.print("->");
                    graph.print(names.get(node.getChild(label)));
                    graph.print(" [label=\"");
                    if(labelToString != null) graph.print(labelToString.get(label));
                    else graph.println(label);
                    graph.println("\"]");
                }
            }
        }

        graph.print("}");
        graph.close();
        return file;
    }

    public static File convert(MDD mdd, String filename) throws IOException{
        return convert(mdd, filename, null);
    }

    /**
     * Write the header of the .dot graph file into the given filewriter
     * @param graph The filewriter
     * @throws IOException Error during the writing
     */
    private static void header(FileWriter graph) throws IOException {
        graph.write("digraph {\n");
        graph.write("\tgraph [nodesep=\"0.3\", ranksep=\"0.3\",fontsize=12]\n");
        graph.write("\tnode [shape=circle, fontsize=12]\n");
        graph.write("\tedge [arrowsize=0.6]\n");
    }

}
