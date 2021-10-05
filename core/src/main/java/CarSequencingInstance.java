import mdd.MDD;
import mdd.operations.Operation;
import memory.Memory;
import problems.CarSequencing;
import utils.ArgumentParser;
import utils.Logger;
import utils.carsequencing.CSDataParser;

public class CarSequencingInstance {

    public static void main(String[] args) {

        // Default parameters
        ArgumentParser parser = new ArgumentParser("-mode", "default", "-instance", "none", "-options", "0 1");
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
        //int instance = Integer.parseInt(parser.get("-instance"));
        String instance = parser.get("-instance");

        if(instance.equals("none")) {
            System.out.println("No instance selected !");
            return;
        }

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

        CarSequencing cs = CSDataParser.instance(instance, options);

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
    public static void relaxed(CarSequencing cs){

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
            solution = Operation.intersection(MDD.create(), solution, sol100, i, sol100.size(), sol100.size());
            Memory.free(prev);
            prev = solution;
        }

        System.out.println(solution.nSolutions());
    }


}
