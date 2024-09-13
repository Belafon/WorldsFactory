package com.belafon.worldsfactory.interpreter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.ArrayList;

import com.belafon.worldsfactory.Story.LocateOrCreatePythonObjectArgs;
import com.belafon.worldsfactory.api.EventGraphCondition;

public class PythonInterpreterEntry {
    private PythonInterpreter interpreter;
    private QuadConsumer<String, String, Object, List<Integer>> onPropertyChanged;
    public boolean debugMode = false;
    private IPythonJavaRetyping retyper;
    private EventGraphCondition CurrentEventGraphCondition = EventGraphCondition.MOVE_MAX_BY_ONE;
    private boolean checkingConditionsAfterEachSet = false;
    public boolean isEventGraphHeadMoving = false;
    public volatile boolean isClosed = false;

    @FunctionalInterface
    public static interface QuadConsumer<T, U, V, W> {
        void accept(T t, U u, V v, W w);
    }

    /**
     * Creates a new python interpreter entry, provides interface between java
     * library and python worlds factory code
     * provides retyping
     * 
     * @param code
     * @param onPropertyChanged
     * @param debugMode         if true, it will make debug checks, that can slow
     *                          down the code
     */
    private PythonInterpreterEntry(Builder initializer) {
        this.debugMode = initializer.isDebugMode();
        this.checkingConditionsAfterEachSet = initializer.isCheckingConditionsAfterEachSet();
        this.onPropertyChanged = initializer.getOnPropertyChanged();
        this.retyper = new PythonJavaRetyping(initializer.locateOrCreateFromPythonObject);
        this.interpreter = new PythonInterpreter();

        this.interpreter.sendCommand(initializer.getCode());
        setCurrentEventGraphCondition(initializer.getCurrentEventGraphCondition());

        if (debugMode) {
            String classes = this.interpreter.sendCommandWithOutput("print(dir())");
            System.out.println(classes);
        }

    }

    /**
     * sets configuration for moving in event graph
     * 
     * @param EventGraphCondition
     */
    public synchronized void setCurrentEventGraphCondition(EventGraphCondition EventGraphCondition) {
        this.CurrentEventGraphCondition = EventGraphCondition;
        this.interpreter.sendCommand("event_tree_condition = EventGraphCondition." + EventGraphCondition.name());
    }

    public synchronized EventGraphCondition getCurrentEventGraphCondition() {
        return this.CurrentEventGraphCondition;
    }

