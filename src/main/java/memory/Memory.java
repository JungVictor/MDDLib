package memory;

import builder.constraints.parameters.*;
import builder.constraints.states.*;
import costmdd.CostMDD;
import costmdd.components.CostNode;
import costmdd.components.InCostArcs;
import costmdd.components.OutCostArcs;
import mdd.MDD;
import mdd.components.*;
import mdd.operations.Pack;
import structures.Domains;
import structures.booleans.ArrayOfBoolean;
import structures.generics.ArrayOf;
import structures.Binder;
import structures.generics.ListOf;
import structures.generics.MapOf;
import structures.generics.SetOf;
import structures.integers.ArrayOfInt;
import structures.integers.MatrixOfInt;
import structures.integers.TupleOfInt;

/**
 * <b>The class that allocate and manage all objects in memory.</b>
 */
public class Memory {

    /**
     * Free the object
     * @param memoryObject The object to free
     */
    public static void free(MemoryObject memoryObject){
        memoryObject.free();
    }


    //**************************************//
    //                MAPS                  //
    //**************************************//

    private static final MemoryPool<MapOf<Integer, ListOf<Integer>>> mapOfIntegerListOfIntegerPool = new MemoryPool<>();
    public static MapOf<Integer, ListOf<Integer>> MapOfIntegerListOfInteger(){
        MapOf<Integer, ListOf<Integer>> object = mapOfIntegerListOfIntegerPool.get();
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


    //**************************************//
    //                SETS                  //
    //**************************************//

    private static final MemoryPool<SetOf<Node>> setOfNodePool = new MemoryPool<>();
    public static SetOf<Node> SetOfNode(){
        SetOf<Node> object = setOfNodePool.get();
        if(object == null) {
            object = new SetOf<>(setOfNodePool);
            setOfNodePool.add(object);
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


    //**************************************//
    //               ARRAYS                 //
    //**************************************//

    private static final MemoryPool<ArrayOf<MDD>> arrayOfMDDPool = new MemoryPool<>();
    public static ArrayOf<MDD> ArrayOfMDD(int capacity){
        ArrayOf<MDD> object = arrayOfMDDPool.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfMDDPool, capacity);
            arrayOfMDDPool.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

    private static final MemoryPool<ArrayOf<Node>> arrayOfNodePool = new MemoryPool<>();
    public static ArrayOf<Node> ArrayOfNode(int capacity){
        ArrayOf<Node> object = arrayOfNodePool.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfNodePool, capacity);
            arrayOfNodePool.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

    private static final MemoryPool<ArrayOfInt> arrayOfIntPool = new MemoryPool<>();
    public static ArrayOfInt ArrayOfInt(int capacity){
        ArrayOfInt object = arrayOfIntPool.get();
        if(object == null){
            object = new ArrayOfInt(arrayOfIntPool, capacity);
            arrayOfIntPool.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

    private static final MemoryPool<ArrayOfBoolean> arrayOfBooleanPool = new MemoryPool<>();
    public static ArrayOfBoolean ArrayOfBoolean(int capacity){
        ArrayOfBoolean object = arrayOfBooleanPool.get();
        if(object == null){
            object = new ArrayOfBoolean(arrayOfBooleanPool, capacity);
            arrayOfBooleanPool.add(object);
        } else object.setLength(capacity);
        object.prepare();
        return object;
    }

    private static final MemoryPool<ArrayOf<TupleOfInt>> arrayOfTupleOfIntPool = new MemoryPool<>();
    public static ArrayOf<TupleOfInt> ArrayOfTupleOfInt(int length){
        ArrayOf<TupleOfInt> object = arrayOfTupleOfIntPool.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfTupleOfIntPool, length);
            arrayOfTupleOfIntPool.add(object);
        } else object.setLength(length);
        object.prepare();
        return object;
    }

    private static final MemoryPool<MatrixOfInt> matrixOfIntPool = new MemoryPool<>();
    public static MatrixOfInt MatrixOfInt(int height, int length){
        MatrixOfInt object = matrixOfIntPool.get();
        if(object == null){
            object = new MatrixOfInt(matrixOfIntPool, height, length);
            matrixOfIntPool.add(object);
        } else object.setSize(height, length);
        object.prepare();
        return object;
    }


    //**************************************//
    //                LISTS                 //
    //**************************************//

    private static final MemoryPool<ListOf<Layer>> listOfLayerPool = new MemoryPool<>();
    public static ListOf<Layer> ListOfLayer(){
        ListOf<Layer> object = listOfLayerPool.get();
        if(object == null){
            object = new ListOf<>(listOfLayerPool);
            listOfLayerPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<ListOf<Node>> listOfNodePool = new MemoryPool<>();
    public static ListOf<Node> ListOfNode(){
        ListOf<Node> object = listOfNodePool.get();
        if(object == null){
            object = new ListOf<>(listOfNodePool);
            listOfNodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<ListOf<Integer>> listOfIntegerPool = new MemoryPool<>();
    public static ListOf<Integer> ListOfInteger(){
        ListOf<Integer> object = listOfIntegerPool.get();
        if(object == null){
            object = new ListOf<>(listOfIntegerPool);
            listOfIntegerPool.add(object);
        }
        object.prepare();
        return object;
    }


    //**************************************//
    //        STATES AND PARAMETERS         //
    //**************************************//

    private static final MemoryPool<NodeState> stateAmongPool = new MemoryPool<>();
    public static StateAmong StateAmong(ParametersAmong constraint){
        StateAmong object = (StateAmong) stateAmongPool.get();
        if(object == null){
            object = new StateAmong(stateAmongPool);
            stateAmongPool.add(object);
        }
        object.prepare();
        object.init(constraint);
        return object;
    }

    private static final MemoryPool<NodeState> stateGCCPool = new MemoryPool<>();
    public static StateGCC StateGCC(ParametersGCC constraint){
        StateGCC object = (StateGCC) stateGCCPool.get();
        if(object == null){
            object = new StateGCC(stateGCCPool);
            stateGCCPool.add(object);
        }
        object.prepare();
        object.init(constraint);
        return object;
    }

    private static final MemoryPool<NodeState> stateSumPool = new MemoryPool<>();
    public static StateSum StateSum(ParametersSum constraint){
        StateSum object = (StateSum) stateSumPool.get();
        if(object == null){
            object = new StateSum(stateSumPool);
            stateSumPool.add(object);
        }
        object.prepare();
        object.init(constraint);
        return object;
    }

    private static final MemoryPool<NodeState> stateAllDiffPool = new MemoryPool<>();
    public static StateAllDiff StateAllDiff(ParametersAllDiff constraint){
        StateAllDiff object = (StateAllDiff) stateAllDiffPool.get();
        if(object == null){
            object = new StateAllDiff(stateAllDiffPool);
            stateAllDiffPool.add(object);
        }
        object.prepare();
        object.init(constraint);
        return object;
    }

    private static final MemoryPool<NodeState> stateDiffPool = new MemoryPool<>();
    public static StateDiff StateDiff(ParametersDiff constraint, int size){
        StateDiff object = (StateDiff) stateDiffPool.get();
        if(object == null){
            object = new StateDiff(stateDiffPool);
            stateDiffPool.add(object);
        }
        object.prepare();
        object.init(constraint, size);
        return object;
    }
    
    
    private static final MemoryPool<NodeState> stateSubsetPool = new MemoryPool<>();
    public static StateSubset StateSubset(ParametersSubset constraint, int ID){
        StateSubset object = (StateSubset) stateSubsetPool.get();
        if(object == null){
            object = new StateSubset(stateSubsetPool);
            stateSubsetPool.add(object);
        }
        object.prepare();
        object.init(constraint, ID);
        return object;
    }
    

    private static final MemoryPool<ParametersAmong> parametersAmongPool = new MemoryPool<>();
    public static ParametersAmong ParametersAmong(int q, int min, int max, SetOf<Integer> V){
        ParametersAmong object = parametersAmongPool.get();
        if(object == null){
            object = new ParametersAmong(parametersAmongPool);
            parametersAmongPool.add(object);
        }
        object.prepare();
        object.init(q, min, max, V);
        return object;
    }

    private static final MemoryPool<ParametersGCC> parametersGCCPool = new MemoryPool<>();
    public static ParametersGCC ParametersGCC(MapOf<Integer, TupleOfInt> gcc){
        ParametersGCC object = parametersGCCPool.get();
        if(object == null){
            object = new ParametersGCC(parametersGCCPool);
            parametersGCCPool.add(object);
        }
        object.prepare();
        object.init(gcc);
        return object;
    }

    private static final MemoryPool<ParametersSum> parametersSumPool = new MemoryPool<>();
    public static ParametersSum ParametersSum(int min, int max, ArrayOfInt vMin, ArrayOfInt vMax){
        ParametersSum object = parametersSumPool.get();
        if(object == null){
            object = new ParametersSum(parametersSumPool);
            parametersSumPool.add(object);
        }
        object.prepare();
        object.init(min, max, vMin, vMax);
        return object;
    }

    private static final MemoryPool<ParametersAllDiff> parametersAllDiffPool = new MemoryPool<>();
    public static ParametersAllDiff ParametersAllDiff(SetOf<Integer> V){
        ParametersAllDiff object = parametersAllDiffPool.get();
        if(object == null){
            object = new ParametersAllDiff(parametersAllDiffPool);
            parametersAllDiffPool.add(object);
        }
        object.prepare();
        object.init(V);
        return object;
    }

    private static final MemoryPool<ParametersDiff> parametersDiffPool = new MemoryPool<>();
    public static ParametersDiff ParametersDiff(int length){
        ParametersDiff object = parametersDiffPool.get();
        if(object == null){
            object = new ParametersDiff(parametersDiffPool);
            parametersDiffPool.add(object);
        }
        object.prepare();
        object.init(length);
        return object;
    }

    //**************************************//
    //            ATOMIC OBJECTS            //
    //**************************************//

    private static final MemoryPool<Domains> domainsPool = new MemoryPool<>();
    public static Domains Domains(){
        Domains object = domainsPool.get();
        if(object == null){
            object = new Domains(domainsPool);
            domainsPool.add(object);
        }
        object.prepare();
        return object;
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


    private static final MemoryPool<Pack> packPool = new MemoryPool<>();
    public static Pack Pack(int pos, int l, Layer L){
        Pack object = packPool.get();
        if(object == null){
            object = new Pack(packPool);
            packPool.add(object);
        }
        object.prepare();
        object.init(pos, l, L);
        return object;
    }

    private static final MemoryPool<Binder> binderPool = new MemoryPool<>();
    public static Binder Binder(){
        Binder object = binderPool.get();
        if(object == null){
            object = new Binder(binderPool);
            binderPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<Node> nodePool = new MemoryPool<>();
    public static Node Node(){
        Node object = nodePool.get();
        if(object == null){
            object = new Node(nodePool);
            nodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<Node> sNodePool = new MemoryPool<>();
    public static SNode SNode(){
        SNode object = (SNode) sNodePool.get();
        if(object == null){
            object = new SNode(sNodePool);
            sNodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<Node> costNodePool = new MemoryPool<>();
    public static CostNode CostNode(){
        CostNode object = (CostNode) costNodePool.get();
        if(object == null){
            object = new CostNode(costNodePool);
            costNodePool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<InArcs> inArcsPool = new MemoryPool<>();
    public static InArcs InArcs(){
        InArcs object = inArcsPool.get();
        if(object == null){
            object = new InArcs(inArcsPool);
            inArcsPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<InArcs> inCostArcsPool = new MemoryPool<>();
    public static InCostArcs InCostArcs(){
        InCostArcs object = (InCostArcs) inCostArcsPool.get();
        if(object == null){
            object = new InCostArcs(inCostArcsPool);
            inCostArcsPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<OutArcs> outArcsPool = new MemoryPool<>();
    public static OutArcs OutArcs(){
        OutArcs object = outArcsPool.get();
        if(object == null){
            object = new OutArcs(outArcsPool);
            outArcsPool.add(object);
        }
        object.prepare();
        return object;
    }

    private static final MemoryPool<OutArcs> outCostArcsPool = new MemoryPool<>();
    public static OutCostArcs OutCostArcs(){
        OutCostArcs object = (OutCostArcs) outCostArcsPool.get();
        if(object == null){
            object = new OutCostArcs(outCostArcsPool);
            outCostArcsPool.add(object);
        }
        object.prepare();
        return object;
    }

    public static final MemoryPool<MDD> mddPool = new MemoryPool<>();
    public static MDD MDD(){
        return MDD(Node());
    }
    public static MDD MDD(Node node){
        MDD object = mddPool.get();
        if(object == null){
            object = new MDD(mddPool);
            mddPool.add(object);
        }
        object.prepare();
        object.setRoot(node);
        return object;
    }

    public static final MemoryPool<MDD> costMddPool = new MemoryPool<>();
    public static CostMDD CostMDD(){return CostMDD(CostNode());}
    public static CostMDD CostMDD(Node node){
        CostMDD object = (CostMDD) costMddPool.get();
        if(object == null){
            object = new CostMDD(costMddPool);
            costMddPool.add(object);
        }
        object.prepare();
        object.setRoot(node);
        return object;
    }

    private static final MemoryPool<Layer> layerPool = new MemoryPool<>();
    public static Layer Layer(){
        Layer object = layerPool.get();
        if(object == null){
            object = new Layer(layerPool);
            layerPool.add(object);
        }
        object.prepare();
        return object;
    }
}
