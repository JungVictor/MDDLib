package utils;

import mdd.MDD;
import mdd.components.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MDDReader {

    private static int n;

    private static void saveLayer(MDD mdd, int layer, PrintWriter writer, HashMap<Node, String> currentBinding, HashMap<Node, String> nextBinding){
        for(Node node : mdd.getLayer(layer)){
            HashMap<Node, ArrayList<Integer>> reduced = new HashMap<>();
            for(int arc : node.getChildren()) {
                if(!reduced.containsKey(node.getChild(arc))) reduced.put(node.getChild(arc), new ArrayList<>());
                reduced.get(node.getChild(arc)).add(arc);
            }
            if(reduced.size() == 0) continue;
            writer.write(currentBinding.get(node));
            writer.write(":");
            int cpt = 0;
            for(Node next : reduced.keySet()) {
                if(cpt++ != 0) writer.write("|");
                if(!nextBinding.containsKey(next)) nextBinding.put(next, "N"+(n++));
                ArrayList<Integer> labels = reduced.get(next);
                for(int i = 0; i < labels.size() - 1; i++)  writer.write(labels.get(i)+",");
                writer.write(labels.get(labels.size()-1)+"-"+nextBinding.get(next));
            }
            writer.write(";");
        }
    }

    private static void loadLayer(MDD mdd, int i, String layer, HashMap<String, Node> currentBinding, HashMap<String, Node> nextBinding){
        if(layer.isEmpty()) return;
        String[] nodes = layer.split(";");
        for(String node : nodes){
            String[] split = node.split(":");
            Node current = currentBinding.get(split[0]);
            String[] arcs = split[1].split("\\|");
            for(String a : arcs) {
                String[] arc = a.split("-");
                String[] labels = arc[0].split(",");
                if (!nextBinding.containsKey(arc[1])) nextBinding.put(arc[1], mdd.Node());
                Node next = nextBinding.get(arc[1]);
                for (String label : labels) mdd.addArcAndNode(current, Integer.parseInt(label), next, i + 1);
            }
        }
    }

    public static MDD load(MDD mdd, String file){
        int line = 0;
        mdd.setSize(1);
        HashMap<String, Node> currentBinding = new HashMap<>(), nextBinding = new HashMap<>(), tmp;
        currentBinding.put("root", mdd.getRoot());
        try {
            Scanner reader = new Scanner(new File(file));
            while (reader.hasNextLine()){
                mdd.setSize(mdd.size()+1);
                loadLayer(mdd, line++, reader.nextLine(), currentBinding, nextBinding);
                Logger.out.information("\rLOADING LAYER " + line);
                tmp = currentBinding;
                currentBinding = nextBinding;
                nextBinding = tmp;
                nextBinding.clear();
            }
            mdd.reduce();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return mdd;
    }

    public static boolean save(MDD mdd, String file){
        n = 0;
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file+".mdd"));
            HashMap<Node, String> currentBinding = new HashMap<>(), nextBinding = new HashMap<>(), tmp;
            currentBinding.put(mdd.getRoot(), "root");
            for(int i = 0; i < mdd.size(); i++) {
                saveLayer(mdd, i, writer, currentBinding, nextBinding);
                tmp = currentBinding;
                currentBinding = nextBinding;
                nextBinding = tmp;
                nextBinding.clear();
                writer.write(System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
