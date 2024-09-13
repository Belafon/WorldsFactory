package com.belafon.worldsfactory.api;

import org.junit.Assert;
import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.Story;

public class AccessToPropertyAcrossReferenceTest {
    @Test
    public void testAccessToPropertyAcrossReference() {
        String exampleCode = new LoadDataFile().loadCode("testAccessToPropertyAcrossReferenceData.py");

        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(true)
                .build();

        var myObject = new MyClass("MyObject");
        myObject.firstSetterExample("newValue");

        var secondObject = new MyClass("SecondObject");

        var firstPropertyValue = (String) WorldsFactoryStoryManager.getProperty("firstProperty", myObject.objectName);

        secondObject.secondSetterExample(myObject);
        var secondPropertyValue = (String) WorldsFactoryStoryManager.getProperty("secondProperty.firstProperty",
                secondObject.objectName);

        Assert.assertEquals("Total cruel", secondPropertyValue);
        Assert.assertTrue(secondPropertyValue.equals(firstPropertyValue));

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

        @WorldsFactoryPropertySetter(name = "firstProperty")
        public void firstSetterExample(String value) {
            WorldsFactoryStoryManager.setProperty("firstProperty", value, objectName);
            System.out.println("Setter example called");
        }

        @WorldsFactoryPropertySetter(name = "secondProperty")
        public void secondSetterExample(MyClass myObject) {
            WorldsFactoryStoryManager.setProperty("secondProperty", myObject, objectName);
            System.out.println("Second setter example called");
        }

    }
}
