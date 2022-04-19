package generation.states;

import builder.constraints.states.NodeState;
import generation.utils.Reverso;
import memory.AllocatorOf;
import structures.arrays.ArrayOfInt;


import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;

public class StateGram extends NodeState {
    // Thread safe allocator
    private final static ThreadLocal<Allocator> localStorage = ThreadLocal.withInitial(Allocator::new);

    public ArrayOfInt ngram;
    public static Reverso reverso;
    public static StringBuilder sb = new StringBuilder(100);
    public static int SIZE = 5;

    private static Allocator allocator(){
        return localStorage.get();
    }

    private StateGram(int allocatedIndex) {
        super(allocatedIndex);
    }

    /****
     * Initialise un état ngram rempli de mot de debut de phrase.
     */
    public void init(){
        this.ngram = ArrayOfInt.create(SIZE);
        fill(1);
        // this.sum=0;
    }

    /****
     * Creer un état stateGram dans l'allocator
     * @return new StateGram
     */
    public static StateGram create(){
        StateGram object = allocator().allocate();
        object.init();
        return object;
    }

    /***
     * Effectue une permutation circulaire : n -> n-1 sur les mots du n-gram parent.
     * @param parent le ngram de l'etat parent
     * @return Un stateGram pret à recevoir le label de la couche k+1 en position n.
     */
    public ArrayOfInt rotate(ArrayOfInt parent){
        for(int i=1; i<parent.length;i++){
            this.ngram.set(i-1, parent.get(i));
        }
        return ngram;
    }

    /***
     *  Surchage de la methode de nodestate afin de construire un StateGram cohérent en fonction du contexte.
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @return
     */
    @Override
    public NodeState createState(int label, int layer, int size) {
        StateGram state = StateGram.create(); // call parameters
        sb.setLength(0);
        state.compose(this,label, layer); //  how to go from current state to newxt states.
        return state;
    }

    /***
     * à partir d'un état parent, un label, le layer, construit le stateGram suivant.
     * @param parent le StateGram parent
     * @param label Label de l'arc
     * @param layer
     */
    public void compose(StateGram parent, int label, int layer){
        rotate(parent.ngram);
        ngram.set(ngram.length - 1, label);
    }

    public String toString(){

        return "\n"+ngram.get(0)+":"+ngram.get(1)+":"+ngram.get(2);
    }
    /***
     * fill the ngram array with word having value as integer value
     * @param value integer value of a word in Reverso
     */
    public void fill(int value){
        for(int i=0;i<ngram.length;i++){
            this.ngram.set(i,value);
        }
    }
    public String ToKey(int start) {
        sb.setLength(0);
        for (int i = start; i < ngram.length - 1; i++) {
            sb.append(ngram.get(0));
            sb.append(":");
        }
        sb.append(ngram.get(ngram.length - 1));
        return sb.toString();
    }
    public boolean IsNgramAllEqualV(int v){
        boolean b = true;
        int pos = 0;
        while(b && pos < ngram.length){
            if(ngram.get(pos)==v){
                pos++;
            }
            else{
                return false;
            }
        }
        return true;
    }
    public String test(int label,int layer){
        sb.setLength(0);
        assert ngram.length < layer;
        for(int i=ngram.length-(layer-1);i< ngram.length;i++){
            sb.append(ngram.get(i));
            sb.append(";");
        }
        sb.append(label);
        return sb.toString();
    }

    /***
     * Build signature of a state from n-1 last word and the current label
     *
     * @param label le label de l'arc
     * @return Une String w1:w2:w3:...:wn:label
     */
    public String toKeyWithLabel(int label){
        sb.setLength(0);
        for (int i = 1; i < ngram.length; i++) {
            sb.append(ngram.get(i));
            sb.append(":");
        }
        sb.append(label);
        return sb.toString();
    }

    /***
     * Cette methode verifie que le ngram de l'etat courrant appartient aux dictionnaire des ngrams
     * @param label
     * @param layer
     * @param size
     * @return
     */
    public boolean isValidNgram(int label,int layer,int size){

        return reverso.isValid.contains(this.toKeyWithLabel(label));

    }
    /***
     * Cette methode verifie que le ngram de l'etat courrant est une fin de phrase.
     *  la valeur 2 est associé au symbole de fin de phrase "@".
     * @param label
     * @param layer
     * @param size
     * @return
     */
    public boolean isValidEnd (int label, int layer, int size){
        return reverso.isValid.contains(this.toKeyWithLabel(2));
    }

    /***
     *  Verifie que l'etat est valide.
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @return
     */
    @Override
    public boolean isValid(int label, int layer, int size) {

        if (label == 0 && layer >= 9) {
            if (reverso.isValid.contains(toKeyWithLabel(2))) {// && reverso.isTerminal(ngram.get(3))) {
                //System.out.println("j'accepte un mot vide");
                return true;
            }
            if (ngram.get(ngram.length - 1) == 0) {
                return true;
            }
        }
        if (label == 2 || label == 1) return false; // si le mot courant est un marqueur de debut ou de fin;

        if (layer == size - 1) {
            return isValidEnd(label, layer, size) && isValidNgram(label, layer, size);
        }

        if (isValidNgram(label, layer, size)) {
            //System.out.println(layer);
            return true;

        }
        return false;
    }

    /***
     * La signature n-1 derniers mots du ngram précedant et le label
     * @param label Label of the arc
     * @param layer The index of the <b>PARENT</b>'s layer
     * @param size The total size of the DD
     * @return w1:w2:w3:...:wn
     */
    @Override
    public String signature(int label, int layer, int size){
        //System.out.println(toKeyWithLabel(label));
        return  toKeyWithLabel(label);
    }


    public double proba(int label) {
        ArrayOfInt history = this.ngram;
        if (label == 0) return 1;
        for (int i = 1; i < history.length() - 1; i++) {
            if (history.get(i) != 0) {
                sb.append(reverso.IntToWord(history.get(i)));

                sb.append(" ");
            }
        }
        sb.append(reverso.IntToWord(history.get(history.length - 1)));

        return prob(reverso.IntToWord(label), sb.toString());
    }
    public static double prob(String word, String history) {

        String numerateur = history.concat(" ").concat(word);
        String denominateur = history;



        int valeurNum;
        int valeurDeno;
        try{
            valeurNum = reverso.getCount(numerateur);
        }
        catch (Exception e){

            //System.out.println("\n"+numerateur);
            valeurNum = 0;
        }
        try{
            valeurDeno =reverso.getCount(denominateur);
        }
        catch (Exception e){

            //System.out.println(denominateur);
            return (double) 0;
        }
        double d = (double) valeurNum / (double) valeurDeno;
        //  a   if(Double.isInfinite(d)) return 0;
        System.out.println("\n Methode porba : WORD, HISTORY");
        System.out.println("WORD ="+word+" HISTORY="+history);
        System.out.println("la probabilité ="+d);
        System.out.println(word);
        return d;

    }


    /***
     * Libere l'objet dans l'allocator
     */
    @Override
    public void free(){
        //this.constraint = null;
        this.ngram.free();
        allocator().free(this);
    }

    static final class Allocator extends AllocatorOf<StateGram> {

        Allocator(int capacity) {
            super.init(capacity);
        }

        Allocator(){
            super.init();
        }

        @Override
        protected StateGram[] arrayCreation(int capacity) {
            return new StateGram[capacity];
        }

        @Override
        protected StateGram createObject(int index) { return new StateGram(index);
        }

    }
}
