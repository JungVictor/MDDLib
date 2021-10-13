package problems;

import builder.MDDBuilder;
import builder.constraints.ConstraintBuilder;
import builder.constraints.MDDGCC;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import mdd.operations.Operation;
import memory.Memory;
import memory.Binary;
import pmdd.PMDD;
import problems.carsequencing.CarSequencingData;
import structures.arrays.ArrayOfMDD;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.tuples.TupleOfInt;
import utils.Logger;

public class CarSequencing {

    private final CarSequencingData data;

    public CarSequencing(int[] min, int[] max, int[][] options, int... enabledOptions){
        data = new CarSequencingData(min, max, options, enabledOptions);
    }

    public CarSequencing(int[] min, int[] max, int[][] options){
        data = new CarSequencingData(min, max, options);
    }


    //**************************************//
    //               SOLVE                  //
    //**************************************//

    public MDD solve(){
        data.generate();

        // Options
        ArrayOfMDD options = options();
        MDD opts = Operation.intersection(MDD.create(), options);
        for(MDD mdd : options) Memory.free(mdd);
        Memory.free(options);

        // Configurations
        MapOf<Integer, TupleOfInt> gcc = Memory.MapOfIntegerTupleOfInt();
        for(int v : data.getV()){
            int ncars = data.nCarsInConfig(v);
            gcc.put(v, TupleOfInt.create(ncars, ncars));
        }
        //MDD solution = MDDGCC.intersection(MDD.create(), opts, gcc);
        MDD solution = ConstraintOperation.gcc(MDD.create(), opts, gcc);
        Memory.free(opts);
        solution.reduce();

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

        // Replacing values
        Logger.out.information("\rReplacing values...");
        mdd.replace(values);
        Logger.out.information("\rReplacing values done");

        // New option
        MDD new_option = option(data.nOptions()-1, V0, V1);
        Logger.out.information("\rCarSequencing : Adding new option sequence\n");
        MDD newMDD = Operation.intersection(mdd, new_option);
        Memory.free(new_option);

        // New configurations
        Logger.out.information("\rCarSequencing : Adding GCC\n");
        MapOf<Integer, TupleOfInt> gcc = Memory.MapOfIntegerTupleOfInt();
        for(int v : data.getV()){
            int ncars = data.nCarsInConfig(v);
            gcc.put(v, TupleOfInt.create(ncars, ncars));
        }
        MDD result = MDDGCC.intersection(MDD.create(), newMDD, gcc);


        // Freeing the memory
        for(SetOf<Integer> set : values.values()) Memory.free(set);
        Memory.free(values);
        Memory.free(V0);
        Memory.free(V1);
        Memory.free(mdd);
        Memory.free(old_bindings);
        Memory.free(newMDD);

        return result;
    }

    private ArrayOfMDD options(){
        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();
        ArrayOfMDD options = ArrayOfMDD.create(data.nOptions());
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

        MDD seq = MDDBuilder.sequence(MDD.create(), data.seqSizeOption(i), 0, data.seqMaxOption(i), data.nCars());
        MDD sum = ConstraintBuilder.sum(MDD.create(), Binary.Set(), data.nCarsWithOption(i), data.nCarsWithOption(i), data.nCars());

        MDD option = Operation.intersection(seq, sum);
        option.replace(V0, V1);

        Memory.free(seq);
        Memory.free(sum);

        return option;
    }


    //**************************************//
    //            SOLVE LEGACY              //
    //**************************************//

    public MDD solve_legacy(){
        data.generate();

        // Options
        ArrayOfMDD options = options();
        MDD opts = Operation.intersection(MDD.create(), options);

        for(MDD mdd : options) Memory.free(mdd);
        Memory.free(options);

        // Configurations
        MapOf<Integer, TupleOfInt> gcc = Memory.MapOfIntegerTupleOfInt();
        for(int v : data.getV()){
            int ncars = data.nCarsInConfig(v);
            gcc.put(v, TupleOfInt.create(ncars, ncars));
        }
        MDD conf = MDDGCC.generate(MDD.create(), data.nCars()+1, gcc, null);
        MDD solution = Operation.intersection(PMDD.create(), opts, conf);
        Memory.free(conf);
        Memory.free(opts);
        solution.reduce();

        return solution;
    }

    public MDD solve_legacy(MDD mdd, int option){
        // The mdd is a relaxed solution
        SetOf<Integer> V0 = Memory.SetOfInteger(), V1 = Memory.SetOfInteger();

        MapOf<Integer, Integer> old_bindings = Memory.MapOfIntegerInteger(), bindings = data.getBinding();
        for(int key : bindings) old_bindings.put(key, bindings.get(key));

        data.addOptions(option);
        data.generate();


        // Replacing values
        MapOf<Integer, SetOf<Integer>> values = Memory.MapOfIntegerSetOfInteger();
        for(int key : bindings){
            if(!values.contains(old_bindings.get(key))) values.put(old_bindings.get(key), Memory.SetOfInteger());
            values.get(old_bindings.get(key)).add(bindings.get(key));
        }
        Memory.free(old_bindings);

        Logger.out.information("\rReplacing values...");
        mdd.replace(values);
        Logger.out.information("\rReplacing values done");

        for(SetOf<Integer> set : values.values()) Memory.free(set);
        Memory.free(values);


        // New option
        MDD new_option = option(data.nOptions()-1, V0, V1);
        Logger.out.information("\rCarSequencing : Adding new option sequence\n");
        MDD newMDD = Operation.intersection(mdd, new_option);
        Memory.free(mdd);
        Memory.free(new_option);
        Memory.free(V0);
        Memory.free(V1);


        // New configurations
        Logger.out.information("\rCarSequencing : Adding GCC\n");
        MapOf<Integer, TupleOfInt> gcc = Memory.MapOfIntegerTupleOfInt();
        for(int v : data.getV()){
            int ncars = data.nCarsInConfig(v);
            gcc.put(v, TupleOfInt.create(ncars, ncars));
        }
        MDD conf = MDDGCC.generate(MDD.create(), data.nCars(), gcc, null);
        MDD result = Operation.intersection(newMDD.MDD(), conf, newMDD);
        Memory.free(conf);
        Memory.free(newMDD);

        return result;
    }


    //**************************************//
    //            SOLVE RELAXED             //
    //**************************************//

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

    private MDD option_relaxed(int idx, SetOf<Integer> V0, SetOf<Integer> V1){
        V0.clear(); V1.clear();

        for(int v : data.getV()){
            if(data.isOptionInConfig(idx, v)) V1.add(v);
            else V0.add(v);
        }

        MDD seq = MDDBuilder.sequence(MDD.create(), data.seqSizeOption(idx), 0, data.seqMaxOption(idx), data.nCarsRelaxed());
        MDD sum = MDDBuilder.sum(MDD.create(), data.nOptionMin(idx), data.nOptionMax(idx), data.nCarsRelaxed(), Binary.Set());
        MDD option = Operation.intersection(seq, sum);
        option.replace(V0, V1);

        Memory.free(seq);
        Memory.free(sum);

        return option;
    }

    private MDD config_relaxed(int idx, SetOf<Integer> V0, SetOf<Integer> V1){
        V0.clear(); V1.clear();
        for(int i = 0; i < data.getV().size(); i++){
            if (i == idx) V1.add(data.getV().get(i));
            else V0.add(data.getV().get(i));
        }

        MDD config = MDDBuilder.sum(MDD.create(), data.nConfigMin(idx), data.nConfigMax(idx), data.nCars(), Binary.Set());
        config.replace(V0, V1);
        return config;
    }

}
