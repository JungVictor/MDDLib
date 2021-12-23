package problems;

import builder.MDDBuilder;
import dd.mdd.MDD;
import dd.mdd.components.Node;
import dd.operations.Operation;
import memory.Memory;
import structures.generics.MapOf;
import structures.generics.SetOf;

import java.util.Random;

public class AllDiff {

    public static MDD generate(MDD duplicate, int groupsize, int ngroups){
        MapOf<Integer, SetOf<Integer>> mapping = Memory.MapOfIntegerSetOfInteger();
        for(int i = 0; i < groupsize; i++) mapping.put(i, Memory.SetOfInteger());

        MDD groups = duplicate.copy(), oldgroups = groups;
        for(int i = 1; i < ngroups; i++) {
            groups = Operation.union(groups, replaceValues(duplicate, mapping, i, groupsize));
            Memory.free(oldgroups);
            oldgroups = groups;
        }

        groups.clearAllAssociations();
        MDD result = groups.copy();
        MDD old_result = result;

        for(int i = 1; i < ngroups; i++){
            result = Operation.concatenate(result, groups);
            Memory.free(old_result);
            old_result = result;
        }

        Memory.free(mapping);

        return result;
    }

    public static MDD universal(int groupsize, int ngroups){
        MDD universal = MDDBuilder.universal(MDD.create(), groupsize, groupsize);
        MDD result = generate(universal, groupsize, ngroups);
        Memory.free(universal);
        return result;
    }
    public static MDD alldiff(int groupsize, int ngroups){
        SetOf<Integer> V = Memory.SetOfInteger();
        for(int i = 0; i < groupsize; i++) V.add(i);
        MDD alldiff = MDDBuilder.alldiff(MDD.create(), V, groupsize);
        MDD result = generate(alldiff, groupsize, ngroups);
        Memory.free(alldiff);
        return result;
    }

    private static final Random random = new Random();
    public static int randomArcs(MDD mdd, int probability){
        if(probability == 0) return 0;
        int cpt = 0;
        for(int i = 0; i < mdd.size() - 1; i++){
            for(Node parent : mdd.getLayer(i)){
                for(Node child : mdd.getLayer(i+1)) {
                    if(parent.containsChild(child)) continue;
                    int rand = random.nextInt(100);
                    if(rand < probability) {
                        int value = child.getRandomParentValue(random);
                        if(!parent.containsLabel(value)) {
                            cpt++;
                            mdd.addArc(parent, value, child, i);
                        }
                    }
                }
            }
        }
        return cpt;
    }

    private static MDD replaceValues(MDD univ, MapOf<Integer, SetOf<Integer>> mapping, int iGroup, int sGroup){
        for(int value : mapping) mapping.get(value).add(value+iGroup*sGroup);
        MDD replaced = univ.copy();
        replaced.replace(mapping);
        for(int value : mapping) mapping.get(value).clear();
        return replaced;
    }

}
