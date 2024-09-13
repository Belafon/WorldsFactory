package com.belafon.server.messages;

import com.belafon.world.story.StoryMessage;

public interface StoryMessages {
    public default void newStoryMessage(StoryMessage message){
    }
}
