package dd.interfaces;

import builder.constraints.states.NodeState;

public interface IStateNode extends INode {

    /**
     * Return a new node of the same type as this node implementing StateNodeInterface.
     * @return A new node implementing StateNodeInterface
     */
    IStateNode Node();

    /**
     * Set the state of the node
     * @param state The state of the node
     */
    void setState(NodeState state);

    /**
     * Get the state of the node
     * @return The state of the node
     */
    NodeState getState();

}
