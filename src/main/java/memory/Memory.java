package memory;

import mdd.MDD;
import mdd.components.InArcs;
import mdd.components.Node;
import mdd.components.OutArcs;
import structures.ArrayOf;
import structures.Binder;
import structures.SetOf;

public class Memory {

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

    private static final MemoryPool<ArrayOf<Node>> arrayOfNodes = new MemoryPool<>();
    public static ArrayOf<Node> ArrayOfNode(int capacity){
        ArrayOf<Node> object = arrayOfNodes.get();
        if(object == null){
            object = new ArrayOf<>(arrayOfNodes);
            arrayOfNodes.add(object);
        }
        object.setLength(capacity);
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

}
