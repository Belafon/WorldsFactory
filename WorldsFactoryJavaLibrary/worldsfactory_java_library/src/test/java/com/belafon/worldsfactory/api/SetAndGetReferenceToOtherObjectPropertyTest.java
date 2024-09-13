package com.belafon.worldsfactory.api;

import org.junit.Assert;
import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.Story;

public class SetAndGetReferenceToOtherObjectPropertyTest {

    @Test
    public void setAndStaticGetPythonStringProperty() {
        String exampleCode = new LoadDataFile().loadCode("testSetAndGetReferenceToOtherObjectProperty.py");

        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(true)
                .build();

        var myObject = new MyClass("myObject");
        var myObject2 = new MyClass2("myObject2");
        myObject.setterExample(myObject2);

        var myPropertyValue = (MyClass2) WorldsFactoryStoryManager.getProperty("myProperty", myObject.objectName);

        Assert.assertEquals("testMethod", myPropertyValue.testMethod());

        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();
    }

    @WorldsFactoryClass(className = "MyClass")
    public static class MyClass {
        @WorldsFactoryObjectsName
        public final String objectName;

        public MyClass(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        @WorldsFactoryPropertySetter(name = "myProperty")
        public void setterExample(MyClass2 value) {
            WorldsFactoryStoryManager.setProperty("myProperty", value, objectName);
            System.out.println("Setter example called");
        }
    }

    @WorldsFactoryClass(className = "MyClass2")
    public static class MyClass2 {
        @WorldsFactoryObjectsName
        public final String objectName;

        public MyClass2(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        public String testMethod() {
            return "testMethod";
        }
    }
}
