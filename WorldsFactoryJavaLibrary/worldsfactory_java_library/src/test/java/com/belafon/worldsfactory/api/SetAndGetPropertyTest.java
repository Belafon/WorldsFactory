package com.belafon.worldsfactory.api;

import org.junit.Assert;
import org.junit.Test;

import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.Story;

public class SetAndGetPropertyTest {

    @Test
    public void setAndStaticGetPythonStringProperty() {
        Story.runningTest = true;
        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .build();


        var myObject = new MyClass("MyObject");
        myObject.setterExample("newValue");

        // TODO: make this templated to avoid worning
        var myPropertyValue = (String) WorldsFactoryStoryManager.getProperty("myProperty", myObject.objectName);
        Assert.assertEquals("newValue", myPropertyValue);

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

    public static String exampleCode = """

            # ------------------- LIBRARY CLASSES -------------------

            class MethodRuntimeException(Exception):
                def __init__(self, message="An exception occurred during method execution"):
                    self.message = message
                    super().__init__(self.message)

            class class_MyClass:
                properties = {
                    "myProperty": "@basicType:String"
                }
                @property
                def myProperty(self):
                    return self._myProperty
                @myProperty.setter
                def myProperty(self, value):
                    self._myProperty = value
                    set_property("myProperty", value, self.properties["myProperty"])

                pass



            # ------------------- LIBRARY OBJECTS -------------------

            class Objects:
                _instance = None
                def __new__(cls):
                    if cls._instance is None:
                        cls._instance = super(Objects, cls).__new__(cls)
                    return cls._instance
                MyObject = class_MyClass()
                pass

            objects = Objects()

            # ------------------- EVENTS -------------------


            class Events:
                _instance = None
                def __new__(cls):
                    if cls._instance is None:
                        cls._instance = super(Events, cls).__new__(cls)
                    return cls._instance
                pass

            events = Events()

            # ------------------- PROPERTY SETTER -------------------

            class LibraryEntryNotSetException(Exception):
                def __init__(self, message="library is not set"):
                    self.message = message
                    super().__init__(self.message)

            def set_property(propertyName, value, property):
                pass

            """;
}
