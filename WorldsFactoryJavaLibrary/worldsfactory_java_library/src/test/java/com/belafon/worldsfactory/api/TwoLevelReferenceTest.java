package com.belafon.worldsfactory.api;

import org.junit.Test;

import com.belafon.worldsfactory.LoadDataFile;
import com.belafon.worldsfactory.Story;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

public class TwoLevelReferenceTest {

    @Test
    public void setAndStaticGetPythonStringProperty() {
        String exampleCode = new LoadDataFile().loadCode("testTwoLevelReference.py");

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
        var cObj = bObj.toC;

        System.out.println(cObj.testMethod());
        System.out.println(cObj.cProperty);

        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();
    }

    @WorldsFactoryClass(className = "A")
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

    @WorldsFactoryClass(className = "B")
    public static class B {
        @WorldsFactoryObjectsName
        public final String objectName;

        public B(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        public C toC;

        @WorldsFactoryPropertySetter(name = "toC")
        public void setterExample(C value) {
            System.out.println("Setter b example called1");
            this.toC = value;
            WorldsFactoryStoryManager.setProperty("toC", value, objectName);
            System.out.println("Setter b example called2");
        }
    }

    @WorldsFactoryClass(className = "C")
    public static class C {
        @WorldsFactoryObjectsName
        public final String objectName;

        public C(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        public String cProperty;

        @WorldsFactoryPropertySetter(name = "cProperty")
        public void setterExample(String value) {
            System.out.println("Setter c example called1");
            this.cProperty = value;
            WorldsFactoryStoryManager.setProperty("cProperty", value, objectName);
            System.out.println("Setter c example called2");
        }

        public String testMethod() {
            return "testMethod";
        }
    }
}
