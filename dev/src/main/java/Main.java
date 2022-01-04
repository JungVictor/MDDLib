import confidence.ConfidenceTests;
import dd.mdd.MDD;
import utils.confidence.ConfidenceDomainsGenerator;
import utils.DomainsManagements;
import structures.Domains;
import utils.ArgumentParser;

public class Main {

    public static void main(String[] args) {

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

        //ConfidenceTests.testLogInt2(null, gamma, precision, epsilon, 15, n, domains);
        //ConfidenceTests.testLogInt3(gamma, precision, epsilon, 15, n, domains);
        MDD result = ConfidenceTests.testLog3(gamma, precision, epsilon, n, domains);

        //MDD test = MDDReader.load(MDD.create(), "MDD_LAYERS_11_input_20000.dd.mdd");
        //System.out.println("\r"+test.size());



        /*
        MDD previous = null;
        MDD tmp = previous;
        for(int i = 0; i < epsilon; i++){
            previous = ConfidenceTests.testLog2(null,(double)gamma * Math.pow(10, -precision), precision, i, n, domains);

            /* Check the difference between two MDDs
            if(tmp != null) {
                MDD test = Operation.minus(previous, Operation.intersection(previous, tmp));
                System.out.println("\nSOLUTIONS = " + test.nSolutions());
                test.accept(new MDDPrinter());
                Memory.free(test);
            }
            SAFE TO DELETE, TESTING PURPOSES

            if(tmp != null) Memory.free(tmp);
            tmp = previous;
        }
        */



        //ConfidenceTests.testBigInteger2(gamma, precision, n, domains);
        //ConfidenceTests.testPrimeFactorization2(gamma, precision, n, domains);

        //int min = 9 * ((int) Math.pow(10, precision-1));
        //int max = (int) Math.pow(10, precision);
        //int step = (int) Math.pow(10, precision-2);
        //Domains domains = ConfidenceDomainsGenerator.generateData(min, max, step, n);
        //ConfidenceTests.testMulRelaxed1(new MDDPrinter());
        //ConfidenceTests.testMulRelaxed3(gamma, precision, epsilon, n, domains);

        /*
        MDD previous = null;
        for(int i = 0; i < epsilon; i++) {
            MDD confidence = ConfidenceTests.testMulRelaxed2(previous, gamma, precision, i, n, domains);
            if(previous != null) System.out.println("\n"+Operation.inclusion(confidence, previous));
            System.out.println(confidence.getDomain(confidence.getDomains().size() - 1));
            previous = confidence;
        }

         */

        /*
        MDD test1 = ConfidenceTests.testMulRelaxed3(gamma, precision, epsilon, n, domains);
        //ConfidenceTests.testLog2(null,(double)gamma * Math.pow(10, -precision), precision, epsilon,  n, domains);
        MDD test2 = ConfidenceTests.testLog3(gamma, precision, epsilon,  n, domains);

        System.out.println(test1.nSolutions());
        System.out.println(test2.nSolutions());

        MDD xD = Operation.minus(test2, test1);
        xD.accept(new MDDPrinter());
        System.out.println(xD.nSolutions());
        ConfidenceTests.precision(xD, domains, n, precision);

        MDD mdr = Operation.negation(test1);
        ConfidenceTests.precision(mdr, domains, n, precision);

        System.out.println(mdr.nSolutions() + test1.nSolutions());

        for(int i = 0; i < mdr.getDomains().size(); i++) {
            System.out.println(mdr.getDomain(i));
            System.out.println(test1.getDomain(i));
            System.out.println(test2.getDomain(i));
        }

        System.out.println(Operation.inclusion(xD, mdr));
        System.out.println(Operation.inclusion(xD, test1));


        MDD ptdr = ConfidenceTests.testMulRelaxed2(null, gamma, precision, 2, n, domains);
        MDD xptdr = ConfidenceTests.testLog2(null, (double)gamma * Math.pow(10, -precision), precision, 2, n, domains);
        System.out.println("\n"+(Operation.minus(xptdr, ptdr).nSolutions()));
        System.out.println(Operation.inclusion(xD, xptdr));

         */

        //ConfidenceTests.testBigInteger2(gamma, precision, n, domains);
        //ConfidenceTests.testPrimeFactorization2(gamma, precision, n, domains);
    }



}