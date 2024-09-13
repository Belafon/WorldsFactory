package com.belafon.world.mobileClient.game.client;

import java.util.List;

import com.belafon.world.mobileClient.client.Client;
import com.belafon.world.mobileClient.game.behaviours.Behaviour;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

public class BehavioursMessage {

    public void executeBehaviour(List<BehavioursPossibleIngredient> selectedIngredients, Behaviour behaviour) {
        Client.sendMessage("game behaviour executeBehaviour " + behaviour.messagesName + " " + writeIngredients(selectedIngredients));
    }

    private StringBuffer writeIngredients(List<BehavioursPossibleIngredient> selectedIngredients) {
        StringBuffer sb = new StringBuffer();
        for (BehavioursPossibleIngredient ingredient : selectedIngredients) {
            sb.append(ingredient.getVisibleType());
            sb.append("|");
            sb.append(ingredient.getId());
            sb.append(",");
        }
        return sb;
    }

}
