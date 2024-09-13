package com.belafon.worldsfactory;

import com.belafon.worldsfactory.api.WorldsFactoryStory;
import com.belafon.worldsfactory.api.NoStoryInitializedException;
import com.belafon.worldsfactory.api.StoryInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

public class StoriesManager {
    private List<WorldsFactoryStory> stories = new ArrayList<WorldsFactoryStory>();
    private StoryLoader storyLoader = new StoryLoader();
    private CompletableFuture<WorldsFactoryStory> firstLoadingStory;
    private ConcurrentHashMap<String, CompletableFuture<WorldsFactoryStory>> loadingStories = new ConcurrentHashMap<>();

    public synchronized WorldsFactoryStory getImplicitStory() throws NoStoryInitializedException {
        if (firstLoadingStory == null) {
            throw new NoStoryInitializedException("No story initialized");
        }
        return firstLoadingStory.join();
    }

    public synchronized CompletableFuture<WorldsFactoryStory> initializeStory(StoryInitializer initializer) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        var future = CompletableFuture.supplyAsync(() -> {
            WorldsFactoryStory story = null;
            try {
                story = storyLoader.load(initializer);
            } catch (IOException e) {
                throw new CompletionException(e);
            }

            stories.add(story);
            return story;
        }, executor)
                .thenApplyAsync((story) -> {
                    executor.shutdown();
                    return story;
                }, executor);

        if (firstLoadingStory == null) {
            firstLoadingStory = future;
        }

        loadingStories.put(initializer.getStoryName(), future);
        return future;
    }

    public CompletableFuture<WorldsFactoryStory> getStory(String name) throws NoStoryInitializedException {
        if (loadingStories.containsKey(name)) {
            var loadingStory = loadingStories.get(name);
            return loadingStory;
        }
        throw new NoStoryInitializedException("No story initialized");
    }

    /**
     * This is the proper way how to end a story,
     * the story will be removed from the storiesManger,
     * so it is possible that default Story will be changed
     * @param name
     */
    public void endStory(String name) {
        try {
            var story = getStory(name).join();
            stories.remove(story);
            story.end();
        } catch (NoStoryInitializedException e) {
            throw new Error(e);
        }
    }

    /**
     * This is the proper way how to end a story,
     * the story will be removed from the storiesManger,
     * so it is possible that default Story will be changed
     * @param name
     */
    public void endStory(WorldsFactoryStory story){
        if(stories.contains(story)){
            stories.remove(story);
            story.end();
        }
    }

    public void endAllStories() {
        for (var story : loadingStories.values()) {
            endStory(story.join());
        }
    }
}
