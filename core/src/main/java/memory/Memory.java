package memory;

import dd.bdd.components.BinaryNode;
import dd.AbstractNode;
import dd.interfaces.NodeInterface;
import dd.interfaces.StateNodeInterface;
import dd.mdd.components.*;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.generics.SetOfNode;
import structures.tuples.TupleOfInt;
import structures.lists.ListOfInt;

/**
 * <b>The class that allocate and manage all objects in memory.</b>
 */
public class Memory {

    /**
     * Free the object
     * @param freeable The object to free
     */
    public static void free(Freeable freeable){
        freeable.free();
    }

    //**************************************//
    //                MAPS                  //
    //**************************************//

    private static final MemoryPool<MapOf<Integer, ListOfInt>> mapOfIntegerListOfIntegerPool = new MemoryPool<>();
    public static MapOf<Integer, ListOfInt> MapOfIntegerListOfInt(){
        MapOf<Integer, ListOfInt> object = mapOfIntegerListOfIntegerPool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerListOfIntegerPool);
            mapOfIntegerListOfIntegerPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, Integer>> mapOfIntegerIntegerPool = new MemoryPool<>();
    public static MapOf<Integer, Integer> MapOfIntegerInteger(){
        MapOf<Integer, Integer> object = mapOfIntegerIntegerPool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerIntegerPool);
            mapOfIntegerIntegerPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, Long>> mapOfIntegerLongPool = new MemoryPool<>();
    public static MapOf<Integer, Long> MapOfIntegerLong(){
        MapOf<Integer, Long> object = mapOfIntegerLongPool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerLongPool);
            mapOfIntegerLongPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, Node>> mapOfIntegerNodePool = new MemoryPool<>();
    public static MapOf<Integer, Node> MapOfIntegerNode(){
        MapOf<Integer, Node> object = mapOfIntegerNodePool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerNodePool);
            mapOfIntegerNodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, SetOf<Integer>>> mapOfIntegerSetOfIntegerPool = new MemoryPool<>();
    public static MapOf<Integer, SetOf<Integer>> MapOfIntegerSetOfInteger(){
        MapOf<Integer, SetOf<Integer>> object = mapOfIntegerSetOfIntegerPool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerSetOfIntegerPool);
            mapOfIntegerSetOfIntegerPool.add(object);
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

    private static final MemoryPool<MapOf<Integer, Double>> mapOfIntegerDoublePool = new MemoryPool<>();
    public static MapOf<Integer, Double> MapOfIntegerDouble(){
        MapOf<Integer, Double> object = mapOfIntegerDoublePool.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerDoublePool);
            mapOfIntegerDoublePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Node, Double>> mapOfNodeDoublePool = new MemoryPool<>();
    public static MapOf<Node, Double> MapOfNodeDouble(){
        MapOf<Node, Double> object = mapOfNodeDoublePool.get();
        if(object == null){
            object = new MapOf<>(mapOfNodeDoublePool);
            mapOfNodeDoublePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<NodeInterface, Double>> mapOfNodeInterfaceDoublePool = new MemoryPool<>();
    public static MapOf<NodeInterface, Double> MapOfNodeInterfaceDouble(){
        MapOf<NodeInterface, Double> object = mapOfNodeInterfaceDoublePool.get();
        if(object == null){
            object = new MapOf<>(mapOfNodeInterfaceDoublePool);
            mapOfNodeInterfaceDoublePool.add(object);
        }
        object.prepare();
        return object;
    }


    private static final MemoryPool<MapOf<BinaryNode, Long>> mapOfBinaryNodeLongPool = new MemoryPool<>();
    public static MapOf<BinaryNode, Long> MapOfBinaryNodeLong(){
        MapOf<BinaryNode, Long> object = mapOfBinaryNodeLongPool.get();
        if(object == null){
            object = new MapOf<>(mapOfBinaryNodeLongPool);
            mapOfBinaryNodeLongPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<MapOf<Integer, AbstractNode>> mapOfIntegerAbstractNode = new MemoryPool<>();
    public static MapOf<Integer, AbstractNode> MapOfIntegerAbstractNode() {
        MapOf<Integer, AbstractNode> object = mapOfIntegerAbstractNode.get();
        if(object == null){
            object = new MapOf<>(mapOfIntegerAbstractNode);
            mapOfIntegerAbstractNode.add(object);
        }
        object.prepare();
        return object;
    }

    //**************************************//
    //                SETS                  //
    //**************************************//

    private static final MemoryPool<SetOfNode<StateNodeInterface>> setOfStateNodePool = new MemoryPool<>();
    public static SetOfNode<StateNodeInterface> SetOfStateNode(){
        SetOfNode<StateNodeInterface> object = setOfStateNodePool.get();
        if(object == null) {
            object = new SetOfNode<>(setOfStateNodePool);
            setOfStateNodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<SetOfNode<Node>> setOfNodePool = new MemoryPool<>();
    public static SetOfNode<Node> SetOfNode(){
        SetOfNode<Node> object = setOfNodePool.get();
        if(object == null) {
            object = new SetOfNode<>(setOfNodePool);
            setOfNodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<SetOf<BinaryNode>> setOfBinaryNodePool = new MemoryPool<>();
    public static SetOf<BinaryNode> SetOfBinaryNode(){
        SetOf<BinaryNode> object = setOfBinaryNodePool.get();
        if(object == null) {
            object = new SetOf<>(setOfBinaryNodePool);
            setOfBinaryNodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<SetOf<Integer>> setOfIntegerPool = new MemoryPool<>();
    public static SetOf<Integer> SetOfInteger(){
        SetOf<Integer> object = setOfIntegerPool.get();
        if(object == null){
            object = new SetOf<>(setOfIntegerPool);
            setOfIntegerPool.add(object);
        }
        object.prepare();
        return object;
    }

}
