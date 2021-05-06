import builder.MDDBuilder;
import builder.constraints.MDDAllDifferent;
import mdd.MDD;
import mdd.operations.ConstraintOperation;
import memory.Memory;
import pmdd.PMDD;
import pmdd.components.properties.NodeProperty;
import pmdd.memory.PMemory;
import problems.AllDiff;
import problems.AllDiffKN;
import problems.CarSequencing;
import structures.generics.MapOf;
import structures.integers.ArrayOfInt;
import structures.integers.TupleOfInt;
import utils.Logger;
import utils.MDDReader;

public class Main {

    public static void main(String[] args) {
        CarSequencing cs = new CarSequencing(
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
                }, 0, 1);

        MDD load = MDDReader.load(Memory.MDD(), "CarSeq012.mdd");

        /*
        long clock = System.currentTimeMillis();
        System.out.println("Génération du MDDsol100 : en cours...");
        MDD solution = cs.solve();
        Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs\n");
        solution = cs.solve(solution, 2);
        MDDReader.save(solution, "CarSeq012");

        Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs\n");
        solution = cs.solve(solution, 3);
        Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs\n");
        solution = cs.solve(solution, 4);
        Logger.out.print("\rGénération du MDDsol100 : " + (System.currentTimeMillis() - clock) + "ms - " + solution.nodes() + " noeuds / " + solution.arcs() + " arcs\n");
*/
        System.out.println("finished");

    }

}
