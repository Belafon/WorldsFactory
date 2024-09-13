package com.belafon.server.messages;

import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public interface BehavioursMessages{
    /*
     * Informs a player that there exists a behaviour that is feasible for him.
     */
    public default void newFeasibleBehaviour(BehaviourType behaviourType) {
    }

    /*
     * Informs a player that some feasible behaviour is no longer feasible for him.
     */
    public default void removeFeasibleBehaviour(BehaviourType behaviourType) {
    }

    /*
     * Informs a player that some behaviour exists.
     */
    public default void setupBehaviour(BehaviourType behaviourType) {
    }

    /*
     * Informs a player that some requirement exists.
     */
    public default void setupPossibleReqirement(BehavioursPossibleRequirement requirement) {
    }
    
}
