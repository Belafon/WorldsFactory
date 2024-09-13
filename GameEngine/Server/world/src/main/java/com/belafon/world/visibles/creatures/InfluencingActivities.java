package com.belafon.world.visibles.creatures;

import com.belafon.server.sendMessage.MessageSender;

/**
 * List of messages that inform a creature about change of state 
 * of other visibles.
 */
public class InfluencingActivities {
    private MessageSender writer;

    public InfluencingActivities(MessageSender writer) {
        this.writer = writer;
    }

    public void otherCreaturesBehaviourChanged(Creature creature) {
        writer.creatureVisible.behaviourChanged(creature);
    }

    public void otherCreaturesPositionChanged(Creature creature) {
        writer.creatureVisible.positionChanged(creature);
    }

}
