package com.belafon.worldsfactory;

import com.belafon.worldsfactory.api.StoryInitializer;
import com.belafon.worldsfactory.api.WorldsFactoryStoriesManager;
import com.belafon.worldsfactory.api.WorldsFactoryStory;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.worldsfactory.interpreter.PythonInterpreterEntry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class Story implements WorldsFactoryStory {
    public static boolean runningTest = false;
    private String name;
    private PythonInterpreterEntry pythonInterpreterEntry;
    private Map<String, Method> setters = new HashMap<>();
    private Map<String, Object> objectsMap = new HashMap<>();
    private Map<Object, String> objectsNames = new HashMap<>();
    private Map<String, Object> javaObjectsByName = new HashMap<>();
    private Map<String, Class<?>> allAnnotatedClasses = new HashMap<>();

    private Set<String> packagesToLocateSources;

    public Story(StoryInitializer initializer, String code) {

        this.name = initializer.getStoryName();
        this.packagesToLocateSources = initializer.getPackagesToLocateSources();

        findAllAnnotatedClassesWithAutoLoading();

        this.pythonInterpreterEntry = new PythonInterpreterEntry.Builder()
                .withCode(code)
                .withDebugMode(initializer.isDebugMode())
                .withEventGraphCondition(initializer.getCurrentEventGraphCondition())
                .withCheckingConditionsAfterEachSet(initializer.isCheckingConditionsAfterEachSet())
                .withOnPropertyChanged(this::setPropertyFromPython)
                .withLocateOrCreateFromPythonObject((locateOrCreatePythonObjectsArgs) -> this
                .locateOrCreateFromPythonObject(locateOrCreatePythonObjectsArgs))
                .build();
    }

    private void findAllAnnotatedClassesWithAutoLoading() {
        var packages = getClass().getClassLoader().getDefinedPackages();
        for (var pack : packages) {

            if (packages.length > 0
                    && !packagesToLocateSources.contains(pack.getName())
                    && packagesToLocateSources.size() != 0)
                continue;

            var reflections = new Reflections(pack.getName(), Scanners.TypesAnnotated);

            var annotated = reflections.getTypesAnnotatedWith(WorldsFactoryClass.class);
            for (Class<?> clazz : annotated) {
                var annotation = clazz.getAnnotation(WorldsFactoryClass.class);
                var className = annotation.className();

                if (!allAnnotatedClasses.containsKey(className)) {
                    if (annotation.autoRegister())
                        allAnnotatedClasses.put(className, clazz);
                } else if (allAnnotatedClasses.get(className) != clazz && !runningTest) {
                    if (pythonInterpreterEntry.isClosed)
                        return;

                    throw new RuntimeException("Two classes with the same name " + className + " found");
                }
            }
        }
    }

    private Object locateOrCreateFromPythonObject(LocateOrCreatePythonObjectArgs args) {
        // remove @object: from the name
        var pythonObjectName = args.pythonObjecId.replaceAll("^@object:", "");
        var typeName = args.type.replaceAll("^@class:", "");

        if (pythonObjectName.equals("None"))
            return null;

        if (pythonObjectName.equals(""))
            return null;

        if (javaObjectsByName.containsKey(pythonObjectName))
            return javaObjectsByName.get(pythonObjectName);

        if (!allAnnotatedClasses.containsKey(typeName))
            findAllAnnotatedClassesWithAutoLoading(); // TODO // BUG this could be very inefficient

        if (allAnnotatedClasses.containsKey(typeName)) {
            try {
                var clazz = allAnnotatedClasses.get(typeName);

                Object object = null;
                try {
                    var implicitContructor = clazz.getConstructor();
                    object = implicitContructor.newInstance();
                } catch (NoSuchMethodException e) {
                    try {
                        var constructorWithName = clazz.getConstructor(String.class);
                        object = constructorWithName.newInstance(pythonObjectName);
                    } catch (NoSuchMethodException e2) {
                        throw new RuntimeException("Class " + typeName
                                + " does not have a constructor with no arguments or with one String argument");
                    }
                }

                if (object == null)
                    throw new RuntimeException("Object could not be created, not implicit constructor located");

                // lets set the name property if property with annotation
                // WorldsFactoryObjectsName is present
                var properties = clazz.getDeclaredFields();
                Field nameProperty;
                for (var property : properties) {
                    if (property.isAnnotationPresent(WorldsFactoryObjectsName.class)) {
                        nameProperty = property;
                        nameProperty.setAccessible(true);
                        nameProperty.set(object, pythonObjectName);
                    }
                }

                bindObject(pythonObjectName, object);

                // find all setters with annotation and get the property
                //loadAllPropertiesWithSetterMethod(pythonObjectName, clazz); // TODO check if we can rid of this without any bug

                return object;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void loadAllPropertiesWithSetterMethod(String pythonObjectName, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WorldsFactoryPropertySetter.class)) {
                WorldsFactoryPropertySetter setterAnnotation = method.getAnnotation(WorldsFactoryPropertySetter.class);
                var pythonPropertyName = setterAnnotation.name();
                loadProperty(pythonPropertyName, pythonObjectName);
            }
        }
    }

    public static class LocateOrCreatePythonObjectArgs {
        public final String pythonObjecId;
        public final String type;

        public LocateOrCreatePythonObjectArgs(String pythonObjecId, String type) {
            this.pythonObjecId = pythonObjecId;
            this.type = type;
        }
    }

    /**
     * 
     * @param objectName with prefix @class:
     * @param propertyName 
     * @param value
     * @param indexes
     * @param loadingOnly  tells if is called from property setter, or just property
     *                     loading
     */
    private void setPropertyFromPython(String objectName, String propertyName, Object value, List<Integer> indexes) {
        if(!objectName.startsWith("@object:")) {
            objectName = "@object:" + objectName;
        }

        var localSetterId = objectName + "_" + propertyName;
        var setter = setters.get(localSetterId);
        if (setter == null)
            return;
        var object = objectsMap.get(localSetterId);
        Object[] indexesArray = indexes.toArray();
        Object[] params = new Object[indexesArray.length + 1];
        System.arraycopy(indexesArray, 0, params, 0, indexesArray.length);
        params[params.length - 1] = value;

        try {
            setter.invoke(object, params);
            // ...
        } catch (Exception e) {
            if (pythonInterpreterEntry.isClosed)
                return;

            throw new RuntimeException(e);
        }
    }

    public void bindObject(String objectStoryName, Object object) {
        if (!this.pythonInterpreterEntry.checkIfExistsGlobal("objects")) {
            throw new RuntimeException("Global variable objects not found in story generated code");
        } else if (!this.pythonInterpreterEntry.checkIfExistsGlobal("events")) {
            throw new RuntimeException("Global variable events not found in story generated code");
        } else if (!this.pythonInterpreterEntry.doesFieldExistInContext("objects", objectStoryName)) {
            throw new RuntimeException("Object " + objectStoryName + " not found in story generated code");
        } else {
            Class<?> classWithAnnotation = findClassWithAnnotation(object.getClass());
            if (classWithAnnotation == null) {
                throw new RuntimeException("No class in the hierarchy of " + object.getClass().getSimpleName() + 
                                           " has the WorldsFactoryClass annotation");
            }
    
            WorldsFactoryClass objectAnnotation = classWithAnnotation.getAnnotation(WorldsFactoryClass.class);
            String className = objectAnnotation.className();
            String regex = "^@class:(.*)$";
            boolean match = className.matches(regex);
            if (match) {
                className = className.replaceAll(regex, "$1");
            }
    
            if (!this.pythonInterpreterEntry.checkIfExistsGlobal("class_" + className)) {
                throw new RuntimeException("Class " + className + " not found in story generated code");
            } else {
                this.objectsNames.put(object, objectStoryName);
                this.javaObjectsByName.put(objectStoryName, object);
    
                processClassHierarchy(object, objectStoryName, className);
            }
        }
    }
    
    private Class<?> findClassWithAnnotation(Class<?> clazz) {
        while (clazz != null) {
            if (clazz.isAnnotationPresent(WorldsFactoryClass.class)) {
                return clazz;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
    
    private void processClassHierarchy(Object object, String storyName, String className) {
        Class<?> currentClass = object.getClass();
        while (currentClass != null) {
            processClassMethods(object, storyName, className, currentClass);
            this.loadAllPropertiesWithSetterMethod(storyName, currentClass);
            currentClass = currentClass.getSuperclass();
        }
    }
    
    private void processClassMethods(Object object, String storyName, String className, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WorldsFactoryPropertySetter.class)) {
                String propertyName = method.getName();
                WorldsFactoryPropertySetter setterAnnotation = method.getAnnotation(WorldsFactoryPropertySetter.class);
                if (!setterAnnotation.name().isEmpty()) {
                    propertyName = setterAnnotation.name();
                }
    
                boolean initialize = setterAnnotation.initialize();
                if (initialize) {
                    if (!this.pythonInterpreterEntry.doesPropertyExist("objects." + storyName, propertyName)) {
                        throw new RuntimeException("Property " + propertyName + 
                                                   " not found in story generated code in class " + className);
                    }
                    var objectNameWithPrefix = storyName;
                    if(!storyName.startsWith("@object:")) {
                        objectNameWithPrefix = "@object:" + storyName;
                    }
                    var localSetterId = objectNameWithPrefix + "_" + propertyName;
                    this.setters.put(localSetterId, method);
                    this.objectsMap.put(localSetterId, object);
                }
            }
        }
    }

    public void notifyPropertyChangedInCode(String propertyName, Object newValue, String pythonObjectName) {
        this.pythonInterpreterEntry.setProperty(propertyName, newValue, pythonObjectName, objectsNames.get(newValue));
    }

    public String getName() {
        return name;
    }

    public void end() {
        if (!this.pythonInterpreterEntry.isClosed) {
            this.pythonInterpreterEntry.close();
            WorldsFactoryStoriesManager.endStory(this);
        }
    }

    public Object getProperty(String propertyName, String objectName) {
        return this.pythonInterpreterEntry.getProperty(propertyName, objectName);
    }

    /**
     * TODO implement support for general size of array
     * Load property from python to java object.
     * 
     * @param pythonPropertyName
     * @param objectName
     */
    public void loadProperty(String pythonPropertyName, String objectName) {
        if (!pythonInterpreterEntry.isProperyArray(pythonPropertyName, objectName)) {
            Object value = getProperty(pythonPropertyName, objectName);
            if (value != null) {
                setPropertyFromPython(objectName, pythonPropertyName, value, List.of());
            }
        } else {
            List<Integer> dimensions = pythonInterpreterEntry.getArrayDimensions(pythonPropertyName, objectName);
            // set each item in the n dimensional array
            if (dimensions.size() == 1) {
                for (int i = 0; i < dimensions.get(0); i++) {
                    Object value = getProperty(pythonPropertyName + "[" + i + "]", objectName);
                    if (value != null) {
                        setPropertyFromPython(objectName, pythonPropertyName, value, List.of(i));
                    }
                }
            } else if (dimensions.size() == 2) {
                for (int i = 0; i < dimensions.get(0); i++) {
                    for (int j = 0; j < dimensions.get(1); j++) {
                        Object value = getProperty(pythonPropertyName + "[" + i + "][" + j + "]", objectName);
                        if (value != null) {
                            setPropertyFromPython(objectName, pythonPropertyName, value, List.of(i, j));
                        }
                    }
                }
            } else if (dimensions.size() == 3) {
                for (int i = 0; i < dimensions.get(0); i++) {
                    for (int j = 0; j < dimensions.get(1); j++) {
                        for (int k = 0; k < dimensions.get(2); k++) {
                            Object value = getProperty(pythonPropertyName + "[" + i + "][" + j + "][" + k + "]",
                                    objectName);
                            if (value != null) {
                                setPropertyFromPython(objectName, pythonPropertyName, value, List.of(i, j, k));
                            }
                        }
                    }
                }
            } else {
                if (this.pythonInterpreterEntry.debugMode)
                    throw new RuntimeException("Array with more than 3 dimensions not supported");
            }
        }
    }

    /**
     * Try to move in event graph, checks all event condition methods, if the first
     * one is true, it moves in the event graph and executes the event action
     * method.
     */
    public void tryToMoveInEventGraph() {
        this.pythonInterpreterEntry.checkEventConditionsAndMoveInEventGraph();
    }

    public String getObjectName(Object object) {
        return objectsNames.get(object);
    }

    /**
     * Get all object names of the given type in the story data.
     * @param className name of the class in format "@class:ClassName"
     * @return set of object names
     */
    @Override
    public Set<String> getAllObjectNamesOfType(String className) {
        className = className.replaceFirst("^@class:", "");
        className = "class_" + className;
        var output = pythonInterpreterEntry.getAllObjectNamesOfType(className);
        if(output.size() == 1 && output.contains("[]")) {
            return new HashSet<>();
        }
        return output;
    }
}
