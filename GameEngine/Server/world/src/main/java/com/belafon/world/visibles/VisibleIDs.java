package com.belafon.world.visibles;

/**
 * Class for generating unique ids. 
 * The instance is located in World object,
 * so each world has its own ids.
 */
public class VisibleIDs {
    /**
     * returns unique id for a creature
     */
    private int nextCreatureId = 0;
    public synchronized int getCreatureId() {
        return nextCreatureId++;
    }
    
    /**
     * returns unique id for an item
     */
    private int nextItemId = 0;
    public synchronized int getItemId() {
        return nextItemId++;
    }
    
    /**
     * returns unique id for a resource
     */
    private int nextResourceId = 0;
    public synchronized int getResourceId() {
        return nextResourceId++;
    }
    
    private int nextPlaceId = 0;
    
    /**
     * returns unique id for a place
     */
    public int getPlaceId() {
        return nextPlaceId++;
    }
    
    /**
     * returns unique id for an event
     */
    private int nextEventId = 0;
    public int getEventId() {
        return nextEventId++;
    }

}
