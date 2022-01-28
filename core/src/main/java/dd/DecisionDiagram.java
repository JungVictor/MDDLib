package dd;

import memory.Allocable;
import memory.Memory;
import structures.generics.MapOf;

public abstract class DecisionDiagram implements Allocable {

    private int size;

    //**************************************//
    //           INITIALISATION             //
    //**************************************//

    /**
     * Set the size of the DD
     * @param size New size of the DD
     */
    public void setSize(int size){
        this.size = size;
    }

    /**
     * Set the node root of the DD
     * @param root New root node
     */
    public abstract void setRoot(AbstractNode root);

    /**
     * Automatically set the terminal node.
     */
    protected abstract void setTT();


    //**************************************//
    //          SPECIAL FUNCTIONS           //
    //**************************************//

    /**
     * If there is no root, return a default Node type associated with the BDD type
     * @return a Node the same type as the root Node.
     */
    public abstract AbstractNode Node();

    /**
     * Create a DD the same type as the current DD, with the same Node type as the root for default Node
     * @return a DD the same type as the current DD, with the same Node type as the root for default Node
     */
    public abstract DecisionDiagram DD();

    /**
     * Create a DD the same type as the current DD with the given node as a root
     * @param root The node to set as root
     * @return a DD the same type as the current DD with the given node as a root
     */
    public abstract DecisionDiagram DD(AbstractNode root);

    /**
     * Create a copy of the given DD from layer start to stop onto the given DD.
     * Add the copy of the nodes to the given BDD at the same layer + the given offset.
     * Example : start = 1, stop = 3, offset = 2 will copy the layer 1 onto the layer 3 of the copy DD,
     * layer 2 on layer 4 and layer 3 on layer 5.
     * @param copy The DD used to stock the copy
     * @param offset The offset of the copy
     * @param start The first layer to copy
     * @param stop The last layer to copy
     * @return A copy of the current BDD from layer start to layer stop.
     */
    public DecisionDiagram copy(DecisionDiagram copy, int offset, int start, int stop){
        for(int i = start; i < stop; i++){
            for(AbstractNode original : iterateOnLayer(i)) {
                AbstractNode copyNode = original.getX1();
                for(int arc : original.iterateOnChildLabel()){
                    AbstractNode child = original.getChild(arc);
                    AbstractNode copyChild = child.getX1();
                    // Child node is not yet copied
                    if(copyChild == null) {
                        copyChild = copy.Node();
                        child.setX1(copyChild);
                        copy.addNode(copyChild, i+1+offset);
                    }
                    copy.addArc(copyNode, arc, copyChild, i+offset);
                }
                original.setX1(null);
            }
        }
        return copy;
    }

    /**
     * Create a copy of the given DD from root to tt onto the given DD.
     * Add the copy of the nodes to the given DD at the same layer + the given offset.
     * @param copy The DD used to stock the copy
     * @param root The root node of the BDD
     * @param offset The offset of the copy
     * @return A copy of the current DD from root to tt
     */
    public DecisionDiagram copy(DecisionDiagram copy, AbstractNode root, int offset){
        getRoot().setX1(root);
        copy(copy, offset, 0, size());
        copy.setTT();
        return copy;
    }

    /**
     * Create a copy of the given DD from root to tt onto the given BDD.
     * @param copy The DD used to stock the copy
     * @return A copy of the current DD from root to tt
     */
    public DecisionDiagram copy(DecisionDiagram copy){
        copy.setSize(size());
        return copy(copy, copy.getRoot(), 0);
    }

    /**
     * Create a copy of the given DD from root to tt.
     * @return A copy of the current DD from root to tt
     */
    public DecisionDiagram copy(){
        return copy(DD());
    }


    //**************************************//
    //               GETTERS                //
    //**************************************//

    /**
     * Get the root of the DD
     * @return The root of the DD
     */
    public abstract AbstractNode getRoot();

    /**
     * Get the terminal node of the DD
     * @return The terminal node of the DD
     */
    public abstract AbstractNode getTt();

    /**
     * Iterable on the nodes of the ith layer
     * @param i depth of the layer
     * @return Iterable on the nodes of the ith layer
     */
    public abstract Iterable<AbstractNode> iterateOnLayer(int i);

    /**
     * Get the number of nodes in the ith layer
     * @param i The index of the layer
     * @return The number of nodes in the ith layer
     */
    public abstract int getLayerSize(int i);

