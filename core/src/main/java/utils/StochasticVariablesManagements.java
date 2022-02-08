package utils;

import structures.StochasticVariable;

import java.io.*;

public class StochasticVariablesManagements {

    public final static String directoryPath = "data/StochasticVariables/";

    public static StochasticVariable[] getStochasticVariables(String filename){
        return getStochasticVariables(directoryPath, filename);
    }

    public static void saveStochasticVariables(String filename, StochasticVariable[] stochasticVariables, int precision){
        saveStochasticVariables(directoryPath, filename, stochasticVariables, precision);
    }

    public static StochasticVariable[] getStochasticVariables(String directoryPath, String fileName){
        StochasticVariable[] stochasticVariables = new StochasticVariable[0]; //
        try{
            File file = new File(directoryPath + fileName);
            FileReader fr = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);
            int size = Integer.parseInt(br.readLine());
            int precision = Integer.parseInt(br.readLine());
            String stochasticVariable;

            stochasticVariables = new StochasticVariable[size];

            String delims = "[\\[\\], \t]+";
            String[] numbers;
            StochasticVariable tmp;

            for (int i = 0; i < stochasticVariables.length; i++) {
                stochasticVariable = br.readLine();
                numbers = stochasticVariable.split(delims);
                tmp = StochasticVariable.create(precision);
                tmp.setQuantity(Long.parseLong(numbers[1]), Long.parseLong(numbers[2]));
                tmp.setValue(Long.parseLong(numbers[3]), Long.parseLong(numbers[4]));
                stochasticVariables[i] = tmp;
            }
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return stochasticVariables;
    }

    public static void saveStochasticVariables(String directoryPath, String fileName, StochasticVariable[] stochasticVariables, int precision){
        try {
            File file = new File(directoryPath + fileName);

            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();

            if (!file.exists()) file.createNewFile();

            StringBuilder builder = new StringBuilder();
            builder.append(stochasticVariables.length);
            builder.append("\n");
            builder.append(precision);
            builder.append("\n");
            for(int i = 0; i < stochasticVariables.length; i++){
                builder.append(stochasticVariables[i].getQuantity());
                builder.append(", ");
                builder.append(stochasticVariables[i].getValue());
                builder.append("\n");
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(builder.toString());
            bw.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
