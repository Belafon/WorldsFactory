package com.belafon.worldsfactory.api;

import org.junit.Assert;
import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.Story;

public class ChangeArrayPropertyFromAnEventsActionTest {
    @Test
    public void test() {
        String exampleCode = new LoadDataFile().loadCode("testChangeArrayPropertyDuringEventsActionExecutionData.py");

        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(false)
                .build();

        var myObject = new MyClass("MyObject");
        myObject.setArrayValue(0, 0, "Set text");
        Assert.assertEquals("Set text", myObject.myArray[0][0]);

        story.join().tryToMoveInEventGraph();
        String value = myObject.myArray[0][0];
        Assert.assertEquals("Array myArray set from myEvent Action method", value);

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
            myArray[x][y] = value;
            WorldsFactoryStoryManager.setProperty("myArray[" + x + "][" + y + "]", value, objectName);
        }
    }
}
