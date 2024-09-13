package com.belafon.worldsfactory.api;

import org.junit.Assert;
import org.junit.Test;

import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.Story;

public class SetPropertyInEventsActionTest {
    @Test
    public void setPropertyInEventsAction() {
        String exampleCode = new LoadDataFile().loadCode("testSetPropertyInEventsActionTest.py");

        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(true)
                .build();

        var myObject = new MyClass("MyObject");
        myObject.setterExample("newValue");

        var myPropertyValue = (String) WorldsFactoryStoryManager.getProperty("myProperty", myObject.objectName);

        Assert.assertEquals("Total cruel", myPropertyValue);

        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();

    }

    @WorldsFactoryClass(className = "MyClass")
    public static class MyClass {
        public final String objectName;

        public MyClass(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        @WorldsFactoryPropertySetter(name = "myProperty")
        public void setterExample(String value) {
            WorldsFactoryStoryManager.setProperty("myProperty", value, objectName);
            System.out.println("Setter example called");
        }
    }
}
