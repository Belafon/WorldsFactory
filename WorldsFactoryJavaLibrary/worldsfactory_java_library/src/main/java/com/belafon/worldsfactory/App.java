package com.belafon.worldsfactory;

import com.belafon.worldsfactory.api.EventGraphCondition;
import com.belafon.worldsfactory.api.StoryInitializer;
import com.belafon.worldsfactory.api.WorldsFactoryStoriesManager;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) {

        // tupleTest();
        new App().test();
    }

    public static void tupleTest() {
        String input = "(0, 0, 55, 5)"; // Example input
        String regex = "\\((\\d+)(?:, (\\d+))*\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            System.out.println("The input matches the tuple structure.");

            // Check the number of matched groups
            int expectedNumberOfGroups = countOccurrences(input, ',') + 1;
            if (matcher.groupCount() == expectedNumberOfGroups) {
                System.out.println("The correct number of groups were captured.");

                // Find all groups and print them
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String group = matcher.group(i);
                    if (group != null) {
                        System.out.println("Group " + i + ": " + group);
                    }
                }
            } else {
                System.err.println("Error: The number of captured groups does not match the expected number, which is "
                        + expectedNumberOfGroups + ", but " + matcher.groupCount() + " were captured.");
                for (int i = 0; i <= matcher.groupCount() + 4; i++) {
                    String group = matcher.group(i);
                    if (group != null) {
                        System.out.println("Group " + i + ": " + group.trim());
                    }
                }
            }
        } else {
            System.out.println("The input does not match the tuple structure.");
        }
    }

    private static int countOccurrences(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }

    public void test() {
        String exampleCode = new LoadDataFile().loadCode("testTwoArraysSameClass.py");
        Story.runningTest = true;

        var story = new StoryInitializer()
                .withStoryName("MyStory")
                .withCode(exampleCode)
                .withDebugMode(true)
                .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                .withCheckingConditionsAfterEachSet(true)
                .build();

        var myObject = new MyClass("MyObject");
        var secondObject = new MyClass("SecondObject");

        for (int i = 1; i < 5; i++) {
            myObject.setArrayValue(i, "" + i);
        }

        for (int i = 1; i < 5; i++) {
            secondObject.setArrayValue(i, "" + (i + 5));
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
