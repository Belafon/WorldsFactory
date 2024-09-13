package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.ConditionCreatureMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;
import com.belafon.world.visibles.creatures.behaviour.Behaviour;
import com.belafon.world.visibles.creatures.condition.knowledge.Knowledge;

public class ConditionPlayerMessages implements ConditionCreatureMessages {
    public final PlayersMessageSender sendMessage;

    public ConditionPlayerMessages(PlayersMessageSender sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Override
    public void setHealth(int health) {
        sendMessage.sendLetter("player actualCondition health " + health, PlayersMessageSender.TypeMessage.actualStats);
    } 

    @Override
    public void setHunger(int hunger) {
        sendMessage.sendLetter("player actualCondition hunger " + hunger, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setBleeding(int bleeding) {
        sendMessage.sendLetter("player actualCondition bleeding " + bleeding, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setHeat(int heat) {
        sendMessage.sendLetter("player actualCondition heat " + heat, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setFatigueMax(int fatigueMax) {
        sendMessage.sendLetter("player actualCondition fatigueMax " + fatigueMax, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void addKnowledge(Knowledge knowledge) {
        sendMessage.sendLetter("player knowledge add " + knowledge.degree + " " + knowledge.type.name, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setCurrentEnergyOutput(int currentEnergyOutput) {
        sendMessage.sendLetter("player abilityCondition currentEnergyOutput " + currentEnergyOutput, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setStrength(int strength) {
        sendMessage.sendLetter("player abilityCondition strength " + strength, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setAgility(int agility) {
        sendMessage.sendLetter("player abilityCondition agility " + agility, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setSpeedOfWalk(int speedOfWalk) {
        sendMessage.sendLetter("player abilityCondition speedOfWalk " + speedOfWalk, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setHearing(int hearing) {
        sendMessage.sendLetter("player abilityCondition hearing " + hearing, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setObservation(int observation) {
        sendMessage.sendLetter("player abilityCondition observation " + observation, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setVision(int vision) {
        sendMessage.sendLetter("player abilityCondition vision " + vision, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setCurrentSpeed(int currentSpeed) {
        sendMessage.sendLetter("player abilityCondition currentSpeed " + currentSpeed, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setLoudness(int loudness) {
        sendMessage.sendLetter("player abilityCondition loudness " + loudness, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setFatigue(int fatigue) {
        sendMessage.sendLetter("player abilityCondition fatigue " + fatigue, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setAttention(int attention) {
        sendMessage.sendLetter("player abilityCondition attention " + attention, PlayersMessageSender.TypeMessage.actualStats);
    }

    @Override
    public void setSpeedOfRun(int speedOfRun) {
        sendMessage.sendLetter("player abilityCondition speedOfRun " + speedOfRun,
                PlayersMessageSender.TypeMessage.actualStats);
    }
    
    
    @Override
    public void setBehaviour(Behaviour behaviour, int duration) {
        if(behaviour == null)
            sendMessage.sendLetter("behaviour doBehaviour null", PlayersMessageSender.TypeMessage.other);
        else
            sendMessage.sendLetter("behaviour doBehaviour " + behaviour.getType().idName + " " + duration, PlayersMessageSender.TypeMessage.other);
    }

}
