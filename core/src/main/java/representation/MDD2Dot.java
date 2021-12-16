package representation;

import mdd.MDD;
import mdd.components.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class MDD2Dot {

    private final static String path = "dot/", extension = ".dot";

    private static void mkdir(){
        File file = new File(path);
        if(!file.exists()) file.mkdir();
    }

    public static File convert(MDD mdd, String filename) throws IOException {
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
                    graph.print(label);
                    graph.println("\"]");
                }
            }
        }

        graph.print("}");
        graph.close();
        return file;
    }

    private static void header(FileWriter graph) throws IOException {
        graph.write("digraph {\n");
        graph.write("\tgraph [nodesep=\"0.3\", ranksep=\"0.3\",fontsize=12]\n");
        graph.write("\tnode [shape=circle, fontsize=12]\n");
        graph.write("\tedge [arrowsize=0.6]\n");
    }

}
