import builder.MDDBuilder;
import confidence.MyMDDBuilder;
import confidence.structures.PrimeFactorization;
import mdd.MDD;
import memory.Memory;
import representation.MDDPrinter;
import structures.Domains;
import structures.generics.SetOf;
import utils.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Main {

    public static void main(String args[]) {
        MDDPrinter printer = new MDDPrinter();

        //testSum1(printer);

        //testBigInteger1(printer);

        /*
        int gamma = 9000;
        int precision = 4;
        int n = 25;
        Domains domains = generateRandomData(precision, n, 10, 0.97);
        testLog2((double)gamma * Math.pow(10, -precision), precision, n, domains);
        testBigInteger2(gamma, precision, n, domains);
         */

        /*
        int gamma = 80;
        int precision = 2;
        int n = 7;
        Domains domains = generateData(90, 100, 1, n);
        testLog2((double)gamma * Math.pow(10, -precision), precision, n, domains);
        testBigInteger2(gamma, precision, n, domains);
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

        //testLog1(printer);


        PrimeFactorization t1 = new PrimeFactorization(999999);
        System.out.println("t1 : " + t1);
        System.out.println("Log10 : " + t1.toLog10());
        PrimeFactorization t2 = new PrimeFactorization(181712);
        System.out.println("t2 : " + t2);
        System.out.println("Log10 : " + t2.toLog10());
        PrimeFactorization t3 = new PrimeFactorization(386547);
        System.out.println("t3 : " + t3);
        System.out.println("Log10 : " + t3.toLog10());
        PrimeFactorization t4 = new PrimeFactorization(168767);
        System.out.println("t4 : " + t4);
        System.out.println("Log10 : " + t4.toLog10());
        PrimeFactorization t5 = new PrimeFactorization(168768);
        System.out.println("t5 : " + t5);
        System.out.println("Log10 : " + t5.toLog10());
        PrimeFactorization t6 = new PrimeFactorization(435162);
        System.out.println("t6 : " + t6);
        System.out.println("Log10 : " + t6.toLog10());
        PrimeFactorization t7 = t1.multiply(t2).multiply(t3).multiply(t4).multiply(t5).multiply(t6);
        System.out.println("t7 : " + t7);
        System.out.println("Log10 : " + t7.toLog10());

    }

    /**
     * <b>Randomly generate a defined number of domains</b><br>
     * @param precision The number of decimal to take in account
     * @param n The number of domains
     * @param domainsAverageSize The average size of domains
     * @param probaMin The minimal probability wanted in the domain
     * @return n random domains
     */
    public static Domains generateRandomData(int precision, int n, int domainsAverageSize, double probaMin){
        Domains domains = Domains.create();

        int max = (int) Math.pow(10, precision);
        double proportion = domainsAverageSize / ((1 - probaMin) * max);

        for(int i = 0; i < n; i++){
            int count = 0;
            for(int j = (int) (max * probaMin) ; j <= max; j++){
                if(Math.random() <= proportion) {
                    domains.put(i, j);
                    count++;
                }
            }
            System.out.println("Nombre de valeur dans le domaine " + i + " : " + count);
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
        Logger.out.information("Gamma = " + gamma + "\n");

        time1 = System.currentTimeMillis();
        MDD confidence = MyMDDBuilder.confidence(MDD.create(), gamma, precision, n, domains);
        time2 = System.currentTimeMillis();

        Logger.out.information("\nNombre de noeuds : " + confidence.nodes() + "\n");
        System.out.println("Nombre d'arcs : " + confidence.arcs());
        System.out.println("Nombre de solutions : " + confidence.nSolutions());
        System.out.println("Temps de construction : " + (time2 - time1) + " ms.");
    }

    public static void testLog1(MDDPrinter printer){
        double gamma = 0.7;
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

    public static void testLog2(double gamma, int precision, int n, Domains domains){
        long time1;
        long time2;

        System.out.println("\nTest avec le logarithme");
        System.out.println("Gamma = " + gamma);

        time1 = System.currentTimeMillis();
        MDD confidence = MyMDDBuilder.confidence(MDD.create(), gamma, precision, n, domains);
        time2 = System.currentTimeMillis();

        System.out.println("\nNombre de noeuds : " + confidence.nodes());
        System.out.println("Nombre d'arcs : " + confidence.arcs());
        System.out.println("Nombre de solutions : " + confidence.nSolutions());
        System.out.println("Temps de construction : " + (time2 - time1) + " ms.");
    }

}