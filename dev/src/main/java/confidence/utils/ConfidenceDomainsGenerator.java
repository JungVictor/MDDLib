package confidence.utils;

import structures.Domains;

public class ConfidenceDomainsGenerator {

    /**
     * <b>Randomly generate a defined number of domains</b><br>
     * @param precision The number of decimal to take in account
     * @param n The number of domains
     * @param size The size of domains
     * @param probaMin The minimal probability wanted in the domain
     * @return n random domains
     */
    public static Domains generateRandomDomains(int precision, int n, int size, double probaMin){
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
}
