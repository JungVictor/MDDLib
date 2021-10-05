package utils.carsequencing;

import problems.CarSequencing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSDataParser {

    public static final String directoryPath = "data/carsequencing/";

    /*  FORMAT :
        / comment (ignored)
        # ALL
        # INFORMATIONS
        # PROMPTED ON LOAD
        nCars nOptions nConfigurations
        a/b c/d e/f
        i k opts
        ...

        with a/b = the maximum capacity of the options
             i = the index of the configuration
             k = the number of cars with this configuration
             opts = the binary array of options (1 = has option, 0 = doesn't have option)

        Ex :
        # TEST EXAMPLE
        # -1
        10 3 2
        4/5 1/3 2/3
        0 6 1 0 0
        1 4 1 0 1

        10 cars with 3 different options, and a total of 2 different configuration
        First option is taken at most 4 every 5 cars
        Second option is taken at most 1 every 3 cars
        Third option is taken at most 2 every 3 cars
        First configuration requires 6 cars to have only the first option
        Second configuration requires 4 cars to have option 1 and 3.
     */

    public static CarSequencing instance(String filename, int... options){

        try{
            int nCars = 0, nOptions = 0, nConfigurations = 0;
            String name = "";

            File file = new File(directoryPath + filename);
            FileReader fr = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();

            // Header
            if(line != null) {
                while(line != null && line.charAt(0) == '/') line = br.readLine();
                while(line != null && line.charAt(0) == '#') {
                    name += line + "\n";
                    line = br.readLine();
                }
                String[] head = line.split(" ");
                nCars = Integer.parseInt(head[0]);
                nOptions = Integer.parseInt(head[1]);
                nConfigurations = Integer.parseInt(head[2]);

                information(name, nCars, nOptions, nConfigurations);

            } else return null;

            int[] seq_up = new int[nOptions];
            int[] seq_down = new int[nOptions];
            int[][] data = new int[nConfigurations][nOptions + 1];

            line = br.readLine();

            // Options
            if(line != null){
                while(line != null && line.charAt(0) == '/') line = br.readLine();
                String[] option = line.split(" ");
                for(int i = 0; i < nOptions; i++) {
                    String[] seq = option[i].split("/");
                    seq_up[i] = Integer.parseInt(seq[0]);
                    seq_down[i] = Integer.parseInt(seq[1]);
                }
            } else return null;

            line = br.readLine();
            int count = 0;
            // Configurations
            while(line != null) {
                while(line != null && line.charAt(0) == '/') line = br.readLine();
                String[] configuration = line.split(" ");
                for(int i = 0; i < nOptions + 1; i++) {
                    data[count][i] = Integer.parseInt(configuration[i+1]);
                }
                count++;
                line = br.readLine();
            }
            br.close();

            return new CarSequencing(seq_up, seq_down, data, options);

        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public static void information(String information, int nCars, int nOptions, int nConfigs){
        System.out.print("\r"+information);
        System.out.println("\rnCars : "+nCars);
        System.out.println("\rnOptions : "+nOptions);
        System.out.println("\rnConfigurations : "+nConfigs);
        System.out.println();
    }

}
