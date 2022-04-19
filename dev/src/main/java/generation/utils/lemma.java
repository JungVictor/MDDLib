package generation.utils;

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;

import java.util.HashSet;
import java.util.Iterator;


public class lemma {

    public static HashSet<String> clean;


    public static void main(String[] args) throws Exception
    {
        // Point TT4J to the TreeTagger installation directory. The executable is expected
        // in the "bin" subdirectory - in this example at "/opt/treetagger/bin/tree-tagger"
        System.setProperty("treetagger.home", "/opt/treetagger");
        TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
        try {
            tt.setModel("/opt/treetagger/lib/french.par");
            tt.setHandler(new TokenHandler<String>()
            {
                public void token(String token, String pos, String lemma)
                {
                    System.out.println(token +"\t" +lemma);
                }
            });
            //tt.process(new String[] { "Emilie", "est", "née", "à", "Paris","." });

            tt.process(new String[] { "J", "aimerais", "née", "à", "Paris","." });
        }
        finally {
            tt.destroy();
        }

    }

    public static String[][] go(String[] s) throws Exception
    {
        // Point TT4J to the TreeTagger installation directory. The executable is expected
        // in the "bin" subdirectory - in this example at "/opt/treetagger/bin/tree-tagger"
        final String[][] resultat = new String[s.length][2];
        final int[] i = {0};
        System.setProperty("treetagger.home", "/opt/treetagger");
        TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
        try {
            tt.setModel("/opt/treetagger/lib/french.par");
            tt.setHandler(new TokenHandler<String>()
            {
                public void token(String token, String pos, String lemma)
                {
                    //System.out.println(token +"\t" +lemma);
                    resultat[i[0]][0] = lemma;
                    resultat[i[0]][1] = token;
                    i[0]++;
                }
            });
            tt.process(s);
        }
        finally {
            tt.destroy();
        }
        return resultat;
    }
    public static HashSet<String> clean(HashSet<String> lemset, Reverso reverso) throws Exception {
        // penser à split sur l'apostrophe check si les deux appartiennent et si oui ajouter le mot avec apostrophe.
        clean = new HashSet<String>();
        String[] toto = new String[reverso.getWordToInt().keySet().size()];
        String[] delay = new String[reverso.getWordToInt().keySet().size()*2];
        Iterator<String> it = reverso.getWordToInt().keySet().iterator();
        int j = 0;
        while(it.hasNext()){
            toto[j]=it.next();

            if(toto[j].contains("'")){
                j++;
                //System.out.println();
                //delay[j]=toto[j].split("'")[0];
                //delay[j+1]=toto[j].split("'")[1];
                j--;
            }
            j++;
        }
        String[][] cleand = go(toto);
        for(int i=0; i< cleand.length ;i++ ){

            if(lemset.contains(cleand[i][0])){
                //System.out.println(cleand[i][1]);
                clean.add(cleand[i][1]);
            }
            else{
                //System.out.println(cleand[i][1]);
                if(cleand[i][1]!=null){
                    if(cleand[i][1].contains("'")){
                        clean.add(cleand[i][1]);
                        //System.out.println(cleand[i][1]);
                    }
                }

            }
        }
        System.out.println(reverso.getIntToWord().size());
        System.out.println(clean.size());
        HashSet<String> foo= new HashSet<>();

        return clean;
    }

    public static String[][] getPOS(String[] s) throws Exception
    {
        // Point TT4J to the TreeTagger installation directory. The executable is expected
        // in the "bin" subdirectory - in this example at "/opt/treetagger/bin/tree-tagger"
        final String[][] resultat = new String[s.length][2];
        final int[] i = {0};
        System.setProperty("treetagger.home", "/opt/treetagger");
        TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
        try {
            tt.setModel("/opt/treetagger/lib/french.par");
            tt.setHandler(new TokenHandler<String>()
            {
                public void token(String token, String pos, String lemma)
                {
                    //System.out.println(token +"\t" +lemma);
                    resultat[i[0]][0] = pos;
                    resultat[i[0]][1] = token;
                    i[0]++;
                }
            });
            tt.process(s);
        }
        finally {
            tt.destroy();
        }
        return resultat;
    }


    public static void fillIntToPOS (Reverso reverso) throws Exception {
        String[] toto = new String[reverso.getWordToInt().keySet().size()];
        Iterator<String> it = reverso.getWordToInt().keySet().iterator();
        int j = 0;
        while(it.hasNext()){
            toto[j]=it.next();

            if(toto[j].contains("'")){
                j++;
                System.out.println();
                //delay[j]=toto[j].split("'")[0];
                //delay[j+1]=toto[j].split("'")[1];
                j--;
            }
            j++;
        }
        String[][] cleand = getPOS(toto); //pos,token
        for(int i=0; i< cleand.length ;i++ ){
            if(cleand[i][1]==null){

            }
            else if(cleand[i][1].equals("@")){
                reverso.getIntToPOS().put(2,"END");
            }
            else if(cleand[i][1].equals("#")){
                reverso.getIntToPOS().put(1,"START");
            }
            else if(cleand[i][1].equals(" ")){
            }
            else {
                reverso.getIntToPOS().put(reverso.stringToInt(cleand[i][1]), cleand[i][0]);
            }
        }
        reverso.getIntToPOS().put(0,"JOKER");
        System.out.println(reverso.getIntToPOS());
    }

    }

