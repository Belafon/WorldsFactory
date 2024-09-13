package com.belafon.worldsfactory.annotations;

import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

import org.junit.Test;

public class WorldsFactoryClassProcessorTest {
    @Test
    public void testAnnotationProcessing() {
        testValue = 0;
    }

    public static int testValue;

    @WorldsFactoryClass(className = "ExampleClass")
    public static class ExampleClass {
        @WorldsFactoryObjectsName
        private String nameOfObject;
        private Object value;

        @WorldsFactoryPropertySetter(name = "value")
        public void setValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }
    }

    @WorldsFactoryClass(className = "ExampleClass", story = "nameOfStory_OPTIONAL")
    public static class ExampleClass2 {
        @WorldsFactoryObjectsName
        private String nameOfObject;

        public ExampleClass2() {
            WorldsFactoryStoryManager.bindObject("ExampleClass", "nameOfStory_OPTIONAL");
        }

        private Object value;

        @WorldsFactoryPropertySetter(name = "value")
        public void setValue(Object value) {
            WorldsFactoryStoryManager.setProperty("value", "ExampleClass", "nameOfStory_OPTIONAL");
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }
    }
}
