package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.StoryMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;
import com.belafon.world.story.StoryMessage;

public class StoryPlayerMessages implements StoryMessages {
    public final PlayersMessageSender sendMessage;

    public StoryPlayerMessages(PlayersMessageSender sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Override
    public void newStoryMessage(StoryMessage storyMessage){
        var msg = new StringBuilder("__start__string__\n");
        msg.append(storyMessage.message);
        
        for (var option : storyMessage.options) {
            msg.append("|||").append(option);
        }
        msg.append("\n__end__string__\n");

        sendMessage.sendLetter("story new_message " + msg.toString(), PlayersMessageSender.TypeMessage.other);
    }
}
