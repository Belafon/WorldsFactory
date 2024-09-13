
package com.belafon.worldsfactory.api;

import com.belafon.worldsfactory.StoriesManager;

import java.util.concurrent.CompletableFuture;

public class WorldsFactoryStoriesManager {
    private static StoriesManager storiesManager = new StoriesManager();

    private WorldsFactoryStoriesManager() {
    }

    /**
     * This is the proper way how to initialize a story
     * @param storyInitializer
     * @return
     */
    public static CompletableFuture<WorldsFactoryStory> initializeStory(StoryInitializer storyInitializer) {
        return storiesManager.initializeStory(storyInitializer);
    }

    /**
     * @return the name of the default story, the default story is the first story that was initialized
     * @throws NoStoryInitializedException
     */
    public static String getImplicitStoryName() throws NoStoryInitializedException {
        return storiesManager.getImplicitStory().getName();
    }

    /**
     * This is the default story, it is the first story that was initialized
     * @throws NoStoryInitializedException
     */
    public static WorldsFactoryStory getImplicitStory() throws NoStoryInitializedException {
        return storiesManager.getImplicitStory();
    }

    /**
     * This finds a story by its name
     * @throws NoStoryInitializedException
     */
    public static CompletableFuture<WorldsFactoryStory> getStory(String name) throws NoStoryInitializedException {
        return storiesManager.getStory(name);
    }

    /**
     * This is the proper way how to end a story,
     * the story will be removed from the storiesManger,
     * so it is possible that default Story will be changed
     * @param name
     */
    public static void endStory(String name) {
        storiesManager.endStory(name);
    }

    /**
     * This is the proper way how to end a story,
     * the story will be removed from the storiesManger,
     * so it is possible that default Story will be changed
     * @param name
     */
    public static void endStory(WorldsFactoryStory story) {
        storiesManager.endStory(story);
    }

    public static void endAllStories() {
        storiesManager.endAllStories();
    }

}