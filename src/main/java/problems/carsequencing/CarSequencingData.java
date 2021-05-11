package problems.carsequencing;

import memory.Memory;
import structures.generics.ListOf;
import structures.generics.MapOf;

public class CarSequencingData {

    private final int[] max, size;
    private final int[][] options;
    private int[] nConfigMin, nConfigMax, nOptionsMin, nOptionsMax;
    private int nCars, nCarsRelaxed;

    private MapOf<Integer, ListOf<Integer>> data;
    private ListOf<Integer> enabledOptions;
    private ListOf<Integer> V;
    private MapOf<Integer, Integer> binding;

    public CarSequencingData(int[] max, int[] size, int[][] options, int... enabledOptions){
        this.size = size;
        this.max = max;
        this.options = options;
        this.enabledOptions = Memory.ListOfInteger();
        for(int option : enabledOptions) this.enabledOptions.add(option);
        V = Memory.ListOfInteger();
        data = Memory.MapOfIntegerListOfInteger();
        binding = Memory.MapOfIntegerInteger();

        for(int[] opt : options) nCars += opt[0];
    }

    public CarSequencingData(int[] max, int[] size, int[][] options){
        this.size = size;
        this.max = max;
        this.options = options;
    }

    public void generate(){
        V.clear();

        for(int key : data) Memory.free(data.get(key));
        data.clear();

        for(int i = 0; i < options.length; i++){
            int value = 0;
            for(int option : enabledOptions){
                value *= 2;
                value += options[i][option+1];
            }
            if(!V.contains(value)) {
                V.add(value);
                data.put(value, Memory.ListOfInteger());
                data.get(value).add(0);
                for(int j = 0; j < enabledOptions.size(); j++)
                    data.get(value).add(options[i][enabledOptions.get(j)+1]);
            }
            binding.put(i, value);
            data.get(value).set(0, data.get(value).get(0) + options[i][0]);
        }

        generate_relaxed(10);
    }

    public void generate_relaxed(int seqSize){
        this.nConfigMax = new int[options.length];
        this.nConfigMin = new int[this.nConfigMax.length];

        for(int i = 0; i < options.length; i++) {
            double X = 1;
            for(int j = 0; j < options[i].length - 1; j++) if(options[i][j+1] == 1) X = Math.min(X, max[j] / (double) size[j]);
            this.nConfigMax[i] = (int) Math.min(Math.ceil(seqSize * X), options[i][0]);
            this.nConfigMin[i] = (int) Math.max((seqSize - Math.floor((nCars * X)) + options[i][0]) * X, 0);
        }

        this.nOptionsMax = new int[options[0].length - 1];
        this.nOptionsMin = new int[this.nOptionsMax.length];

        for(int i = 0; i < options.length; i++) {
            for (int j = 1; j < options[0].length; j++) {
                nOptionsMax[j - 1] += options[i][j] * nConfigMax[i];
                nOptionsMin[j - 1] += options[i][j] * nConfigMin[i];
            }
        }

        nCarsRelaxed = seqSize;
    }

    public void addOptions(int... options){
        for(int option : options) if(!this.enabledOptions.contains(option)) this.enabledOptions.add(option);
    }

    public int nCars(){
        return nCars;
    }
    public int nCarsRelaxed(){
        return nCarsRelaxed;
    }

    public int seqSizeOption(int option){
        return size[enabledOptions.get(option)];
    }

    public int seqMaxOption(int option){
        return max[enabledOptions.get(option)];
    }

    public int nCarsWithOption(int option){
        int cars = 0;
        for(int config : data){
            if(isOptionInConfig(option, config)) cars += nCarsInConfig(config);
        }
        return cars;
    }

    public MapOf<Integer, Integer> getBinding(){
        return binding;
    }

    public int nOptions(){
        return enabledOptions.size();
    }

    public int nConfigs(){
        return V.size();
    }

    public int nConfigMin(int i){
        return nConfigMin[i];
    }
    public int nConfigMax(int i){
        return nConfigMax[i];
    }
    public int nOptionMin(int i){
        return nOptionsMin[i];
    }
    public int nOptionMax(int i){
        return nOptionsMax[i];
    }

    public ListOf<Integer> getV(){
        return V;
    }

    public boolean isOptionInConfig(int option, int config){
        return data.get(config).get(enabledOptions.get(option)+1) == 1;
    }

    public int nCarsInConfig(int config){
        return data.get(config).get(0);
    }

}
