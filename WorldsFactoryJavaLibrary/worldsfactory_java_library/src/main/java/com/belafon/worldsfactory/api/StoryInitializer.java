package com.belafon.worldsfactory.api;

import java.util.concurrent.CompletableFuture;
import java.util.HashSet;
import java.util.Set;

/**
 * Story name, and either pathToStoryData or code must be set,
 * Event graph condition must be set
 */
public class StoryInitializer {
    private String storyName;
    private String pathToStoryData;
    private boolean debugMode = false;
    private String code;
    private EventGraphCondition CurrentEventGraphCondition = EventGraphCondition.MOVE_MAX_BY_ONE;
    private boolean checkingConditionsAfterEachSet = false;
    private Set<String> packagesToLocateSources = new HashSet<>();

    public StoryInitializer withStoryName(String storyName) {
        this.storyName = storyName;
        return this;
    }

    public StoryInitializer withPathToStoryData(String pathToStoryData) {
        this.pathToStoryData = pathToStoryData;
        return this;
    }

    public StoryInitializer withDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    public StoryInitializer withCode(String code) {
        this.code = code;
        return this;
    }

    public StoryInitializer withEventGraphCondition(EventGraphCondition EventGraphCondition) {
        this.CurrentEventGraphCondition = EventGraphCondition;
        return this;
    }

    public StoryInitializer withCheckingConditionsAfterEachSet(boolean checkingConditionsAfterEachSet) {
        this.checkingConditionsAfterEachSet = checkingConditionsAfterEachSet;
        return this;
    }

    public StoryInitializer withPackageToLocateSourcesWithAnnotatedClasses(String packagesPath) {
        this.packagesToLocateSources.add(packagesPath);
        return this;
    }

    public boolean isCheckingConditionsAfterEachSet() {
        return checkingConditionsAfterEachSet;
    }

    public EventGraphCondition getCurrentEventGraphCondition() {
        return CurrentEventGraphCondition;
    }

    public String getStoryName() {
        return storyName;
    }

    public String getPathToStoryData() {
        return pathToStoryData;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public String getCode() {
        return code;
    }

    public Set<String> getPackagesToLocateSources() {
        return packagesToLocateSources;
    }

    public CompletableFuture<WorldsFactoryStory> build() {
        if (storyName == null || (pathToStoryData == null && code == null)) {
            throw new IllegalArgumentException("Story name, and either pathToStoryData or code must be set");
        }

        if (CurrentEventGraphCondition == null)
            throw new IllegalArgumentException("Event graph condition must be set");

        return WorldsFactoryStoriesManager.initializeStory(this);
    }
}
