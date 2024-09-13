package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.ItemVisibleMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;

public class ItemVisiblePlayerMessages implements ItemVisibleMessages {
    public final PlayersMessageSender sender;

    public ItemVisiblePlayerMessages(PlayersMessageSender sender) {
        this.sender = sender;
    }   

}
