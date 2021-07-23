import builder.MDDBuilder;
import confidence.MyConstraintOperation;
import confidence.MyMDDBuilder;
import confidence.MyMemory;
import confidence.properties.PropertySumDouble;
import confidence.utils.ConfidenceDomainsGenerator;
import confidence.utils.DomainsManagements;
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
        String dataFile = parser.get("-dataFile");
        boolean generateRandom = Boolean.parseBoolean(parser.get("-generateRandom"));

        if(generateRandom) DomainsManagements.saveDomains(dataFile, ConfidenceDomainsGenerator.generateRandomDomains(precision, n, size, p));
        Domains domains = DomainsManagements.getDomains(dataFile);

        MDD previous = null;
        MDD tmp = previous;
        for(int i = 0; i < epsilon; i++){
            previous = testLog2(null,(double)gamma * Math.pow(10, -precision), precision, i, n, domains);

            /* Check the difference between two MDDs
            if(tmp != null) {
                MDD test = Operation.minus(previous, Operation.intersection(previous, tmp));
                System.out.println("\nSOLUTIONS = " + test.nSolutions());
                test.accept(new MDDPrinter());
                Memory.free(test);
            }
            SAFE TO DELETE, TESTING PURPOSES */

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

    public static void testSum1(MDDPrinter printer){
        SetOf<Integer> V = Memory.SetOfInteger();
        for(int i = 0; i < 2; i++) V.add(i);
        MDD sum = MDDBuilder.sum(MDD.create(), 1, 3, 4, V);

        sum.accept(printer);
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