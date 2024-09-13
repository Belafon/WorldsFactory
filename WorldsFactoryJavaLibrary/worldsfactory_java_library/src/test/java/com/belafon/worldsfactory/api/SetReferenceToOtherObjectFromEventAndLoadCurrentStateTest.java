package com.belafon.worldsfactory.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import com.belafon.worldsfactory.Story;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

public class SetReferenceToOtherObjectFromEventAndLoadCurrentStateTest {

    @Test
    public void setAndStaticGetPythonStringProperty() {
        String exampleCode = new LoadDataFile()
                .loadCode("testSetReferenceToOtherObjectFromEventAndLoadCurrentStateTest.py");

        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(false)
                .build();

        var myObject = new MyClasss("myObject");
        story.join().tryToMoveInEventGraph();

        var myPropertyValue = (MyClasss2) WorldsFactoryStoryManager.getProperty("myProperty", myObject.objectName);

        Assert.assertEquals("testMethod", myPropertyValue.testMethod());

        WorldsFactoryStoriesManager.endStory(story.join().getName());
        WorldsFactoryStoriesManager.endAllStories();
    }

    @WorldsFactoryClass(className = "MyClass5")
    public static class MyClasss {
        @WorldsFactoryObjectsName
        public final String objectName;

        public MyClasss(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        @WorldsFactoryPropertySetter(name = "myProperty")
        public void setterExample(MyClasss2 value) {
            WorldsFactoryStoryManager.setProperty("myProperty", value, objectName);
            System.out.println("Setter example called");
        }
    }

    @WorldsFactoryClass(className = "MyClass6")
    public static class MyClasss2 {
        @WorldsFactoryObjectsName
        public final String objectName;

        public MyClasss2(String objectName) {
            this.objectName = objectName;
            WorldsFactoryStoryManager.bindObject(objectName, this);
        }

        public String testMethod() {
            return "testMethod";
        }
    }

    public static class LoadDataFile {
        public String loadCode(String fileName) {
            String filePath = "/home/belafon/Documents/projects/bc_thesis_tichavsky/WorldsFactoryJavaLibrary/worldsfactory_java_library/src/test/java/com/belafon/worldsfactory/resources/"
                    + fileName;
            StringBuilder exampleCode = new StringBuilder();
            try {
                File file = new File(filePath);
                return Files.readString(file.toPath());
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                return ""; // Or you might want to throw an exception
            }
        }
    }
}
