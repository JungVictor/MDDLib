package memory;

import mdd.MDD;
import mdd.components.InArcs;
import mdd.components.Layer;
import mdd.components.Node;
import mdd.components.OutArcs;
import mdd.operations.Pack;
import structures.booleans.ArrayOfBoolean;
import structures.generics.ArrayOf;
import structures.Binder;
import structures.generics.ListOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;
import structures.integers.TupleOfInt;

public class Memory {

    public static void free(MemoryObject memoryObject){
        memoryObject.free();
    }


    private static final MemoryPool<TupleOfInt> tupleOfIntPool = new MemoryPool<>();
    public static TupleOfInt TupleOfInt(){
        return TupleOfInt(0,0);
    }

    public static TupleOfInt TupleOfInt(int e1, int e2){
        TupleOfInt object = tupleOfIntPool.get();
        if(object == null){
            object = new TupleOfInt(tupleOfIntPool);
            tupleOfIntPool.add(object);
        }
        object.prepare();
        object.set(e1, e2);
        return object;
    }

    public static TupleOfInt TupleOfInt(TupleOfInt tuple){
        return TupleOfInt(tuple.getFirst(), tuple.getSecond());
    }


    //**************************************//
    //                MAPS                  //
    //**************************************//

    private static final MemoryPool<MapOf<Integer, ListOf<Integer>>> mapOfIntegersListOfIntegers = new MemoryPool<>();
    public static MapOf<Integer, ListOf<Integer>> MapOfIntegerListOfInteger(){
        MapOf<Integer, ListOf<Integer>> object = mapOfIntegersListOfIntegers.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegersListOfIntegers);
            mapOfIntegersListOfIntegers.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, Integer>> mapOfIntegersIntegers = new MemoryPool<>();
    public static MapOf<Integer, Integer> MapOfIntegerInteger(){
        MapOf<Integer, Integer> object = mapOfIntegersIntegers.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegersIntegers);
            mapOfIntegersIntegers.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, Node>> mapOfIntegersNodes = new MemoryPool<>();
    public static MapOf<Integer, Node> MapOfIntegerNode(){
        MapOf<Integer, Node> object = mapOfIntegersNodes.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegersNodes);
            mapOfIntegersNodes.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, SetOf<Integer>>> mapOfIntegersSetOfIntegers = new MemoryPool<>();
    public static MapOf<Integer, SetOf<Integer>> MapOfIntegerSetOfInteger(){
        MapOf<Integer, SetOf<Integer>> object = mapOfIntegersSetOfIntegers.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegersSetOfIntegers);
            mapOfIntegersSetOfIntegers.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Node, Integer>> mapOfNodeIntegerPool = new MemoryPool<>();
    public static MapOf<Node, Integer> MapOfNodeInteger(){
        MapOf<Node, Integer> object = mapOfNodeIntegerPool.get();
        if(object == null){
            object = new MapOf<>(mapOfNodeIntegerPool);
            mapOfNodeIntegerPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, TupleOfInt>> mapOfIntegerTupleOfIntPool = new MemoryPool<>();
    public static MapOf<Integer, TupleOfInt> MapOfIntegerTupleOfInt(){
        MapOf<Integer, TupleOfInt> object = mapOfIntegerTupleOfIntPool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerTupleOfIntPool);
            mapOfIntegerTupleOfIntPool.add(object);
        }
        object.prepare();
        return object;
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

    private static final MemoryPool<ArrayOf<MDD>> arrayOfMdds = new MemoryPool<>();
    public static ArrayOf<MDD> ArrayOfMDD(int capacity){
        ArrayOf<MDD> object = arrayOfMdds.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfMdds, capacity);
            arrayOfMdds.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

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

    public static final MemoryPool<MDD> mdds = new MemoryPool<>();
    public static MDD MDD(){
        return MDD(Node());
    }

    public static MDD MDD(Node node){
        MDD object = mdds.get();
        if(object == null){
            object = new MDD(mdds);
            mdds.add(object);
        }
        object.prepare();
        object.setRoot(node);
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
