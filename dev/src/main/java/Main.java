import builder.MDDBuilder;
import confidence.MyConstraintOperation;
import confidence.MyMDDBuilder;
import confidence.MyMemory;
import confidence.properties.PropertySumDouble;
import confidence.structures.PrimeFactorization;
import mdd.MDD;
import memory.Memory;
import pmdd.PMDD;
import representation.MDDPrinter;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import utils.ArgumentParser;
import utils.Logger;

import java.io.*;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Main {

    public static void main(String args[]) {

        ArgumentParser parser = new ArgumentParser(
                "-gamma", "9000", "-precision", "4", "-n", "10",
                "-p", "0.95", "-size", "10", "-eps", "5", "-dataFile", "tmp.txt",
                "-generateRandom", "false");
        parser.read(args);

        int gamma = Integer.parseInt(parser.get("-gamma"));
        int precision = Integer.parseInt(parser.get("-precision"));
        int n = Integer.parseInt(parser.get("-n"));
        int epsilon = Integer.parseInt(parser.get("-eps"));
        int size = Integer.parseInt(parser.get("-size"));
        float p = Float.parseFloat(parser.get("-p"));
        String dataFile = "dev/src/main/resources\\" + parser.get("-dataFile");
        boolean generateRandom = Boolean.parseBoolean(parser.get("-generateRandom"));

        if(generateRandom) generateRandomData(dataFile, precision, n, size, p);
        Domains domains = getData(dataFile);

        MDD previous = null;
        MDD tmp = previous;
        for(int i = 0; i < epsilon; i++){
            previous = testLog2(previous,(double)gamma * Math.pow(10, -precision), precision, i, n, domains);
            if(tmp != null) Memory.free(tmp);
            tmp = previous;
        }



        //testBigInteger2(gamma, precision, n, domains);
        //testPrimeFactorization2(gamma, precision, n, domains);


        /*
        int gamma = 80;
        int precision = 2;
        int n = 50;
        Domains domains = generateData(90, 100, 1, n);
        testLog2((double)gamma * Math.pow(10, -precision), precision, 2,  n, domains);
        testBigInteger2(gamma, precision, n, domains);
        testPrimeFactorization2(gamma, precision, n, domains);
        */

        /*
        double x = 0;
        double y = 0;

        for(int i = 0; i < 10000; i++){
            x += Math.log10(0.0001);
            x += Math.log10(0.0004);
            y += Math.log10(0.0002);
            y += Math.log10(0.0002);
        }

        System.out.println(x);
        System.out.println(y);

         */

    }

    /**
     * <b>Randomly generate a defined number of domains</b><br>
     * @param precision The number of decimal to take in account
     * @param n The number of domains
     * @param size The size of domains
     * @param probaMin The minimal probability wanted in the domain
     * @return n random domains
     */
    public static Domains generateRandomData(String fileName, int precision, int n, int size, double probaMin){
        Domains domains = Domains.create();

        int max = (int) Math.pow(10, precision);

        for(int i = 0; i < n; i++){
            int j = 0;
            while (j < size){
                int value = (int) (max * (probaMin + (Math.random() * (1 - probaMin))));
                domains.put(i, value);
                j = domains.get(i).size();
            }
        }

        try {
            File file = new File(fileName);

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

        return domains;
    }

    public static Domains generateData(int min, int max, int step, int n){
        Domains domains = Domains.create();

        for(int i = 0; i < n; i++){
            int count = 0;
            for(int j = min; j <= max; j+= step){
                domains.put(i, j);
            }
        }

        return domains;
    }

    public static void testSum1(MDDPrinter printer){
        SetOf<Integer> V = Memory.SetOfInteger();
        for(int i = 0; i < 2; i++) V.add(i);
        MDD sum = MDDBuilder.sum(MDD.create(), 1, 3, 4, V);

        sum.accept(printer);
    }

    public static Domains getData(String fileName){
        Domains domains = Domains.create();
        try{
            File file = new File(fileName);
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
            return null;
        }
        return domains;
    }

    public static void testMul1(MDDPrinter printer, int n){
        BigInteger min = BigInteger.valueOf(2);
        BigInteger max = BigInteger.valueOf(8);
        Domains domains = Domains.create();

        for(int i = 0; i < n; i++){
            domains.put(i, 1);
            domains.put(i, 2);
        }

        MDD mul = MyMDDBuilder.mul(MDD.create(), min, max, n, domains);
        mul.accept(printer);
    }

    public static void testBigInteger1(MDDPrinter printer){
        int gamma = 70;
        int precision = 2;
        int n = 4;
        Domains domains = Domains.create();

        domains.put(0, 80);
        domains.put(0, 90);
        domains.put(0, 100);

        domains.put(1, 78);
        domains.put(1, 87);
        domains.put(1, 97);

        domains.put(2, 73);
        domains.put(2, 83);
        domains.put(2, 93);

        domains.put(3, 97);
        domains.put(3, 98);
        domains.put(3, 99);

        MDD confidence = MyMDDBuilder.confidence(MDD.create(), gamma, precision, n, domains);
        confidence.accept(printer);
    }

    public static void testBigInteger2(int gamma, int precision, int n, Domains domains){

        long time1;
        long time2;

        Logger.out.information("Test avec BigInteger\n");
        System.out.println("Gamma = " + gamma);

        time1 = System.currentTimeMillis();
        MDD confidence = MyMDDBuilder.confidence(MDD.create(), gamma, precision, n, domains);
        time2 = System.currentTimeMillis();

        Logger.out.information("");
        System.out.println("\nNombre de noeuds : " + confidence.nodes());
        System.out.println("Nombre d'arcs : " + confidence.arcs());
        System.out.println("Nombre de solutions : " + confidence.nSolutions());
        System.out.println("Temps de construction : " + (time2 - time1) + " ms.\n");

        confidence.clearAllAssociations();
        PMDD test = PMDD.create();
        confidence.copy(test);



        Memory.free(confidence);
    }

    public static void testPrimeFactorization1(MDDPrinter printer){
        int gamma = 70;
        int precision = 2;
        int n = 4;
        Domains domains = Domains.create();

        domains.put(0, 80);
        domains.put(0, 90);
        domains.put(0, 100);

        domains.put(1, 78);
        domains.put(1, 87);
        domains.put(1, 97);

        domains.put(2, 73);
        domains.put(2, 83);
        domains.put(2, 93);

        domains.put(3, 97);
        domains.put(3, 98);
        domains.put(3, 99);

        MDD confidence = MyMDDBuilder.confidencePF(MDD.create(), gamma, precision, n, domains);
        confidence.accept(printer);
    }

    public static void testPrimeFactorization2(int gamma, int precision, int n, Domains domains){

        long time1;
        long time2;

        Logger.out.information("Test avec PrimeFactorization\n");
        System.out.println("Gamma = " + gamma);

        time1 = System.currentTimeMillis();
        MDD confidence = MyMDDBuilder.confidencePF(MDD.create(), gamma, precision, n, domains);
        time2 = System.currentTimeMillis();

        Logger.out.information("");
        System.out.println("\nNombre de noeuds : " + confidence.nodes());
        System.out.println("Nombre d'arcs : " + confidence.arcs());
        System.out.println("Nombre de solutions : " + confidence.nSolutions());
        System.out.println("Temps de construction : " + (time2 - time1) + " ms.\n");
        Memory.free(confidence);
    }

    public static void testLog1(MDDPrinter printer){
        double gamma = 0.7;
        int precision = 2;
        int epsilon = 2;
        int n = 4;
        Domains domains = Domains.create();

        domains.put(0, 80);
        domains.put(0, 90);
        domains.put(0, 100);

        domains.put(1, 78);
        domains.put(1, 87);
        domains.put(1, 97);

        domains.put(2, 73);
        domains.put(2, 83);
        domains.put(2, 93);

        domains.put(3, 97);
        domains.put(3, 98);
        domains.put(3, 99);

        MDD confidence = MyMDDBuilder.confidence(MDD.create(), gamma, epsilon, precision, n, domains);
        confidence.accept(printer);
    }

    public static MDD testLog2(MDD previous, double gamma, int precision, int epsilon, int n, Domains domains){
        long time1;
        long time2;

        Logger.out.information("Test avec le logarithme\n");
        System.out.println("Gamma = " + gamma);

        time1 = System.currentTimeMillis();
        MDD confidence = MDD.create();

        if(previous != null) MyConstraintOperation.confidence(confidence, previous, gamma, precision, epsilon, n, domains);
        else MyMDDBuilder.confidence(confidence, gamma, precision, epsilon, n, domains);

        time2 = System.currentTimeMillis();

        Logger.out.information("");
        System.out.println("\nNombre de noeuds : " + confidence.nodes());
        System.out.println("Nombre d'arcs : " + confidence.arcs());
        System.out.println("Nombre de solutions : " + confidence.nSolutions());
        System.out.println("Temps de construction : " + (time2 - time1) + " ms.");

        precision(confidence, domains, n, precision);

        System.out.println();

        return confidence;
    }

    @SuppressWarnings("unchecked")
    private static void precision(MDD result, Domains D, int n, int precision){
        PMDD confidence = PMDD.create();
        MapOf<Integer, Double> mapLog = MyMemory.MapOfIntegerDouble();
        for(int i = 0; i < n; i++){
            for(int v : D.get(i)) mapLog.put(v, Math.log(v * Math.pow(10, -precision)));
        }
        result.clearAllAssociations();
        result.copy(confidence);

        PropertySumDouble confidenceProperty = MyMemory.PropertySumDouble(0, 0, mapLog);
        confidence.addRootProperty("confidence", confidenceProperty);
        MapOf<Integer, Double> ranges = (MapOf<Integer, Double>) confidence.propagateProperties().get("confidence").getData();
        double borne_sup = Math.exp(ranges.get(1));
        double borne_inf = Math.exp(ranges.get(0));
        System.out.println("CONFIDENCE = ["+borne_inf+", " + borne_sup + "]");

        Memory.free(confidence);
        Memory.free(mapLog);
    }

}