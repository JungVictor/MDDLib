package utils;

import structures.Domains;

import java.io.*;

public class DomainsManagements {

    private static String directoryPath = "data/MDDDomains/";

    public static Domains getDomains(String fileName){
        Domains domains = Domains.create();
        try{
            File file = new File(directoryPath + fileName);
            FileReader fr = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);
            String domain = br.readLine();

            int count = 0;

            while(domain != null){
                String delims = "[\\[\\], ]+";
                String[] numbers = domain.split(delims);
                for(int i = 0; i < numbers.length-1; i++) {
                    domains.put(count, Integer.parseInt(numbers[i+1]));
                }
                domain = br.readLine();
                count++;
            }
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return domains;
    }

    public static void saveDomains(String fileName, Domains domains){
        try {
            File file = new File(directoryPath + fileName);

            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();

            if (!file.exists()) file.createNewFile();

            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < domains.size(); i++){
                builder.append(domains.get(i));
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
