package confidence;

import builder.MDDBuilder;
import confidence.properties.PropertySumDouble;
import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.PMDD;
import representation.MDDPrinter;
import structures.Domains;
import structures.generics.MapOf;
import structures.generics.SetOf;
import utils.Logger;

import java.math.BigInteger;

public strictfp class ConfidenceTests {

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
        Logger.out.information("Epsilon = " + epsilon + "\n");
        Logger.out.information("Gamma = " + gamma + "\n");

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

        //precision(confidence, domains, n, precision);

        System.out.println();

        return confidence;
    }

    public static void testLog3(double gamma, int precision, int epsilon, int n, Domains domains) {
        MDD result = null;
        MDD confidence, extract = null;
        MDD tmp = null, tmp_res = null;
        for (int i = 0; i <= epsilon; i++) {
            confidence = testLog2(extract,gamma * Math.pow(10, -precision), precision, i, n, domains);

            if(i == epsilon) extract = null;
            else if (confidence.nSolutions() > 0) extract = extract(confidence, domains, n, precision, gamma);

            if (extract != null && extract.nSolutions() == 0) {
                Memory.free(extract);
                if(result == null) result = confidence;
                else if(confidence.nSolutions() > 0) {
                    result = Operation.union(result, confidence);
                    Memory.free(tmp_res);
                }
                break;
            }

            if(i == epsilon) {
                if(result == null) result = confidence;
                else {
                    Logger.out.information("\rBuilding the union... ");
                    result = Operation.union(result, confidence);
                    Memory.free(confidence);
                }
            } else {
                Logger.out.information("\rBuilding the difference... ");
                if (result == null) result = Operation.minus(confidence, extract);
                else {
                    if(confidence.nSolutions() == 0) break;
                    MDD difference = Operation.minus(confidence, extract);
                    Logger.out.information("\rBuilding the union... ");
                    result = Operation.union(result, difference);
                    Memory.free(difference);
                }
                Memory.free(confidence);
            }

            if(tmp != null) {
                Memory.free(tmp);
                Memory.free(tmp_res);
            }

            tmp = extract;
            tmp_res = result;
        }

        int nNodes = 0;
        int nArcs = 0;
        double nSol = 0;

        if(result != null) {
            nNodes = result.nodes();
            nArcs = result.arcs();
            nSol = result.nSolutions();
            precision(result, domains, n, precision);
        }

        Logger.out.information("");
        Logger.out.information("\r\nNombre de noeuds : " + nNodes);
        Logger.out.information("\r\nNombre d'arcs : " + nArcs);
        Logger.out.information("\r\nNombre de solutions : " + nSol);

        System.out.println();
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
        MapOf<Integer, Double> ranges = (MapOf<Integer, Double>) confidence.propagateProperties(false).get("confidence").getData();
        double borne_sup = Math.exp(ranges.get(1));
        double borne_inf = Math.exp(ranges.get(0));
        System.out.println("\rCONFIDENCE = ["+borne_inf+", " + borne_sup + "]");

        Memory.free(confidence);
        Memory.free(mapLog);
    }

    /**
     * Extract all potentially false solutions from the MDD
     * @param result The set of all solutions
     * @param D Domains
     * @param n Size of solution
     * @param precision Precision of the solution
     * @param gamma The threshold
     * @return All nodes and arcs that belongs to a false solution
     */
    private static MDD extract(MDD result, Domains D, int n, int precision, double gamma){
        Logger.out.information("\rExtracting... ");
        PMDD confidence = PMDD.create();
        MapOf<Integer, Double> mapLog = MyMemory.MapOfIntegerDouble();
        for(int i = 0; i < n; i++){
            for(int v : D.get(i)) mapLog.put(v, Math.log(v * Math.pow(10, -precision)));
        }
        result.clearAllAssociations();
        result.copy(confidence);

        PropertySumDouble confidenceProperty = MyMemory.PropertySumDouble(0, 0, mapLog);
        confidence.addTtProperty("confidence", confidenceProperty);
        confidence.reversePropagateProperties(false);

        MDD extract = ConstraintPruning.iterative_prune(confidence, "confidence", mapLog, Math.log(gamma * Math.pow(10, -precision)));
        Memory.free(mapLog);
        Memory.free(confidence);
        return extract;
    }

}
