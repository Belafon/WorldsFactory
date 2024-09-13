package com.belafon.worldsfactory.api;

import org.junit.Assert;
import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.Story;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

public class LoadArrayFromPythonCodeTest {

    @Test
    public void loadArrayFromPythonCode() {
        String exampleCode = new LoadDataFile().loadCode("testLoadArrayAfterInitializeInEvent.py");

        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(false)
                .build();

        var myObject = new A("a");
        story.join().tryToMoveInEventGraph();

        var bObj = (B) WorldsFactoryStoryManager.getProperty("toB", myObject.objectName);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Assert.assertEquals("" + i + j, bObj.array[i][j]);
            }
        }

        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();
    }

    @WorldsFactoryClass(className = "AA")
    public static class A {
        @WorldsFactoryObjectsName
        public final String objectName;

        public A(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        @WorldsFactoryPropertySetter(name = "toB")
        public void setterExample(B value) {
            System.out.println("Setter a example called1");
            WorldsFactoryStoryManager.setProperty("toB", value, objectName);
            System.out.println("Setter a example called2");
        }
    }

    @WorldsFactoryClass(className = "BB")
    public static class B {
        @WorldsFactoryObjectsName
        public final String objectName;

        public B(String objectName) {
            this.objectName = objectName;
        }

        public String[][] array = new String[2][2];

        @WorldsFactoryPropertySetter(name = "array")
        public void setterExample(int i, int j, String value) {
            System.out.println("Setter b array example called1");
            this.array[i][j] = value;
            WorldsFactoryStoryManager.setProperty("array", value, objectName + "[" + i + "][" + j + "]");
            System.out.println("Setter b array example called2");
        }
    }
}
