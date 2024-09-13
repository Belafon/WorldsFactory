package com.belafon.world.mobileClient.game.client;

public class MessageSender {

    public BehavioursMessage behaviours;
    public ServerMessages serverMessages;
    public StoryMessages storyMessages;
    public MessageSender() {
        behaviours = new BehavioursMessage();
        serverMessages = new ServerMessages();
        storyMessages = new StoryMessages();
    }

}
