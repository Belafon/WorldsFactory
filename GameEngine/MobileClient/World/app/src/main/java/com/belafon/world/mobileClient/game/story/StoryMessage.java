package com.belafon.world.mobileClient.game.story;

import java.util.List;

public class StoryMessage {
    public String message;
    public List<StoryOption> options;
    public StoryMessage(String message, List<StoryOption> options) {
        this.message = message;
        this.options = options;
    }
}