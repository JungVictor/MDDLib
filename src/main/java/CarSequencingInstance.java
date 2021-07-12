import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import problems.CarSequencing;
import utils.ArgumentParser;
import utils.Logger;

public class CarSequencingInstance {

    public static void information(String name, int nCars, int nOptions, int nConfigs, int solution){
        System.out.println("\r"+name);
        System.out.println("\r#Cars : "+nCars);
        System.out.println("\r#Options : "+nOptions);
        System.out.println("\r#Configurations : "+nConfigs);
        if(solution == 0) System.out.println("\rResult : no solution\n");
        if(solution == 1) System.out.println("\rResult : satisfiable\n");
        if(solution == -1) System.out.println("\rResult : unknown\n");
    }

    public static void main(String[] args) {

        // Default parameters
        ArgumentParser parser = new ArgumentParser("-mode", "default", "-instance", "1", "-options", "0 1");
        parser.read(args);

        // Mode
        int mode;
        switch (parser.get("-mode")) {
            case "legacy":
                mode = 1;
                System.out.println("\rSOLVER : LEGACY");
                break;
            case "constraint":
                mode = 2;
                System.out.println("\rSOLVER : FULL CONSTRAINT");
                break;
            default:
                mode = 0;
                System.out.println("\rSOLVER : DEFAULT");
        }


        // Instance number
        int instance = Integer.parseInt(parser.get("-instance"));

        // Options
        String[] opts = parser.get("-options").split(" ");
        System.out.print("OPTIONS : ");
        int[] options = new int[opts.length];
        for(int i = 0; i < options.length; i++) {
            System.out.print(opts[i]);
            System.out.print(" ");
            options[i] = Integer.parseInt(opts[i]);
        }
        System.out.println();

        CarSequencing cs;
        switch (instance) {
            case 1:
                cs = instance1(options);
                break;
            case 2:
                cs = instance2(options);
                break;
            case 3:
                cs = instance3(options);
                break;
            default:
                cs = instance0(options);
        }

        full(cs, mode);
    }

    //**************************************//
    //             RESOLUTION               //
    //**************************************//

    /**
     * Solve the CarSequencing problem.<br>
     * Begin with 2 options, then progressively add all options one by one
     */
    public static void full(CarSequencing cs, int mode){
        long clock = System.currentTimeMillis();
        Logger.out.print("Génération du MDDsol100 : en cours...\n");
        MDD solution = solve(cs, mode);
        Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs\n");

        solution = solve(cs, mode, solution, 2);
        Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.nSolutions() + " arcs\n");

        if(solution.nodes() > 1) {
            solution = solve(cs, mode, solution, 3);
            Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs\n");
        }

        if(solution.nodes() > 1) {
            solution = solve(cs, mode, solution, 4);
            Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs\n");
        }