    /**
     * Iterable on the value of the domain of the ith variable
     * @param i The index of the variable
     * @return The Iterable over the value of the domain
     */
    public abstract Iterable<Integer> iterateOnDomain(int i);

    /**
     * Get the size of the domain of the ith variable
     * @param i Index of the variable
     * @return The number of value in the domain of the ith variable
     */
    public abstract int getDomainSize(int i);

    /**
     * Check if the value v is contained in the domain of the ith variable
     * @param i The index of the variable
     * @param v The value to check
     * @return true if the value is contained in the ith domain, false otherwise
     */
    public abstract boolean domainContains(int i, int v);

    /**
     * Get the depth of the DD (the number of layers)
     * @return int size of the DD
     */
    public int size(){
        return size;
    }

    /**
     * Get the number of nodes in the DD
     * @return The number of nodes in the DD
     */
    public int nodes(){
        int n = 0;
        for(int i = 0; i < size; i++) n += getLayerSize(i);
        return n;
    }

    /**
     * Get the number of arcs in the BDD
     * @return The number of arcs in the BDD
     */
    public int arcs(){
        int n = 0;
        for(int i = 0; i < size; i++) for(AbstractNode x : iterateOnLayer(i)) n += x.numberOfChildren();
        return n;
    }

    /**
     * Get the number of solutions represented by the DD
     * @return The number of solutions represented by the DD
     */
    public double nSolutions(){
        if(getLayerSize(size() - 1) == 0) return 0;
        if(getRoot() == getTt()) return 0;

        MapOf<AbstractNode, Double> currentLayer = Memory.MapOfAbstractNodeDouble();
        MapOf<AbstractNode, Double> next = Memory.MapOfAbstractNodeDouble(), tmp;

        for(AbstractNode x : iterateOnLayer(size() - 1)) currentLayer.put(x, 1.0);

        for(int i = size() - 2; i >= 0; i--){
            for(AbstractNode x : iterateOnLayer(i)){
                double sum = 0;
                for(int arc : x.iterateOnChildLabel()) sum += currentLayer.get(x.getChild(arc));
                next.put(x, sum);
            }
            currentLayer.clear();
            tmp = currentLayer;
            currentLayer = next;
            next = tmp;
        }
        double result = currentLayer.get(getRoot());

        Memory.free(currentLayer);
        Memory.free(next);

        return result;
    }


    //**************************************//
    //              MANAGE DD               //
    //**************************************//

    /**
     * Add the given Node to the given layer.
     * @param node The node to add
     * @param layer The index of the layer
     */
    public abstract void addNode(AbstractNode node, int layer);

    /**
     * Remove a node from the given layer
     * @param node The node to remove
     * @param layer The index of the layer
     */
    public abstract void removeNode(AbstractNode node, int layer);

    /**
     * Add an arc between the source node and the destination node with the given value as label.
     * Ensures the connection between the two nodes
     * @param source The source node (parent)
     * @param value The value of the arc's label
     * @param destination The destination node (child)
     * @param layer The layer of the PARENT node (source)
     */
    public void addArc(AbstractNode source, int value, AbstractNode destination, int layer){
        source.addChild(value, destination);
        destination.addParent(value, source);
    }

    /**
     * Remove the arc from the source node with the given label's value.
     * Ensures to delete references of this arc in both nodes.
     * @param source The source node
     * @param value The value of the arc's label.
     */
    public void removeArc(AbstractNode source, int value){
        source.getChild(value).removeParent(value, source);
        source.removeChild(value);
    }

    /**
     * Add the node destination to the given layer, and add an arc between the source node
     * and the destination node with the given value as label.
     * @param source The source node
     * @param value The value of the arc's label
     * @param destination The destination node - node to add in the DD
     * @param layer The layer of the DD where the node destination will be added
     */
    public void addArcAndNode(AbstractNode source, int value, AbstractNode destination, int layer){
        addArc(source, value, destination, layer-1);
        addNode(destination, layer);
    }

    /**
     * Transform the DD into a normal form (delete useless nodes, merge leaves, reduce).
     */
    public abstract void reduce();

    /**
     * Clear all nodes' associations
     */
    public void clearAllAssociations(){
        for(int i = 0; i < size(); i++) for(AbstractNode node : iterateOnLayer(i)) node.clearAssociations();
    }

    /**
     * Clear the DD without freeing it.
     * That is, remove and free all nodes from the layers,
     * clear the domains and set the tt's pointer to null.
     * The layers are kept.
     */
    public abstract void clear();

}