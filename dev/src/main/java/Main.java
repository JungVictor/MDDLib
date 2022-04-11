import csp.constraints.IntervalConstraintEqual;
import csp.constraints.IntervalConstraintSum;
import csp.IntervalConstraintsNetwork;
import csp.IntervalVariable;

public class Main {

    public static void main(String[] args) {

        // Main class when compiling dev
        // ...
        IntervalVariable a = IntervalVariable.create(0, 20);
        IntervalVariable b = IntervalVariable.create(1, 19);
        IntervalVariable c = IntervalVariable.create(2, 18);
        IntervalVariable d = IntervalVariable.create(3, 17);
        IntervalVariable e = IntervalVariable.create(4, 16);
        IntervalVariable f = IntervalVariable.create(5, 15);
        IntervalVariable g = IntervalVariable.create(6, 14);

        IntervalConstraintEqual c1 = IntervalConstraintEqual.create(a, b);
        IntervalConstraintEqual c2 = IntervalConstraintEqual.create(b, c);
        IntervalConstraintEqual c3 = IntervalConstraintEqual.create(c, d);
        IntervalConstraintEqual c4 = IntervalConstraintEqual.create(d, e);
        IntervalConstraintSum c8 = IntervalConstraintSum.create(a, g, f);

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
        System.out.println(f);
        System.out.println(g);

        IntervalConstraintsNetwork constraintNetwork = IntervalConstraintsNetwork.create();
        constraintNetwork.addConstraint(c1);
        constraintNetwork.addConstraint(c2);
        constraintNetwork.addConstraint(c3);
        constraintNetwork.addConstraint(c4);
        constraintNetwork.addConstraint(c8);
        constraintNetwork.resolve();

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
        System.out.println(f);
        System.out.println(g);
    }

}