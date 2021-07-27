import confidence.ConfidenceTests;
import confidence.utils.ConfidenceDomainsGenerator;
import confidence.utils.DomainsManagements;
import structures.Domains;
import utils.ArgumentParser;

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
        //Domains domains = DomainsManagements.getDomains(dataFile);
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

        int min = 9 * ((int) Math.pow(10, precision-1));
        int max = (int) Math.pow(10, precision);
        int step = (int) Math.pow(10, precision-2);
        Domains domains = ConfidenceDomainsGenerator.generateData(min, max, step, n);
        //ConfidenceTests.testLog2((double)gamma * Math.pow(10, -precision), precision, 2,  n, domains);
        ConfidenceTests.testBigInteger2(gamma, precision, n, domains);
        //ConfidenceTests.testPrimeFactorization2(gamma, precision, n, domains);


    }



}