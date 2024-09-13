package com.belafon.worldsfactory.api;

import java.util.Set;

public interface WorldsFactoryStory {

    /**
     * Bind object to the named object in the story data.
     * @param name name of the object in the story data
     * @param object object to bind
     */
    public void bindObject(String name, Object object);

    /**
     * Notifies the story that the property of the object has changed.
     * @param propertyName
     * @param newValue
     * @param pythonObjectName
     */
    public void notifyPropertyChangedInCode(String propertyName, Object newValue, String pythonObjectName);

    /**
     * @return the name of the story
     */
    public String getName();

    /**
     * End the story, it will stop the story and remove all objects from the story data.
     * It will also remove the story object from the {@link com.belafon.world.WorldsFactoryStoriesManager}.
     */
    public void end();

    /**
     * Get property from object in the story data,
     * @param propertyName path to the property from the object name as the root
     * @param objectName name of the object in the story data as the root of the path to the property
     * @return the value of the property
     */
    public Object getProperty(String propertyName, String objectName);

    /**
     * Try to move in event graph, checks all event condition methods,
     * if the first one is true, it moves in the event graph and
     * executes the event action method.
     */
    public void tryToMoveInEventGraph();

    /**
     * Get all object names of the given type in the story data.
     * @param className name of the class in format "@class:ClassName"
     * @return set of object names
     */
    public Set<String> getAllObjectNamesOfType(String className);
}