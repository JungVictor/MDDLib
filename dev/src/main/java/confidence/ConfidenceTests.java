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

    public static void testMulRelaxed1(MDDPrinter printer){
        int gamma = 70;
        int precision = 2;
        int epsilon = 6;
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

        MDD confidence = MyMDDBuilder.confidenceMulRelaxed(MDD.create(), gamma, precision, epsilon, n, domains);
        confidence.accept(printer);
    }

    public static MDD testMulRelaxed2(MDD previous, int gamma, int precision, int epsilon, int n, Domains domains){
        long time1;
        long time2;

        Logger.out.information("Test avec les entiers et la relaxation\n");
        Logger.out.information("Epsilon = " + epsilon + "\n");
        Logger.out.information("Gamma = " + gamma + "\n");

        time1 = System.currentTimeMillis();
        MDD confidence = MDD.create();

        if(previous != null) MyConstraintOperation.confidenceMulRelaxed(confidence, previous, gamma, precision, epsilon, n, domains);
        else MyMDDBuilder.confidenceMulRelaxed(confidence, gamma, precision, epsilon, n, domains);

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

    public static MDD testMulRelaxed3(int gamma, int precision, int epsilon, int n, Domains domains) {
        MDD result = null;
        MDD confidence = null, extract = null;
        MDD tmp = null, tmp_res = null;

        long time = System.currentTimeMillis();

        for (int i = 0; i <= epsilon; i++) {
            confidence = testMulRelaxed2(extract, gamma, precision, i, n, domains);
            // Free the ancient extract
            if(extract != null) {
                Memory.free(extract);
                extract = null;
            }

            if(confidence.nSolutions() == 0) break;
            else if(i == epsilon) {
                if(result == null) {
                    result = confidence;
                    confidence = null;
                } else {
                    tmp_res = result;
                    result = Operation.union(result, confidence);
                    Memory.free(tmp_res);
                }
                break;
            }

            extract = extract(confidence, domains, n, precision, gamma);

            // Stop
            if(extract.nSolutions() == 0) {

                Memory.free(extract);
                extract = null;

                if(result == null) {
                    result = confidence;
                    confidence = null;
                }
                else {
                    tmp_res = result;
                    result = Operation.union(result, confidence);
                    Memory.free(tmp_res);
                    Memory.free(confidence);
                    confidence = null;
                    tmp_res = null;
                }
                break;
            }

            MDD diff = Operation.minus(confidence, extract);
            Memory.free(confidence);
            confidence = null;

            if(diff.nSolutions() > 0) {
                if (result == null) result = diff;
                else {
                    tmp_res = result;
                    result = Operation.union(result, diff);
                    Memory.free(tmp_res);
                    Memory.free(diff);
                }
            } else Memory.free(diff);
        }

        if(extract != null) Memory.free(extract);
        if(confidence != null) Memory.free(confidence);

        time = System.currentTimeMillis() - time;

        Logger.out.information("\r\nTemps (ms) : " + time + "\n\n");

        int nNodes = 0;
        int nArcs = 0;
        double nSol = 0;

        if(result != null) {
            nNodes = result.nodes();
            nArcs = result.arcs();
            nSol = result.nSolutions();
            precision(result, domains, n, precision);

            MDD negation = Operation.negation(result);
            precision(negation, domains, n, precision);
        }

        Logger.out.information("\r\nNombre de noeuds : " + nNodes);
        Logger.out.information("\r\nNombre d'arcs : " + nArcs);
        Logger.out.information("\r\nNombre de solutions : " + nSol);


        System.out.println();
        return result;
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

        MDD confidence = MyMDDBuilder.confidence(MDD.create(), gamma, precision, epsilon, n, domains);
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

    public static MDD testLog3(double gamma, int precision, int epsilon, int n, Domains domains) {
        MDD result = null;
        MDD confidence = null, extract = null;
        MDD tmp = null, tmp_res = null;

        long time = System.currentTimeMillis();

        for (int i = 0; i <= epsilon; i++) {
            confidence = testLog2(extract,gamma * Math.pow(10, -precision), precision, i, n, domains);

            // Free the ancient extract
            if(extract != null) {
                Memory.free(extract);
                extract = null;
            }

            if(confidence.nSolutions() == 0) break;
            else if(i == epsilon) {
                if(result == null) {
                    result = confidence;
                    confidence = null;
                } else {
                    tmp_res = result;
                    result = Operation.union(result, confidence);
                    Memory.free(tmp_res);
                }
                break;
            }

            extract = extract(confidence, domains, n, precision, gamma);

            // Stop
            if(extract.nSolutions() == 0) {

                Memory.free(extract);
                extract = null;

                if(result == null) {
                    result = confidence;
                    confidence = null;
                }
                else {
                    tmp_res = result;
                    result = Operation.union(result, confidence);
                    Memory.free(tmp_res);
                    Memory.free(confidence);
                    confidence = null;
                    tmp_res = null;
                }
                break;
            }

            MDD diff = Operation.minus(confidence, extract);
            Memory.free(confidence);
            confidence = null;

            if(diff.nSolutions() > 0) {
                if (result == null) result = diff;
                else {
                    tmp_res = result;
                    result = Operation.union(result, diff);
                    Memory.free(tmp_res);
                    Memory.free(diff);
                }
            } else Memory.free(diff);
        }

        if(extract != null) Memory.free(extract);
        if(confidence != null) Memory.free(confidence);

        time = System.currentTimeMillis() - time;

        Logger.out.information("\r\nTemps (ms) : " + time + "\n\n");

        int nNodes = 0;
        int nArcs = 0;
        double nSol = 0;

        if(result != null) {
            nNodes = result.nodes();
            nArcs = result.arcs();
            nSol = result.nSolutions();
            precision(result, domains, n, precision);

            MDD negation = Operation.negation(result);
            precision(negation, domains, n, precision);
        }

        Logger.out.information("\r\nNombre de noeuds : " + nNodes);
        Logger.out.information("\r\nNombre d'arcs : " + nArcs);
        Logger.out.information("\r\nNombre de solutions : " + nSol);


        System.out.println();
        return result;
    }

    public static MDD testLogInt2(MDD previous, int gamma, int precision, int epsilon, int logPrecision, int n, Domains domains){
        long time1;
        long time2;

        Logger.out.information("Test avec le logarithme entier\n");
        Logger.out.information("Epsilon = " + epsilon + "\n");
        Logger.out.information("Gamma = " + gamma + "\n");

        time1 = System.currentTimeMillis();
        MDD confidence = MDD.create();

        if(previous != null) MyConstraintOperation.confidence(confidence, previous, gamma, precision, epsilon, n, logPrecision, domains);
        else MyMDDBuilder.confidence(confidence, gamma, precision, epsilon, n, logPrecision, domains);

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

    public static MDD testLogInt3(int gamma, int precision, int epsilon, int logPrecision, int n, Domains domains) {
        MDD result = null;
        MDD confidence = null, extract = null;
        MDD tmp = null, tmp_res = null;

        long time = System.currentTimeMillis();

        for (int i = 0; i <= epsilon; i++) {
            confidence = testLogInt2(extract, gamma, precision, i, logPrecision, n, domains);

            // Free the ancient extract
            if(extract != null) {
                Memory.free(extract);
                extract = null;
            }

            if(confidence.nSolutions() == 0) break;
            else if(i == epsilon) {
                if(result == null) {
                    result = confidence;
                    confidence = null;
                } else {
                    tmp_res = result;
                    result = Operation.union(result, confidence);
                    Memory.free(tmp_res);
                }
                break;
            }

            extract = extract(confidence, domains, n, precision, gamma);

            // Stop
            if(extract.nSolutions() == 0) {

                Memory.free(extract);
                extract = null;

                if(result == null) {
                    result = confidence;
                    confidence = null;
                }
                else {
                    tmp_res = result;
                    result = Operation.union(result, confidence);
                    Memory.free(tmp_res);
                    Memory.free(confidence);
                    confidence = null;
                    tmp_res = null;
                }
                break;
            }

            MDD diff = Operation.minus(confidence, extract);
            Memory.free(confidence);
            confidence = null;

            if(diff.nSolutions() > 0) {
                if (result == null) result = diff;
                else {
                    tmp_res = result;
                    result = Operation.union(result, diff);
                    Memory.free(tmp_res);
                    Memory.free(diff);
                }
            } else Memory.free(diff);
        }

        if(extract != null) Memory.free(extract);
        if(confidence != null) Memory.free(confidence);

        time = System.currentTimeMillis() - time;

        Logger.out.information("\r\nTemps (ms) : " + time + "\n\n");

        int nNodes = 0;
        int nArcs = 0;
        double nSol = 0;

        if(result != null) {
            nNodes = result.nodes();
            nArcs = result.arcs();
            nSol = result.nSolutions();
            precision(result, domains, n, precision);

            MDD negation = Operation.negation(result);
            precision(negation, domains, n, precision);
        }

        Logger.out.information("\r\nNombre de noeuds : " + nNodes);
        Logger.out.information("\r\nNombre d'arcs : " + nArcs);
        Logger.out.information("\r\nNombre de solutions : " + nSol);


        System.out.println();
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void precision(MDD result, Domains D, int n, int precision){
        if(result.nSolutions() == 0) {
            System.out.println("\rCONFIDENCE = NO SOLUTION !");
            return;
        }
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
