package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.InventoryMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.items.typeItem.FoodTypeItem;
import com.belafon.world.visibles.items.types.*;

public class InventoryPlayerMessages implements InventoryMessages {
    public final PlayersMessageSender sendMessage;

    public InventoryPlayerMessages(PlayersMessageSender sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Override
    public void ClothesPutOn(Clothes clothes) {
        sendMessage.sendLetter("item " + clothes.getClass().getSimpleName() + " putOn " + clothes.id,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void ClothesPutOff(Clothes clothes) {
        sendMessage.sendLetter("item " + clothes.getClass().getSimpleName() + " putOff " + clothes.id,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void setAddItem(Item item, Creature creature) {
        StringBuilder behaviours = SurroundingPlayerMessages.getAllPossibleBehavioursReqruiementsAsMessage(item,
                creature);
        sendMessage.sendLetter("item addItem " + item.id + " " + item.type.getClass().getSimpleName() + " "
                + item.type.name + " " + item.type.regularWeight + " "
                + item.type.visibility + " " + item.type.toss + " " + getItemPropertiesMessage(item) + " " + behaviours,
                PlayersMessageSender.TypeMessage.other);
    }

    public static StringBuilder getItemPropertiesMessage(Item item) {

        StringBuilder nextValues = new StringBuilder();
        if (item instanceof Food) {
            nextValues.append(((Food) item).getFreshness() + " " + ((FoodTypeItem) item.type).getFilling() + " "
                    + ((Food) item).getWarm());
        } else if (item instanceof Tool) {
            nextValues.append(((Tool) item).getQuality());
        } else if (item instanceof Clothes) {

        } else if (item instanceof QuestItem) {

        }
        return nextValues;
    }

    @Override
    public void setRemoveItem(Item item) {
        sendMessage.sendLetter("item removeItem " + item.id + " " + item.type.name,
                PlayersMessageSender.TypeMessage.other);
    }
}
