package problems;

import builder.MDDBuilder;
import builder.constraints.MDDGCC;
import mdd.MDD;
import mdd.operations.ConstraintMDD;
import mdd.operations.ConstraintOperation;
import mdd.operations.Operation;
import memory.Memory;
import pmdd.memory.PMemory;
import problems.carsequencing.CarSequencingData;
import representation.MDDPrinter;
import structures.generics.ArrayOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.TupleOfInt;
import utils.Logger;

public class CarSequencing {

    private final CarSequencingData data;
    private final ConstraintMDD constraint = new ConstraintMDD();

    public CarSequencing(int[] min, int[] max, int[][] options, int... enabledOptions){
        data = new CarSequencingData(min, max, options, enabledOptions);
    }

    public CarSequencing(int[] min, int[] max, int[][] options){
        data = new CarSequencingData(min, max, options);
    }

    public MDD solve(){
        data.generate();
        ArrayOf<MDD> options = options();

        MDD opts = Operation.intersection(Memory.MDD(), options);

        MapOf<Integer, TupleOfInt> gcc = Memory.MapOfIntegerTupleOfInt();
        for(int v : data.getV()){
            int ncars = data.nCarsInConfig(v);
            gcc.put(v, Memory.TupleOfInt(ncars, ncars));
        }
        // NEW METHOD
        // MDD solution = MDDGCC.intersection(Memory.MDD(), opts, gcc);


        // OLD METHOD
        //ArrayOf<MDD> configs = configs();
        //MDD conf = Operation.intersection(Memory.MDD(), configs);
        MDD conf = MDDGCC.generate(Memory.MDD(), data.nCars()+1, gcc, null);
        //for(MDD config : configs) Memory.free(config);
        //Memory.free(configs);
        MDD solution = Operation.intersection(PMemory.PMDD(), opts, conf);
        Memory.free(conf);


        solution.reduce();

        for(MDD mdd : options) Memory.free(mdd);
        Memory.free(options);
        Memory.free(opts);

        return solution;
    }

    public MDD solve_relaxed(){
        data.generate();

        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();

        MDD current = option_relaxed(0, V0, V1);
        MDD prev = current;

        for(int i = 1; i < data.nOptions(); i++) {
            current = Operation.intersection(current, option_relaxed(i, V0, V1));
            Memory.free(prev);
            prev = current;
        }

        for(int i = 1; i < data.nOptions(); i++) {
            current = Operation.intersection(current, config_relaxed(i, V0, V1));
            Memory.free(prev);
            prev = current;
        }

        return current;
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

        Logger.out.information("\rReplacing values...");
        mdd.replace(values);
        Logger.out.information("\rReplacing values done");

        MDD new_option = option(data.nOptions()-1, V0, V1);
        Logger.out.information("\rCarSequencing : Adding new option sequence\n");
        MDD newMDD = Operation.intersection(mdd, new_option);

        Memory.free(new_option);

        Logger.out.information("\rCarSequencing : Adding GCC\n");


        MapOf<Integer, TupleOfInt> gcc = Memory.MapOfIntegerTupleOfInt();
        for(int v : data.getV()){
            int ncars = data.nCarsInConfig(v);
            gcc.put(v, Memory.TupleOfInt(ncars, ncars));
        }
        // NEW METHOD
        MDD result = MDDGCC.intersection(Memory.MDD(), newMDD, gcc);


        /*
        // OLD METHOD
        //ArrayOf<MDD> configs = configs();
        //MDD conf = Operation.intersection(Memory.MDD(), configs);
        MDD conf = MDDGCC.generate(Memory.MDD(), data.nCars(), gcc, null);
        //for(MDD config : configs) Memory.free(config);
        //Memory.free(configs);
        MDD result = Operation.intersection(mdd.MDD(), conf, newMDD);
        Memory.free(conf);

         */



        for(SetOf<Integer> set : values.values()) Memory.free(set);
        Memory.free(values);
        Memory.free(V0);
        Memory.free(V1);
        Memory.free(mdd);
        Memory.free(old_bindings);
        Memory.free(newMDD);

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

    private MDD option_relaxed(int idx, SetOf<Integer> V0, SetOf<Integer> V1){
        V0.clear(); V1.clear();

        for(int v : data.getV()){
            if(data.isOptionInConfig(idx, v)) V1.add(v);
            else V0.add(v);
        }

        ArrayOfInt B = Memory.ArrayOfInt(2);
        B.set(0,0); B.set(1,1);

        MDD seq = MDDBuilder.sequence(Memory.MDD(), data.seqSizeOption(idx), 0, data.seqMaxOption(idx), data.nCarsRelaxed());
        MDD sum = MDDBuilder.sum(Memory.MDD(), data.nOptionMin(idx), data.nOptionMax(idx), data.nCarsRelaxed(), B);
        MDD option = Operation.intersection(seq, sum);
        option.replace(V0, V1);

        Memory.free(seq);
        Memory.free(sum);
        Memory.free(B);

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

    private MDD config_relaxed(int idx, SetOf<Integer> V0, SetOf<Integer> V1){
        V0.clear(); V1.clear();
        for(int i = 0; i < data.getV().size(); i++){
            if (i == idx) V1.add(data.getV().get(i));
            else V0.add(data.getV().get(i));
        }

        ArrayOfInt B = Memory.ArrayOfInt(2);
        B.set(0,0); B.set(1,1);
        MDD config = MDDBuilder.sum(Memory.MDD(), data.nConfigMin(idx), data.nConfigMax(idx), data.nCars(), B);
        config.replace(V0, V1);
        Memory.free(B);
        return config;
    }
}
