package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.ResourceVisibleMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;

public class ResourceVisiblePlayerMessages implements ResourceVisibleMessages {
    PlayersMessageSender sender;
    public ResourceVisiblePlayerMessages(PlayersMessageSender sender) {
        this.sender = sender; 
    }
 
}