        double nSol = solution.nSolutions();
        if(nSol > 0) Logger.out.print("Finished : " + nSol + " solutions");
        else Logger.out.print("No solution !");
    }

    private static MDD solve(CarSequencing cs, int mode){
        if(mode == 0) return cs.solve();
        return cs.solve_legacy();

    }

    private static MDD solve(CarSequencing cs, int mode, MDD mdd, int opt){
        if(mode == 0) return cs.solve(mdd, opt);
        return cs.solve_legacy(mdd, opt);
    }

    /**
     * Solve the CarSequencing problem by relaxing it to a smaller size,
     * then combine it to form a full size MDD.
     */
    public static void relaxed(){
        CarSequencing cs = instance2(0,1,2,3,4);

        long clock = System.currentTimeMillis();
        Logger.out.print("Génération du MDDsol10 : en cours...\n");
        MDD sol10 = cs.solve_relaxed();
        Logger.out.print("\rGénération du MDDsol10 : " + (System.currentTimeMillis() - clock) + "ms - " + sol10.nodes() + " noeuds / " + sol10.arcs() + " arcs\n");

        sol10.clearAllAssociations();

        MDD sol100 = sol10.copy();
        MDD prev = sol100;
        for(int i = 1; i < 10; i++) {
            sol100 = Operation.concatenate(sol100, sol10);
            Memory.free(prev);
            prev = sol100;
        }

        MDD solution = sol100.copy();
        prev = solution;
        for(int i = 1; i < 10; i++){
            solution = Operation.intersection(Memory.MDD(), solution, sol100, i, sol100.size(), sol100.size());
            Memory.free(prev);
            prev = solution;
        }

        System.out.println(solution.nSolutions());
    }



    //**************************************//
    //              INSTANCES               //
    //**************************************//
    // https://www.csplib.org/Problems/prob001/data/data.txt.html

    /**
     * <b>Problem 4/72  (Regin & Puget #1)</b><br>
     * Cars : 100<br>
     * Options : 5<br>
     * Configurations : 22<br>
     * Result : Satisfiable
     * @param options
     * @return Problem 4/72  (Regin & Puget #1)
     */
    private static CarSequencing instance0(int... options){
        information("INSTANCE 0 : Problem 4/72  (Regin & Puget #1)", 100, 5, 22, 1);
        return  new CarSequencing(
                new int[]{1, 2, 1, 2, 1},
                new int[]{2, 3, 3, 5, 5},
                new int[][]{
                        // 0 0
                        {4, 0, 0, 0, 0, 1},
                        {8, 0, 0, 0, 1, 0},
                        {3, 0, 0, 1, 0, 0},
                        {5, 0, 0, 1, 1, 0},
                        // 0 1
                        {15, 0, 1, 0, 0, 0},
                        {4, 0, 1, 0, 0, 1},
                        {8, 0, 1, 0, 1, 0},
                        {2, 0, 1, 1, 0, 0},
                        {1, 0, 1, 1, 1, 0},
                        // 1 0
                        {10, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 1},
                        {6, 1, 0, 0, 1, 0},
                        {3, 1, 0, 0, 1, 1},
                        {2, 1, 0, 1, 0, 0},
                        {1, 1, 0, 1, 0, 1},
                        {2, 1, 0, 1, 1, 0},
                        // 1 1
                        {4, 1, 1, 0, 0, 0},
                        {2, 1, 1, 0, 0, 1},
                        {6, 1, 1, 0, 1, 0},
                        {10, 1, 1, 1, 0, 0},
                        {1, 1, 1, 1, 0, 1},
                        {1, 1, 1, 1, 1, 1},
                }, options);
    }

    /**
     * <b>Problem 19/71  (Regin & Puget #4)</b><br>
     * Cars : 100<br>
     * Options : 5<br>
     * Configurations : 23<br>
     * Result : No solution
     * @param options
     * @return Problem 19/71  (Regin & Puget #4)
     */
    private static CarSequencing instance1(int... options){
        information("INSTANCE 1 : Problem 19/71  (Regin & Puget #4)", 100, 5, 23, 0);
        return  new CarSequencing(
                new int[]{1, 2, 1, 2, 1},
                new int[]{2, 3, 3, 5, 5},
                new int[][]{
                        // 0 0
                        {2, 0, 0, 0, 0, 1},
                        {4, 0, 0, 0, 1, 0},
                        {2, 0, 0, 0, 1, 1},
                        {4, 0, 0, 1, 0, 0},
                        {2, 0, 0, 1, 0, 1},
                        {1, 0, 0, 1, 1, 0},
                        // 0 1
                        {19, 0, 1, 0, 0, 0},
                        {4, 0, 1, 0, 0, 1},
                        {4, 0, 1, 0, 1, 0},
                        {4, 0, 1, 1, 0, 0},
                        {1, 0, 1, 1, 0, 1},
                        {5, 0, 1, 1, 1, 0},
                        // 1 0
                        {10, 1, 0, 0, 0, 0},
                        {1, 1, 0, 0, 0, 1},
                        {8, 1, 0, 0, 1, 0},
                        {2, 1, 0, 1, 1, 0},
                        // 1 1
                        {6, 1, 1, 0, 0, 0},
                        {1, 1, 1, 0, 0, 1},
                        {7, 1, 1, 0, 1, 0},
                        {1, 1, 1, 0, 1, 1},
                        {4, 1, 1, 1, 0, 0},
                        {3, 1, 1, 1, 0, 1},
                        {5, 1, 1, 1, 1, 0}
                }, options);
    }

    /**
     * <b>Problem 60-07</b><br>
     * Cars : 200<br>
     * Options : 5<br>
     * Configurations : 21<br>
     * Solution : Unknown
     * @param options
     * @return Problem 60-07
     */
    private static CarSequencing instance2(int... options){
        information("INSTANCE 2 : Problem 60-07", 200, 5, 21, -1);
        return  new CarSequencing(
                new int[]{1, 2, 1, 2, 1},
                new int[]{2, 3, 3, 5, 5},
                new int[][]{
                        // 0 0
                        {20, 0, 0, 0, 0, 1},
                        {28, 0, 0, 0, 1, 0},
                        {17, 0, 0, 1, 0, 0},
                        {1, 0, 0, 1, 1, 0},
                        // 0 1
                        {82, 0, 1, 0, 0, 0},
                        {3, 0, 1, 0, 0, 1},
                        {5, 0, 1, 0, 1, 0},
                        {5, 0, 1, 1, 0, 0},
                        {3, 0, 1, 1, 1, 0},
                        // 1 0
                        {12, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 1},
                        {2, 1, 0, 0, 1, 0},
                        {2, 1, 0, 1, 0, 0},
                        {1, 1, 0, 1, 0, 1},
                        {1, 1, 0, 1, 1, 0},
                        // 1 1
                        {5, 1, 1, 0, 0, 0},
                        {2, 1, 1, 0, 0, 1},
                        {2, 1, 1, 0, 1, 0},
                        {1, 1, 1, 0, 1, 1},
                        {4, 1, 1, 1, 0, 0},
                        {2, 1, 1, 1, 1, 0}
                }, options);
    }

    /**
     * <b>Problem 60-02</b><br>
     * Cars : 200<br>
     * Options : 5<br>
     * Configurations : 17<br>
     * Solution : Unknown
     * @param options
     * @return Problem 60-02
     */
    private static CarSequencing instance3(int... options){
        information("INSTANCE 3 : Problem 60-02", 200, 5, 17, -1);
        return  new CarSequencing(
                new int[]{1, 2, 1, 2, 1},
                new int[]{2, 3, 3, 5, 5},
                new int[][]{
                        // 0 0
                        {30, 0, 0, 0, 0, 1},
                        {54, 0, 0, 0, 1, 0},
                        {39, 0, 0, 1, 0, 0},
                        {2, 0, 0, 1, 1, 0},
                        // 0 1
                        {53, 0, 1, 0, 0, 0},
                        {1, 0, 1, 0, 0, 1},
                        {1, 0, 1, 1, 0, 0},
                        {1, 0, 1, 1, 0, 1},
                        // 1 0
                        {4, 1, 0, 0, 0, 0},
                        {2, 1, 0, 0, 0, 1},
                        {1, 1, 0, 0, 1, 1},
                        {3, 1, 0, 1, 0, 0},
                        {1, 1, 0, 1, 0, 1},
                        {2, 1, 0, 1, 1, 0},
                        // 1 1
                        {4, 1, 1, 0, 0, 0},
                        {1, 1, 1, 0, 1, 0},
                        {1, 1, 1, 1, 0, 0}
                }, options);
    }


}
