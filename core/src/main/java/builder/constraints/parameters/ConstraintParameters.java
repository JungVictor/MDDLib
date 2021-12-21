package builder.constraints.parameters;

import memory.Allocable;
import structures.generics.SetOf;

public abstract class ConstraintParameters implements Allocable {

    private final int allocatedIndex;
    private SetOf<Integer> variables;

    public ConstraintParameters(int allocatedIndex){
        this.allocatedIndex = allocatedIndex;
    }

    public void setVariables(SetOf<Integer> variables){
        this.variables = variables;
    }

    public boolean isVariable(int layer) {
        return variables == null || variables.contains(layer);
    }

    @Override
    public int allocatedIndex(){
        return allocatedIndex;
    }

    @Override
    public void free(){
        this.variables = null;
    }

}
