package com.belafon.world.mobileClient.game.story;

public class StoryOption {
    public final String title;
    public StoryOption(String title){
        this.title = title;
    }

    @Override
    public String toString(){
        return title;
    }
}