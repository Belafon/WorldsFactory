package com.belafon.worldsfactory.interpreter;


public interface IPythonJavaRetyping {
    public String javaObjectToPython(Object object, String javaObjectsName);
    public Object pythonObjectToJava(String object, String type);
    public boolean checkTypeCompatibility(Object javaValue, String pythonType);
}
