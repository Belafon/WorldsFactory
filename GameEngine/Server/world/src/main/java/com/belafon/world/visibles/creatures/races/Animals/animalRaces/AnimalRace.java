package com.belafon.world.visibles.creatures.races.Animals.animalRaces;

import com.belafon.server.sendMessage.MessageSender;
import com.belafon.world.visibles.creatures.races.Animals.TestingMessages;

public class AnimalRace {
    public final String name;
    public final String description;
    public final MessageSender sendMessage;
    
    public AnimalRace(String name, String description, MessageSender sendMessage) {
        this.name = name;
        this.description = description;
        this.sendMessage = sendMessage;
    }

    public static final AnimalRace deer = new AnimalRace(
        "Deer", "Deers description", 
        TestingMessages.createMessageSender()); // TODO
}
