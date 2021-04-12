package problems;

import builder.MDDBuilder;
import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.memory.PMemory;
import problems.carsequencing.CarSequencingData;
import representation.MDDPrinter;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import utils.Logger;

public class CarSequencing {

    private final CarSequencingData data;

    public CarSequencing(int[] min, int[] max, int[][] options, int... enabledOptions){
        data = new CarSequencingData(min, max, options, enabledOptions);
    }

    public CarSequencing(int[] min, int[] max, int[][] options){
        data = new CarSequencingData(min, max, options);
    }

    public MDD solve(){
        data.generate();
        ArrayOf<MDD> options = options();
        ArrayOf<MDD> configs = configs();

        ArrayOf<MDD> mdds = Memory.ArrayOfMDD(options.length + configs.length);
        for(int i = 0; i < options().length; i++) mdds.set(i, options.get(i));
        for(int i = 0; i < configs.length; i++) mdds.set(i+options.length, configs.get(i));

        MDD solution = Operation.intersection(PMemory.PMDD(), mdds);
        solution.reduce();

        for(MDD mdd : mdds) Memory.free(mdd);
        Memory.free(mdds);
        Memory.free(options);
        Memory.free(configs);

        return solution;
    }

    public MDD solve(MDD mdd, int option){
        // The mdd is a relaxed solution
        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();

        MapOf<Integer, Integer> old_bindings = Memory.MapOfIntegerInteger(), bindings = data.getBinding();
        for(int key : bindings) old_bindings.put(key, bindings.get(key));

        data.addOptions(option);
        data.generate();

        MapOf<Integer, SetOf<Integer>> values = Memory.MapOfIntegerSetOfInteger();
        for(int key : bindings){
            if(!values.contains(old_bindings.get(key))) values.put(old_bindings.get(key), Memory.SetOfInteger());
            values.get(old_bindings.get(key)).add(bindings.get(key));
        }

        Logger.out.information("Replacing values...\n");
        mdd.replace(values);
        Logger.out.information("Replacing values done\n");

        MDD new_option = option(data.nOptions()-1, V0, V1);
        ArrayOf<MDD> configs = configs();
        ArrayOf<MDD> mdds = Memory.ArrayOfMDD(configs.length + 2);

        for(int i = 0; i < configs.length; i++) mdds.set(i, configs.get(i));
        mdds.set(configs.length, new_option);
        mdds.set(configs.length + 1, mdd);

        MDD result = Operation.intersection(mdds);

        for(SetOf<Integer> set : values.values()) Memory.free(set);
        Memory.free(values);
        Memory.free(V0);
        Memory.free(V1);
        Memory.free(mdd);
        Memory.free(configs);
        Memory.free(new_option);
        Memory.free(old_bindings);

        return result;
    }

    private ArrayOf<MDD> options(){
        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();
        ArrayOf<MDD> options = Memory.ArrayOfMDD(data.nOptions());
        for(int i = 0; i < data.nOptions(); i++){
            V0.clear(); V1.clear();
            options.set(i, option(i, V0, V1));
        }
        Memory.free(V0);
        Memory.free(V1);

        return options;
    }

    private MDD option(int i, SetOf<Integer> V0, SetOf<Integer> V1){
        for(int v : data.getV()){
            if(data.isOptionInConfig(i, v)) V1.add(v);
            else V0.add(v);
        }

        MDD seq = MDDBuilder.sequence(Memory.MDD(), data.seqSizeOption(i), 0, data.seqMaxOption(i), data.nCars());
        MDD sum = MDDBuilder.sum(Memory.MDD(), data.nCarsWithOption(i), data.nCars());
        MDD option = Operation.intersection(seq, sum);
        option.replace(V0, V1);

        Memory.free(seq);
        Memory.free(sum);

        return option;
    }

    private ArrayOf<MDD> configs(){
        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();
        ArrayOf<MDD> configs = Memory.ArrayOfMDD(data.nConfigs());

        int i = 0;
        for(int v : data.getV()){
            V0.clear(); V1.clear();
            V1.add(v);
            V0.add(data.getV()); V0.remove(v);

            configs.set(i, config(i, V0, V1));

            i++;
        }

        Memory.free(V0);
        Memory.free(V1);

        return configs;
    }

    private MDD config(int i, SetOf<Integer> V0, SetOf<Integer> V1){
        MDD config = MDDBuilder.sum(Memory.MDD(), data.nCarsInConfig(i), data.nCars());
        config.replace(V0, V1);
        return config;
    }
}
