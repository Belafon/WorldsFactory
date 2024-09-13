package com.belafon.worldsfactory.api;

import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.Story;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

public class SetReferenceToOtherObjectFromEventTest {

    @Test
    public void setAndStaticGetPythonStringProperty() {
        String exampleCode = new LoadDataFile().loadCode("testSetReferenceToOtherObjectFromEventTest.py");

        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(false)
                .build();

        var myObject = new MyClass1("myObject");
        story.join().tryToMoveInEventGraph();

        var myPropertyValue = (MyClazz2) WorldsFactoryStoryManager.getProperty("myProperty", myObject.objectName);

        System.out.println(myPropertyValue.testMethod());

        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();
    }

    @WorldsFactoryClass(className = "MyClass3")
    public static class MyClass1 {
        @WorldsFactoryObjectsName
        public final String objectName;

        public MyClass1(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        @WorldsFactoryPropertySetter(name = "myProperty")
        public void setterExample(MyClazz2 value) {
            System.out.println("Setter example called1");
            WorldsFactoryStoryManager.setProperty("myProperty", value, objectName);
            System.out.println("Setter example called2");
        }
    }

    @WorldsFactoryClass(className = "MyClass4")
    public static class MyClazz2 {
        @WorldsFactoryObjectsName
        public final String objectName;

        public MyClazz2(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        public String testMethod() {
            return "testMethod";
        }
    }
}
