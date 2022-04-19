package dd.interfaces;

import builder.constraints.states.NodeState;

public interface StateNodeInterface extends NodeInterface {

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
