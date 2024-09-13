package com.belafon.world.visibles.creatures.races.NPC;

import com.belafon.server.messages.BehavioursMessages;
import com.belafon.server.messages.ConditionCreatureMessages;
import com.belafon.server.messages.CreatureVisibleMessages;
import com.belafon.server.messages.InventoryMessages;
import com.belafon.server.messages.ItemVisibleMessages;
import com.belafon.server.messages.ResourceVisibleMessages;
import com.belafon.server.messages.ServerMessages;
import com.belafon.server.messages.StoryMessages;
import com.belafon.server.messages.SurroundingMessages;
import com.belafon.server.sendMessage.MessageSender;

public class NPCMessages implements BehavioursMessages, ConditionCreatureMessages,
InventoryMessages, ItemVisibleMessages,
ResourceVisibleMessages, SurroundingMessages,
ServerMessages, CreatureVisibleMessages, StoryMessages {
    public static MessageSender createMessageSender() {
        final NPCMessages messages = new NPCMessages();

        MessageSender sender = MessageSender.getBuilder()
                .setBehavioursMessages(messages)
                .setCondition(messages)
                .setCreatureVisible(messages)
                .setItemVisible(messages)
                .setResourceVisible(messages)
                .setServer(messages)
                .setSurrounding(messages)
                .setInventory(messages)
                .setStory(messages)
                .build();
        return sender;
    }

}
