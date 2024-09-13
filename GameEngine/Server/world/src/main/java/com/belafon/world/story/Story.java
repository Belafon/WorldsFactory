package com.belafon.world.story;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import com.belafon.world.visibles.creatures.Player;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

@WorldsFactoryClass(className = "Story")
public class Story implements StoryMessage.StoryMessageListener {
    public Dictionary<String, StoryMessage> messages = new Hashtable<>();
    private StoryMessage lastMessage;
    public List<StoryMessage> storyHistory = new ArrayList<>();
    public final Player player;

    @WorldsFactoryObjectsName
    public String storyName;

    public Story(Player player, String storyName) {
        this.player = player;
        this.storyName = storyName;
        WorldsFactoryStoryManager.bindObject(storyName, this);
    }

    @WorldsFactoryPropertySetter(name = "lastMessage")
    public void newStoryMessage(StoryMessage message) {
        lastMessage = message;
        WorldsFactoryStoryManager.setProperty("lastMessage", message, storyName);
        messages.put(message.message, message);
        player.writer.story.newStoryMessage(message);
        message.listeners.add(this);
    }

    public StoryMessage getLastMessage() {
        return lastMessage;
    }

    @WorldsFactoryPropertySetter(name = "choosenOption", initialize = false)
    public void setLastChoosenOption(String option) {
        var lastMessage = this.lastMessage;
        storyHistory.add(lastMessage.clone());
        lastMessage.options.clear();
        WorldsFactoryStoryManager.setProperty("choosenOption", option, storyName);
    }

    @Override
    public void onPropertyChanged(StoryMessage message) {
        player.client.writer.story.newStoryMessage(message);
    }
}
