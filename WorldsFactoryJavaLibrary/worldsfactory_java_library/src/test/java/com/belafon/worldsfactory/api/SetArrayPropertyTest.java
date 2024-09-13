package com.belafon.worldsfactory.api;

import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.Story;

public class SetArrayPropertyTest {
    @Test
    public void test() {
        String exampleCode = new LoadDataFile().loadCode("testSetArrayPropertyData.py");
        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(true)
                .build();

        var myObject = new MyClass("MyObject");

        for (int i = 1; i < 5; i++) {
            myObject.setArrayValue(i, "" + i);
        }

        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();
    }

    @WorldsFactoryClass(className = "MyClass")
    public static class MyClass {
        public final String objectName;

        private String[] myArray = new String[5];

        public MyClass(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        @WorldsFactoryPropertySetter(name = "myArray")
        public void setArrayValue(int x, String value) {
            WorldsFactoryStoryManager.setProperty("myArray[" + x + "]", value, objectName);
            myArray[x] = value;
        }
    }
}
