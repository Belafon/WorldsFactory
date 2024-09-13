package com.belafon.worldsfactory.api;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletionException;

import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;

public class WorldsFactoryStoryManager {
    public static void setProperty(String propertyName, Object value, String pythonObjectName, String storyName_OPTIONAL) {
        if (storyName_OPTIONAL == null) {
            try {
                storyName_OPTIONAL = WorldsFactoryStoriesManager.getImplicitStoryName();
            } catch (NoStoryInitializedException e) {
                throw new Error(e);
            }
        }

        try {
            var future = WorldsFactoryStoriesManager.getStory(storyName_OPTIONAL);
            future.join().notifyPropertyChangedInCode(propertyName, value, pythonObjectName);
        } catch (CompletionException e) {
            throw new Error(e.getCause());
        } catch (NoStoryInitializedException e) {
            throw new Error(e);
        }
    }

    public static void setProperty(String propertyName, Object value, String pythonObjectName) {
        setProperty(propertyName, value, pythonObjectName, null);
    }

    public static void setPropertyWithExceptions(String propertyName, Object value, String pythonObjectName,
            String storyName_OPTIONAL)
            throws IOException, NoStoryInitializedException {
        if (storyName_OPTIONAL == null) {
            storyName_OPTIONAL = WorldsFactoryStoriesManager.getImplicitStoryName();
        }

        var future = WorldsFactoryStoriesManager.getStory(storyName_OPTIONAL);
        try {
            future.join().notifyPropertyChangedInCode(propertyName, value, pythonObjectName);
        } catch (CompletionException e) {
            if (e.getCause() instanceof IOException ioe) {
                throw ioe;
            }
            throw e;
        }
    }

    public static void setPropertyWithExceptions(String propertyName, Object value, String className)
            throws IOException, NoStoryInitializedException {
        setPropertyWithExceptions(propertyName, className, className, null);
    }

    public static void bindObject(String objectName, Object classObject, String storyName_OPTIONAL) {
        if (storyName_OPTIONAL == null) {
            try {
                if (classObject.getClass().isAnnotationPresent(WorldsFactoryClass.class)) {
                    WorldsFactoryClass annotation = classObject.getClass().getAnnotation(WorldsFactoryClass.class);
                    storyName_OPTIONAL = annotation.story();

                    if (storyName_OPTIONAL.equals("")) {
                        storyName_OPTIONAL = WorldsFactoryStoriesManager.getImplicitStoryName();
                    }
                } else {
                    storyName_OPTIONAL = WorldsFactoryStoriesManager.getImplicitStoryName();
                }
            } catch (NoStoryInitializedException e) {
                throw new Error(e);
            }
        }

        try {
            var future = WorldsFactoryStoriesManager.getStory(storyName_OPTIONAL);
            future.join().bindObject(objectName, classObject);
        } catch (CompletionException e) {
            throw new Error(e.getCause());
        } catch (NoStoryInitializedException e) {
            throw new Error(e);
        }
    }

    public static void bindObject(String objectName, Object object) {
        bindObject(objectName, object, null);
    }

    public static void bindObjectWithExceptions(String objectName, Object object, String storyName_OPTIONAL)
            throws IOException, NoStoryInitializedException {
        if (storyName_OPTIONAL == null) {
            storyName_OPTIONAL = WorldsFactoryStoriesManager.getImplicitStoryName();
        }

        var future = WorldsFactoryStoriesManager.getStory(storyName_OPTIONAL);
        try {
            future.join().bindObject(objectName, object);
        } catch (CompletionException e) {
            if (e.getCause() instanceof IOException ioe) {
                throw ioe;
            }
            throw e;
        }
    }

    public static Object getProperty(String propertyName, String objectName) {
        return getProperty(propertyName, objectName, null);
    }

    public static Object getProperty(String propertyName, String objectName, String storyName_OPTIONAL) {
        if (storyName_OPTIONAL == null) {
            try {
                storyName_OPTIONAL = WorldsFactoryStoriesManager.getImplicitStoryName();
            } catch (NoStoryInitializedException e) {
                throw new Error(e);
            }
        }

        try {
            var future = WorldsFactoryStoriesManager.getStory(storyName_OPTIONAL);
            return future.join().getProperty(propertyName, objectName);
        } catch (CompletionException e) {
            throw new Error(e.getCause());
        } catch (NoStoryInitializedException e) {
            throw new Error(e);
        }
    }

    /**
     * @param className
     * @return a set of all object names of the given class name in the current story
     */
    public static Set<String> getAllObjectNamesOfType(String className, String storyName_OPTIONAL) {
        if (storyName_OPTIONAL == null) {
            try {
                storyName_OPTIONAL = WorldsFactoryStoriesManager.getImplicitStoryName();
            } catch (NoStoryInitializedException e) {
                throw new Error(e);
            }
        }

        try {
            var future = WorldsFactoryStoriesManager.getStory(storyName_OPTIONAL);
            return future.join().getAllObjectNamesOfType(className);
        } catch (CompletionException e) {
            throw new Error(e.getCause());
        } catch (NoStoryInitializedException e) {
            throw new Error(e);
        }
    }
}