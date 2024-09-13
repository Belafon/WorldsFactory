package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.BehavioursMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public class BehavioursPlayersMessages implements BehavioursMessages {
    public final PlayersMessageSender sendMessage;

    public BehavioursPlayersMessages(PlayersMessageSender sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Override
    public void newFeasibleBehaviour(BehaviourType behaviourType) {
        sendMessage.sendLetter("player feasibleBehaviour add " + behaviourType.idName,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void removeFeasibleBehaviour(BehaviourType behaviourType) {
        sendMessage.sendLetter("player feasibleBehaviour remove " + behaviourType.idName,
                PlayersMessageSender.TypeMessage.other);
    }

    public void setupBehaviour(BehaviourType behaviourType) {
        StringBuilder requirements = getRequirementsInMessage(behaviourType);
        sendMessage.sendLetter("behaviour setupBehaviour "
                + behaviourType.idName + " "
                + behaviourType.name.replaceAll(" ", "_") + " "
                + behaviourType.description.replaceAll(" ", "_") + " "
                + requirements, PlayersMessageSender.TypeMessage.other);
    }

    private StringBuilder getRequirementsInMessage(BehaviourType behaviourType) {
        StringBuilder message = new StringBuilder();
        boolean first = true;
        for (var requirement : behaviourType.requirements.keySet()) {
            if (first) {
                message.append(requirement.idName)
                        .append("|")
                        .append(behaviourType.requirements.get(requirement).description() == null ? ""
                                : behaviourType.requirements.get(requirement).description())
                        .append("|")
                        .append(behaviourType.requirements.get(requirement).numOfSpecificIngredients())
                        .append("|")
                        .append(behaviourType.requirements.get(requirement).numOfGeneralIngredients());
                first = false;
            } else {
                message.append(",")
                        .append(requirement.idName)
                        .append("|")
                        .append(behaviourType.requirements.get(requirement).description() == null ? ""
                                : behaviourType.requirements.get(requirement).description())
                        .append("|")
                        .append(behaviourType.requirements.get(requirement).numOfSpecificIngredients())
                        .append("|")
                        .append(behaviourType.requirements.get(requirement).numOfGeneralIngredients());
            }
        }
        return message;
    }

    public void setupPossibleReqirement(BehavioursPossibleRequirement requirement) {
        sendMessage.sendLetter("behaviour setupRequirement "
                + requirement.idName
                + " "
                + requirement.name.replaceAll(" ", "_"),
                PlayersMessageSender.TypeMessage.other);

    }

}
