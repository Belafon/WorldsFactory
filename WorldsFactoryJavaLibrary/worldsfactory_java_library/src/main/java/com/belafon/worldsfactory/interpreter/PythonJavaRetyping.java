package com.belafon.worldsfactory.interpreter;

import java.util.function.Function;

import com.belafon.worldsfactory.Story.LocateOrCreatePythonObjectArgs;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;

public class PythonJavaRetyping implements IPythonJavaRetyping {
    private Function<LocateOrCreatePythonObjectArgs, Object> locateOrCreateFromPythonObject;

    public PythonJavaRetyping(Function<LocateOrCreatePythonObjectArgs, Object> locateOrCreateFromPythonObject) {
        this.locateOrCreateFromPythonObject = locateOrCreateFromPythonObject;
    }

    @Override
    public String javaObjectToPython(Object javaValue, String storyObjectName) {
        if(javaValue == null)
            return "None";
        if (javaValue instanceof String) {
            return "'" + javaValue.toString() + "'";
        } else if (javaValue instanceof Integer
                || javaValue instanceof Double
                || javaValue instanceof Float
                || javaValue instanceof Long
                || javaValue instanceof Short) {
            return javaValue.toString();
        } else if (javaValue instanceof Boolean) {
            if ((Boolean) javaValue)
                return "True";
            else
                return "False";
        } else {
            return "objects." + storyObjectName;
        }
    }

    private static final String BASIC_TYPE_PREFIX = "@basicType:";
    private static final String OBJECT_TYPE_PREFIX = "@class:";

    @Override
    public Object pythonObjectToJava(String pythonObject, String type) {
        if(pythonObject.equals("None"))
            return null;
        
        return switch (type) {
            case "@basicType:String" -> (Object) pythonObject;
            case "@basicType:Integer" -> Integer.parseInt(pythonObject);
            case "@basicType:Boolean" -> {
                if (pythonObject.equals("True"))
                    yield true;
                else if (pythonObject.equals("False"))
                    yield false;
                else
                    throw new RuntimeException("Unknown boolean value: " + pythonObject);
            }
            default -> {
                if (type.substring(0, OBJECT_TYPE_PREFIX.length()).equals(OBJECT_TYPE_PREFIX)) {
                    var javaObject = locateOrCreateFromPythonObject
                            .apply(new LocateOrCreatePythonObjectArgs(pythonObject, type));
                    if (javaObject != null)
                        yield javaObject;
                    else
                        yield null;
                }
                yield null;
            }
        };
    }

    @Override
    public boolean checkTypeCompatibility(Object javaValue, String pythonType) {
        if(javaValue == null)
            return true;
        return switch (pythonType) {
            case "@basicType:String" -> javaValue instanceof String;
            case "@basicType:Integer" -> javaValue instanceof Integer;
            case "@basicType:Boolean" -> javaValue instanceof Boolean;
            default -> {
                var referenceId = getReferenceIdFromAnnotation(javaValue);
                if (referenceId != null && referenceId.equals(pythonType))
                    yield true;

                handleTypeError(pythonType);
                yield false;
            }
        };
    }

    private boolean handleTypeError(String type) {
        if (type.substring(0, BASIC_TYPE_PREFIX.length()).equals(BASIC_TYPE_PREFIX)) {
            throw new RuntimeException("Unsupported basic type: " + type);
        } else if (type.substring(0, OBJECT_TYPE_PREFIX.length()).equals(OBJECT_TYPE_PREFIX)) {
            throw new RuntimeException("Unsupported object type: " + type);
        } else {
            throw new RuntimeException("Unknown type: " + type);
        }
    }

    private String getReferenceIdFromAnnotation(Object object) {
        Class<?> currentClass = object.getClass();
        
        // find the first class with the annotation WorldsFactoryClass
        while (currentClass != null) {
            if (currentClass.isAnnotationPresent(WorldsFactoryClass.class)) {
                var annotation = currentClass.getAnnotation(WorldsFactoryClass.class);
                var className = annotation.className();
                if (className.charAt(0) != '@')
                    className = "@class:" + className;
    
                return className;
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return null;
    }
}
