package com.belafon.world.mobileClient.game.story;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.client.Client;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;

public class Story {
    public Dictionary<String, StoryMessage> messages = new Hashtable<>();
    private StoryMessage lastMessage;
    public List<StoryMessage> storyHistory = new ArrayList<>();
    public HashSet<StoryMessageObserver> storyMessageListener = new HashSet<>();

    public Story(List<StoryMessage> messages) {
        for (var message : messages) {
            this.messages.put(message.message, message);
        }
    }

    public void showNewMessage(StoryMessage message) {
        lastMessage = message;
        storyHistory.add(lastMessage);
        for(var observer : storyMessageListener){
            AbstractActivity.getActualActivity().runOnUiThread(() -> {
                observer.update(message);
            });
        }
    }

    public StoryMessage getLastMessage() {
        return lastMessage;
    }

    public void newMessage(String serverMessage){
        var parts = serverMessage.split("\\|\\|\\|");
        if(parts.length < 1){
            throw new IllegalArgumentException("message for story is not correct");
        }

        var message = parts[0];
        var options = new ArrayList<StoryOption>();
        for(var i = 1; i < parts.length; i++){
            options.add(new StoryOption(parts[i]));
        }
        var storyMessage = new StoryMessage(message, options);
        showNewMessage(storyMessage);
    }

    public void chooseOption(String option){
        Client.sender.storyMessages.chooseOption(option);
    }

    public interface StoryMessageObserver{
        void update(StoryMessage storyMessage);
    }
}
