package com.belafon.world.story;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

@WorldsFactoryClass(className = "StoryMessage")
public class StoryMessage {
    public HashSet<StoryMessageListener> listeners = new HashSet<>();

    @WorldsFactoryObjectsName
    public String storyObjectName;

    public String message;
    public List<String> options;

    public StoryMessage() {
        System.out.println("StoryMessage: constructor -----------------------------------     ");
    }

    @WorldsFactoryPropertySetter(name = "message")
    public void setMessage(String message) {
        this.message = message;
        WorldsFactoryStoryManager.setProperty("message", message, storyObjectName);

        for (StoryMessageListener listener : listeners) {
            listener.onPropertyChanged(this);
        }
    }

    @WorldsFactoryPropertySetter(name = "options")
    public void setOption(int index, String option) {
        setOptionToList(index, option);
        WorldsFactoryStoryManager.setProperty("options[" + index + "]", option, storyObjectName);
    }

    private void setOptionToList(int index, String option) {
        if(option == null) {
            return;
        }
        
        if (options == null) {
            options = new ArrayList<>();
        }

        if (index < options.size()) {
            options.set(index, option);
        } else {
            options.add(option);
            if (index != options.size() - 1) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
        }
    }

    public StoryMessage clone() {
        StoryMessage newMessage = new StoryMessage();
        newMessage.message = this.message;
        newMessage.options = new ArrayList<>(this.options);
        return newMessage;
    }

    public static interface StoryMessageListener {
        public void onPropertyChanged(StoryMessage message);
    }
}