    private Pattern propertyNamePattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)(((\\[\\d\\])*)*)$");

    public synchronized void setProperty(String pythonPropertyName, Object javaValue, String pythonObjectName,
            String pythonTagetObjectName)
            throws RuntimeException {
        if (isEventGraphHeadMoving)
            return;

        if (debugMode) {

            if (!this.hasObjectAttribute("objects", pythonObjectName))
                throw new RuntimeException("Class " + pythonObjectName + " not found in python code");

            if (pythonPropertyName.charAt(pythonPropertyName.length() - 1) != ']') {
                // property is not an array
                if (!this.doesPropertyExist("objects." + pythonObjectName, pythonPropertyName))
                    throw new RuntimeException("Property " + pythonPropertyName + " not found in python code");

                String pythonType = this.getPropertyType(pythonPropertyName, pythonObjectName);
                if (!this.retyper.checkTypeCompatibility(javaValue, pythonType))
                    throw new RuntimeException(
                            "Type mismatch " + javaValue.getClass().getName() + " and " + pythonType);
            } else {
                // property is an array
                Matcher matcher = propertyNamePattern.matcher(pythonPropertyName);
                if (matcher.matches()) {
                    System.out.println("Name: " + matcher.group(1));
                    System.out.println("Brackets: " + matcher.group(2));
                    var nameFirstPart = matcher.group(1);
                    // var brackets = matcher.group(2);

                    if (!this.doesPropertyExist("objects." + pythonObjectName, nameFirstPart))
                        throw new RuntimeException("Array property " + nameFirstPart + " not found in python code");

                    String pythonType = this.getPropertyType(pythonPropertyName, pythonObjectName);
                    if (!this.retyper.checkTypeCompatibility(javaValue, pythonType))
                        throw new RuntimeException(
                                "Type mismatch " + javaValue.getClass().getName() + " and " + pythonType);
                    // TODO check if array has right dimensions

                } else {
                    throw new RuntimeException(
                            "An array Name " + pythonPropertyName + " does not match propertys name structure.");
                }

            }

        }

        String pythonValue = retyper.javaObjectToPython(javaValue, pythonTagetObjectName);

        var oldPythonValue = this.getProperty(pythonPropertyName, pythonObjectName);

        if (pythonPropertyName.charAt(pythonPropertyName.length() - 1) == ']') {
            // property is an array
            var list = pythonPropertyName.substring(0, pythonPropertyName.length() - 3);
            var setDirectAccessCommand = "objects." + pythonObjectName + "." + list + ".direct_access = True";
            var setCommand = "objects." + pythonObjectName + "." + pythonPropertyName + " = " + pythonValue;
            this.interpreter.sendCommandWithOutput(setDirectAccessCommand + "\n" + setCommand);
        } else {
            // property is regular property
            // add the underscore to the name, to avoid calling the setter
            var setCommand = "objects." + pythonObjectName + "._" + pythonPropertyName + " = " + pythonValue;
            this.interpreter.sendCommandWithOutput(setCommand);
        }

        var newPythonValue = this.getProperty(pythonPropertyName, pythonObjectName);

        if (oldPythonValue == null && newPythonValue == null) {
            return;
        }

        if (oldPythonValue != null && newPythonValue != null) {
            if (oldPythonValue.equals(newPythonValue)) {
                return;
            }
        }

        if (this.checkingConditionsAfterEachSet) {
            this.checkEventConditionsAndMoveInEventGraph();
        }
    }

    /**
     * Tells if an python object has a property
     * 
     * @param object
     * @param property
     * @return
     */
    private synchronized boolean hasObjectAttribute(String object, String property) {

        String doesPropertyExist = this.interpreter
                .sendCommandWithOutput("print(hasattr(" + object + ", '" + property + "'))");
        return doesPropertyExist.equals("True");
    }

    /**
     * Tells if a property exists in a python class
     * 
     * @param classObject
     * @param propertyName
     * @return
     */
    public synchronized boolean doesPropertyExist(String classObject, String propertyName) {
        String command = "print(\"" + propertyName + "\" in " + classObject + ".properties)";
        String doesPropertyExist = this.interpreter.sendCommandWithOutput(command);
        var doesPropExistBool = doesPropertyExist.equals("True");
        if (!doesPropExistBool)
            System.out.println("Property " + propertyName + " does not exist in " + classObject);
        return doesPropExistBool;
    }

    /**
     * Tells if a field exists in a object
     * It uses pythons dir function and
     * then checks if the field is in the list
     * 
     * @param classObject
     * @param fieldName
     * @return
     */
    public synchronized boolean doesFieldExistInContext(String classObject, String fieldName) {

        String command = "print(\"" + fieldName + "\" in " + "dir(" + classObject + "))";

        String doesPropertyExist = this.interpreter.sendCommandWithOutput(command);

        // String printCommand = "print(dir(" + classObject + "))";
        // String print = this.interpreter.sendCommandWithOutput(printCommand);
        // System.out.println(print);

        return doesPropertyExist.equals("True");
    }

    /**
     * Checks if a variable exists in the global scope.
     * It uses pythons dir function and
     * then checks if the field is in the list
     * 
     * @param typeName
     * @return
     */
    public synchronized boolean checkIfExistsGlobal(String typeName) {

        String doesPropertyExist = this.interpreter.sendCommandWithOutput("print(\"" + typeName + "\" in dir())");

        if (isClosed)
            return true;

        return doesPropertyExist.equals("True");
    }

    public synchronized Object getProperty(String propertyName, String objectName) {
        if (debugMode) {
            if (!this.hasObjectAttribute("objects", objectName))
                throw new RuntimeException("Class " + objectName + " not found in python code");

            var path = new StringBuilder("objects." + objectName);
            for (var name : propertyName.split("\\.")) {
                // remove brackets
                var nameWithoutBrackets = name.replaceAll("\\[.*\\]", "");
                var pathInStr = path.toString();
                if (!this.doesPropertyExist(pathInStr, nameWithoutBrackets))
                    throw new RuntimeException("Property " + nameWithoutBrackets
                            + " not found in python code, with this path used: " + pathInStr);
                path.append("." + name);
            }
        }

        String getCommand = "print(objects." + objectName + "." + propertyName + ")";
        String pythonValue = this.interpreter.sendCommandWithOutput(getCommand);
        String propertyType = this.getPropertyType(propertyName, objectName);
        Object javaValue = retyper.pythonObjectToJava(pythonValue, propertyType);
        return javaValue;
    }

    private Pattern getPropertyPathPattern;
    {
        var regex = "((?:[a-zA-Z0-9_]+(?:\\[[0-9]*\\])*)\\.)*([a-zA-Z0-9_]+)(?:\\[.*\\])*";
        getPropertyPathPattern = Pattern.compile(regex);
    }

    public synchronized String getPropertyType(String propertyPathWithoutFirstObject, String objectName) {
        if (debugMode) {
            if (!this.hasObjectAttribute("objects", objectName))
                throw new RuntimeException("Class " + objectName + " not found in python code");

            var path = new StringBuilder("objects." + objectName);
            for (var name : propertyPathWithoutFirstObject.split("\\.")) {
                // remove brackets
                var nameWithoutBrackets = name.replaceAll("\\[.*\\]", "");
                var pathInStr = path.toString();
                if (!this.doesPropertyExist(pathInStr, nameWithoutBrackets))
                    throw new RuntimeException("Property " + nameWithoutBrackets
                            + " not found in python code, with this path used: " + pathInStr);
                path.append("." + name);
            }

        }

        var match = getPropertyPathPattern.matcher(propertyPathWithoutFirstObject);
        if (!match.find())
            throw new RuntimeException("Property path " + propertyPathWithoutFirstObject + " does not match regex");

        var propertyName = match.group(2);
        var partOfPath = match.group(1);
        
        if(propertyName.equals("__class__") || propertyName.equals("__name__")) {
            return "@basicType:String";
        } else {
            var wholePropertyPath = new StringBuilder("objects." + objectName + ".");
            if (partOfPath != null) {
                partOfPath = partOfPath.substring(0, partOfPath.length() - 1);
                wholePropertyPath.append(partOfPath + ".");
            }
    
            String getTypeCommand = "print(" + wholePropertyPath + "properties[\"" + propertyName + "\"])";
            return this.interpreter.sendCommandWithOutput(getTypeCommand);
        }
            
    }

    public synchronized void close() {

        isClosed = true;
        this.interpreter.close();
    }

    public synchronized void checkEventConditionsAndMoveInEventGraph() {
        System.out.println("Checking event conditions and moving in event tree");
        isEventGraphHeadMoving = true;
        String command = "event_tree_head.try_move()";
        System.out.println("Command: " + command);
        var message = this.interpreter.sendCommandWithOutputSeparatedLines(command);
        System.out.println("Message: " + message);
        handleStreamOfMessagesFromPythonProcessAfterMessageSent(message);
        isEventGraphHeadMoving = false;
    }

    private void handleStreamOfMessagesFromPythonProcessAfterMessageSent(List<String> message) {
        var iterator = message.iterator();
        while (iterator.hasNext()) {
            var line = iterator.next();
            if (line.equals("_exception_")) {
                System.out.println("Exception");
                var exceptionMessage = new StringBuilder("\n" + iterator.next());
                var class_ = iterator.next();
                var method = iterator.next();
                while (iterator.hasNext()) { // lets get whole exception message
                    var exceptionLine = iterator.next();
                    if (exceptionLine.equals("_exception_end_")) {
                        break;
                    }
                    exceptionMessage.append("\n" + exceptionLine);
                }

                isEventGraphHeadMoving = false;
                if (debugMode)
                    throw new RuntimeClassMethodException(exceptionMessage.toString(), class_, method);
            } else if (line.equals("_set_property_")) {
                var propertyName = iterator.next();
                var valueType = iterator.next();
                var objectName = iterator.next();
                var pythonValue = new StringBuilder(iterator.next());
                String nextLine = "";
                while (iterator.hasNext()) {
                    nextLine = iterator.next();
                    if (nextLine.equals("_set_property_end_")) {
                        break;
                    }
                    pythonValue.append("\n" + nextLine);
                }
                var value = retyper.pythonObjectToJava(pythonValue.toString(), valueType);

                onPropertyChanged.accept(objectName, propertyName, value, new ArrayList<>());

                var end = nextLine.equals("_set_property_end_") || !iterator.hasNext() ? nextLine : iterator.next();
                if (!end.equals("_set_property_end_")) {
                    throw new RuntimeException("Expected _end_ but got " + end);
                }
            } else if (line.equals("_set_array_property_")) {
                var propertyName = iterator.next();
                var valueType = iterator.next();
                var objectName = iterator.next();
                var pythonValue = new StringBuilder(iterator.next());
                var nextLine = "";
                String indexes = null;

                if (nextLine.length() > 2
                        && nextLine.charAt(0) == '('
                        && nextLine.charAt(nextLine.length() - 1) == ')') {
                    indexes = nextLine;
                    break;
                }

                while (iterator.hasNext()) {
                    nextLine = iterator.next();

                    if (nextLine.length() > 2
                            && nextLine.charAt(0) == '('
                            && nextLine.charAt(nextLine.length() - 1) == ')') {
                        indexes = nextLine;
                        break;
                    }

                    if (nextLine.equals("_set_array_property_end_")) {
                        break;
                    }
                    pythonValue.append("\n" + nextLine);
                }

                if (indexes == null && iterator.hasNext()) {
                    indexes = iterator.next();
                }

                List<Integer> indexesList = new ArrayList<>();
                var indexesMatcher = Pattern.compile("\\((\\d+)(?:, (\\d+))*(?:,)*\\)").matcher(indexes);
                if (indexesMatcher.matches()) {
                    for (var i = 1; i <= indexesMatcher.groupCount(); i++) {
                        if (indexesMatcher.group(i) == null)
                            break;
                        var index = indexesMatcher.group(i);
                        indexesList.add(Integer.parseInt(index));
                    }
                } else {
                    throw new RuntimeException("Indexes " + indexes + " does not match regex");
                }
                var value = retyper.pythonObjectToJava(pythonValue.toString(), valueType);

                onPropertyChanged.accept(objectName, propertyName, value, indexesList);

                var end = iterator.next();
                if (!end.equals("_set_array_property_end_")) {
                    throw new RuntimeException("Expected _end_ but got " + end);
                }
            } else {
            }
        }
    }

    public static class RuntimeClassMethodException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private String classId;
        private String methodId;

        public RuntimeClassMethodException(String message, String classId, String methodId) {
            super(message);
            this.classId = classId;
            this.methodId = methodId;
        }

        public String getClassId() {
            return classId;
        }

        public String getMethodId() {
            return methodId;
        }
    }

    public void loadProperty(String pythonPropertyName, String objectName, String pythonTargetObjectName) {
        if (debugMode) {
            if (!this.hasObjectAttribute("objects", objectName))
                throw new RuntimeException("Class " + objectName + " not found in python code");

            if (!this.doesPropertyExist("objects." + objectName, pythonPropertyName))
                throw new RuntimeException("Property " + pythonPropertyName + " not found in python code in object "
                        + objectName);
        }

        Object value = getProperty(pythonPropertyName, objectName);
        setProperty(pythonPropertyName, value, objectName, pythonTargetObjectName);
    }

    public static class Builder {
        private boolean debugMode = false;
        private EventGraphCondition CurrentEventGraphCondition = EventGraphCondition.MOVE_MAX_BY_ONE;
        private boolean checkingConditionsAfterEachSet = false;
        private QuadConsumer<String, String, Object, List<Integer>> onPropertyChanged;
        private String code;
        private Function<LocateOrCreatePythonObjectArgs, Object> locateOrCreateFromPythonObject;

        public Builder withDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
            return this;
        }

        public Builder withEventGraphCondition(EventGraphCondition EventGraphCondition) {
            this.CurrentEventGraphCondition = EventGraphCondition;
            return this;
        }

        public Builder withCheckingConditionsAfterEachSet(boolean checkingConditionsAfterEachSet) {
            this.checkingConditionsAfterEachSet = checkingConditionsAfterEachSet;
            return this;
        }

        public Builder withOnPropertyChanged(QuadConsumer<String, String, Object, List<Integer>> onPropertyChanged) {
            this.onPropertyChanged = onPropertyChanged;
            return this;
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withLocateOrCreateFromPythonObject(
                Function<LocateOrCreatePythonObjectArgs, Object> locateOrCreateFromPythonObject) {
            this.locateOrCreateFromPythonObject = locateOrCreateFromPythonObject;
            return this;
        }

        public QuadConsumer<String, String, Object, List<Integer>> getOnPropertyChanged() {
            return onPropertyChanged;
        }

        public String getCode() {
            return code;
        }

        public boolean isCheckingConditionsAfterEachSet() {
            return checkingConditionsAfterEachSet;
        }

        public EventGraphCondition getCurrentEventGraphCondition() {
            return CurrentEventGraphCondition;
        }

        public boolean isDebugMode() {
            return debugMode;
        }

        public PythonInterpreterEntry build() {
            return new PythonInterpreterEntry(this);
        }
    }

    public boolean isProperyArray(String propertyName, String objectName) {
        var command = "print(isinstance(objects." + objectName + "." + propertyName
                + ", CustomList) or isinstance(objects." + objectName + "." + propertyName + ", ArrayObject))";
        var result = this.interpreter.sendCommandWithOutput(command);
        return result.equals("True");
    }

    public List<Integer> getArrayDimensions(String propertyName, String objectName) {
        var command = "print(objects." + objectName + "." + propertyName + ".dimensions)";
        var dimStr = this.interpreter.sendCommandWithOutput(command);
        // the pattern can be [0], [2, 2], ...
        var dimStrMatcher = Pattern.compile("\\((\\d+)(?:,\\s*(\\d+))*\\)").matcher(dimStr);
        var dimStrMatcherWithBrackets = Pattern.compile("\\[(\\d+)(?:,\\s*(\\d+))*\\]").matcher(dimStr);

        List<Integer> dimensions = new ArrayList<>();
        if (dimStrMatcher.matches()) {
            var groupsCount = dimStrMatcher.groupCount();
            for (var i = 1; i <= groupsCount; i++) {
                if (dimStrMatcher.group(i) == null)
                    break;
                var dim = dimStrMatcher.group(i);
                dimensions.add(Integer.parseInt(dim));
            }
        } else if (dimStrMatcherWithBrackets.matches()) {
            var groupsCount = dimStrMatcherWithBrackets.groupCount();
            for (var i = 1; i <= groupsCount; i++) {
                if (dimStrMatcherWithBrackets.group(i) == null)
                    break;
                var dim = dimStrMatcherWithBrackets.group(i);
                dimensions.add(Integer.parseInt(dim));
            }
        } else {
            throw new RuntimeException("Dimensions " + dimStr + " does not match regex");
        }
        return dimensions;
    }

    public Set<String> getAllObjectNamesOfType(String className) {
        var command = "print(objects.get_all_object_names_of_type('" + className + "'))";
        var result = this.interpreter.sendCommandWithOutput(command);
        // split the result, it is in format pythons array of strings
        var names = result.split(",");
        return Set.of(names);
    }
}
