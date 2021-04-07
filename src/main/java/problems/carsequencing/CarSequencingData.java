package problems.carsequencing;

import memory.Memory;
import problems.CarSequencing;
import structures.generics.ListOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;

public class CarSequencingData {

    private final int[] max, size;
    private final int[][] options;

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
                for(int j = 0; j < enabledOptions.size(); j++)
                    data.get(value).add(j, options[i][enabledOptions.get(j)]);
            }
            binding.put(i, value);
            data.get(value).add(0, data.get(value).get(0) + options[i][0]);
        }
    }

    public void addOptions(int... options){
        for(int option : options) if(!this.enabledOptions.contains(option)) this.enabledOptions.add(option);
    }

    public int nCars(){
        return 0;
    }

    public int seqSizeOption(int option){
        return size[option];
    }

    public int seqMaxOption(int option){
        return max[option];
    }

    public int nCarsWithOption(int option){
        int cars = 0;
        for(int config : data){
            if(isOptionInConfig(option, config)) cars += nCarsInConfig(config);
        }
        return cars;
    }

    public int nOptions(){
        return enabledOptions.size();
    }

    public int nConfigs(){
        return V.size();
    }

    public ListOf<Integer> getV(){
        return V;
    }

    public boolean isOptionInConfig(int option, int config){
        return data.get(config).get(option+1) == 1;
    }

    public int nCarsInConfig(int config){
        return data.get(config).get(0);
    }

}
