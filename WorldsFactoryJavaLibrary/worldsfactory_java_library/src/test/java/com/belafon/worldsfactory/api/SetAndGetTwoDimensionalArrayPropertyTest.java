package com.belafon.worldsfactory.api;

import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.Story;

import org.junit.Assert;

public class SetAndGetTwoDimensionalArrayPropertyTest {
    @Test
    public void test() {
        String exampleCode = new LoadDataFile().loadCode("testSetTwoDimensionalArrayPropertyData.py");
        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(true)
                .build();

        var myObject = new MyClass("MyObject");
        myObject.setArrayValue(0, 0, "Set text");
        String value = myObject.myArray[0][0];
        Assert.assertEquals("Set text", value);
        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();
    }

    @WorldsFactoryClass(className = "MyClass")
    public static class MyClass {
        public final String objectName;

        private String[][] myArray = new String[2][2];

        public MyClass(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        @WorldsFactoryPropertySetter(name = "myArray")
        public void setArrayValue(int x, int y, String value) {
            WorldsFactoryStoryManager.setProperty("myArray[" + x + "][" + y + "]", value, objectName);
            myArray[x][y] = value;
        }
    }
}
