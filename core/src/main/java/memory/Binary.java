package memory;

import structures.generics.SetOf;
import structures.arrays.ArrayOfInt;

public class Binary {

    private static final Binary memory = new Binary();

    private final ArrayOfInt ARRAY_BINARY;
    private final SetOf<Integer> SET_BINARY;

    private Binary(){
        ARRAY_BINARY = ArrayOfInt.create(2);
        ARRAY_BINARY.set(0,0); ARRAY_BINARY.set(1,1);

        SET_BINARY = Memory.SetOfInteger();
        SET_BINARY.add(0);
        SET_BINARY.add(1);
    }

    public static ArrayOfInt Array(){
        return memory.ARRAY_BINARY;
    }

    public static SetOf<Integer> Set(){
        return memory.SET_BINARY;
    }

}
