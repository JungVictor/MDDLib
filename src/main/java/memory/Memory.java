package memory;

import mdd.MDD;
import mdd.components.InArcs;
import mdd.components.Layer;
import mdd.components.Node;
import mdd.components.OutArcs;
import mdd.operations.Pack;
import pmdd.PMDD;
import pmdd.components.PNode;
import structures.booleans.ArrayOfBoolean;
import structures.generics.ArrayOf;
import structures.Binder;
import structures.generics.ListOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;

public class Memory {

    public static void free(MemoryObject memoryObject){
        memoryObject.free();
    }


    //**************************************//
    //                SETS                  //
    //**************************************//

    private static final MemoryPool<SetOf<Node>> setOfNodes = new MemoryPool<>();
    public static SetOf<Node> SetOfNode(){
        SetOf<Node> object = setOfNodes.get();
        if(object == null) {
            object = new SetOf<>(setOfNodes);
            setOfNodes.add(object);
        }
        object.prepare();
        return object;

    }

    private static final MemoryPool<SetOf<Integer>> setOfIntegers = new MemoryPool<>();
    public static SetOf<Integer> SetOfInteger(){
        SetOf<Integer> object = setOfIntegers.get();
        if(object == null){
            object = new SetOf<>(setOfIntegers);
            setOfIntegers.add(object);
        }
        object.prepare();
        return object;
    }


    //**************************************//
    //               ARRAYS                 //
    //**************************************//

    private static final MemoryPool<ArrayOf<Node>> arrayOfNodes = new MemoryPool<>();
    public static ArrayOf<Node> ArrayOfNode(int capacity){
        ArrayOf<Node> object = arrayOfNodes.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfNodes, capacity);
            arrayOfNodes.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

    private static final MemoryPool<ArrayOfInt> arrayOfInts = new MemoryPool<>();
    public static ArrayOfInt ArrayOfInt(int capacity){
        ArrayOfInt object = arrayOfInts.get();
        if(object == null){
            object = new ArrayOfInt(arrayOfInts, capacity);
            arrayOfInts.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }


    private static final MemoryPool<MatrixOfInt> matrixOfInts = new MemoryPool<>();
    public static MatrixOfInt MatrixOfInt(int height, int length){
        MatrixOfInt object = matrixOfInts.get();
        if(object == null){
            object = new MatrixOfInt(matrixOfInts, height, length);
            matrixOfInts.add(object);
        } else object.setSize(height, length);
        object.prepare();
        return object;
    }


    private static final MemoryPool<ArrayOfBoolean> arrayOfBooleans = new MemoryPool<>();
    public static ArrayOfBoolean ArrayOfBoolean(int capacity){
        ArrayOfBoolean object = arrayOfBooleans.get();
        if(object == null){
            object = new ArrayOfBoolean(arrayOfBooleans, capacity);
            arrayOfBooleans.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }


    //**************************************//
    //                LISTS                 //
    //**************************************//

    private static final MemoryPool<ListOf<Layer>> listOfLayers = new MemoryPool<>();
    public static ListOf<Layer> ListOfLayer(){
        ListOf<Layer> object = listOfLayers.get();
        if(object == null){
            object = new ListOf<>(listOfLayers);
            listOfLayers.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<ListOf<Node>> listOfNodes = new MemoryPool<>();
    public static ListOf<Node> ListOfNode(){
        ListOf<Node> object = listOfNodes.get();
        if(object == null){
            object = new ListOf<>(listOfNodes);
            listOfNodes.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<ListOf<Integer>> listOfIntegers = new MemoryPool<>();
    public static ListOf<Integer> ListOfInteger(){
        ListOf<Integer> object = listOfIntegers.get();
        if(object == null){
            object = new ListOf<>(listOfIntegers);
            listOfIntegers.add(object);
        }
        object.prepare();
        return object;
    }


    //**************************************//
    //            ATOMIC OBJECTS            //
    //**************************************//

    private static final MemoryPool<Pack> packs = new MemoryPool<>();
    public static Pack Pack(int pos, int l, Layer L){
        Pack object = packs.get();
        if(object == null){
            object = new Pack(packs);
            packs.add(object);
        }
        object.prepare();
        object.init(pos, l, L);
        return object;
    }

    private static final MemoryPool<Binder> binders = new MemoryPool<>();
    public static Binder Binder(){
        Binder object = binders.get();
        if(object == null){
            object = new Binder(binders);
            binders.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<Node> nodes = new MemoryPool<>();
    public static Node Node(){
        Node object = nodes.get();
        if(object == null){
            object = new Node(nodes);
            nodes.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<InArcs> inArcs = new MemoryPool<>();
    public static InArcs InArcs(){
        InArcs object = inArcs.get();
        if(object == null){
            object = new InArcs(inArcs);
            inArcs.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<OutArcs> outArcs = new MemoryPool<>();
    public static OutArcs OutArcs(){
        OutArcs object = outArcs.get();
        if(object == null){
            object = new OutArcs(outArcs);
            outArcs.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MDD> mdds = new MemoryPool<>();
    public static MDD MDD(){
        MDD object = mdds.get();
        if(object == null){
            object = new MDD(mdds);
            mdds.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<Layer> layers = new MemoryPool<>();
    public static Layer Layer(){
        Layer object = layers.get();
        if(object == null){
            object = new Layer(layers);
            layers.add(object);
        }
        object.prepare();
        return object;
    }
}
