package generation.utils;

import builder.constraints.ConstraintBuilder;

import structures.Domains;
import structures.arrays.ArrayOfInt;


import java.util.HashSet;
import java.util.Iterator;

public class MyConstraintBuilder extends ConstraintBuilder {
   /*** public static MDD foo(MDD mdd, int size, SuccessionRuleChupapaya s, boolean relaxation) {
        SNode snode = SNode.create();

        snode.setState(StateFoo.create());

        build(mdd, snode, s, size, relaxation);


        //Memory.free();
        int nP = mdd.nodes();
        int aP = mdd.arcs();
        mdd.reduce();
        float d = (float) aP / mdd.arcs();
        float d2 = (float) nP / mdd.nodes();

        miniBench.nodes_pre = nP;
        miniBench.nodes_post = mdd.nodes();
        miniBench.arcs_pre = aP;
        miniBench.arcs_post = mdd.arcs();
        //miniBench.f_reduction_arcs = aP/mdd.arcs();
        miniBench.f_reduction_node = nP / mdd.nodes();

        miniBench.n_solutions = mdd.nSolutions();

        System.out.println("\nCOMPRESSION ARCS:  " + String.format("%.2f", d));
        System.out.println("COMPRESSION NODES:  " + String.format("%.2f", d2));
        return mdd;
    }
    public static MDD universal(MDD mdd, int size, Domains s, boolean relaxation){
        SNode snode = SNode.create();
        snode.setState(WordUniversal.create());
        build(mdd, snode, s, size, relaxation);
        mdd.reduce();
        return mdd;
    }
    public static MDD box(MDD mdd, int size, Domains s, boolean relaxation) {
        SNode snode = SNode.create();

        snode.setState(GeomState.create());

        build(mdd, snode, s, size, relaxation);
        mdd.reduce();
        return mdd;
    }
    ***/
    public static Domains cons(Reverso reverso, int size){
        Domains resultat = Domains.create();
        for(int k=0;k<size;k++){
            resultat.add(k);
            for(Integer s : reverso.getIntToWord().keySet()){
                resultat.put(k, s);
            }
        }
        //System.out.println(resultat.get(1).size());
        return resultat;

    }
    public static ArrayOfInt consArrayFromLemset(HashSet<String> cleaned,Reverso reverso){
        int taille = cleaned.size();
        int i =0;
        ArrayOfInt resultat;
        if(!cleaned.contains("")){
            i=1;
            taille++;
            resultat = ArrayOfInt.create(taille);
            resultat.set(0,0);
        }
        else{
            resultat = ArrayOfInt.create(taille);
        }

        Iterator<String> it = cleaned.iterator();
        while(it.hasNext()){
            resultat.set(i,reverso.getWordToInt().get(it.next()));
            i++;
        }
        return resultat;
    }
    public static ArrayOfInt consArray(Reverso reverso, int size){


        int taille = reverso.getIntToWord().keySet().size() - 2;

        System.out.println(taille);
        int i = 0;
        int backn=1;
        if(reverso.getWordToInt().containsKey("\n")){
            backn=reverso.stringToInt("\n");
            taille--;
        }
        ArrayOfInt resultat = ArrayOfInt.create(taille);


        for(Integer s : reverso.getIntToWord().keySet()){

            if(s== 0|| s==backn || s==2 || s==1){
                System.out.println(s+ "pas ajouté");

            }
            else {
                resultat.set(i,s);
                i++;}
        }
        return resultat;
    }

}


