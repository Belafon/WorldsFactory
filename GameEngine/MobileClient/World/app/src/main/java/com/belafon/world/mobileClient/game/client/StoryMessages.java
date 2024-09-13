package com.belafon.world.mobileClient.game.client;

import com.belafon.world.mobileClient.client.Client;
import com.belafon.world.mobileClient.game.behaviours.Behaviour;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

import java.util.List;

public class StoryMessages {
    public void chooseOption(String choosenOption) {
        Client.sendMessage("game story choosenOption " + choosenOption);
    }
}
