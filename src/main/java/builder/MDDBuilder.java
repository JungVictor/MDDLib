package builder;

import builder.constraints.MDDAmong;
import mdd.MDD;

public class MDDBuilder {

    public static MDD among(MDD mdd, int q, int min, int max){
        return MDDAmong.generate(mdd, q, min, max);
    }

}
