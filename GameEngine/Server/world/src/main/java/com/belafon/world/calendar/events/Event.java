package com.belafon.world.calendar.events;

import com.belafon.world.World;
import com.belafon.world.time.Clocks;
import com.belafon.world.time.Time;

/**
 * Event of calendar.
 * It has to be comparable by its time, 
 * when the event should happen.
 */
public abstract class Event implements Comparable<Event>{
    private long date;
	protected final int id;

    public Event(long date, World game) {
		this.id = game.visibleIds.getEventId();
		this.date = date;
	}

    /**
     * it is executed by calendar, when 
     * the time comes
     * @param game
     */
    public abstract void action(World game);
	public abstract void interrupt(World game);

    public long getDate() {
        return date;
    }
    
    /**
     * sets the time of the exectution
     * @param dateOfAction
     */
	public synchronized void setDate(long dateOfAction) {
		this.date = dateOfAction;
	}

    public int getId() {
        return id;
    }

    /**
     * compareTo method for sorting the events based on their date
     */
	@Override
    public int compareTo(Event event) {
        return this.getDate() < event.getDate() ? -1 : this.getDate() == event.getDate() ? 0 : 1;
    }

    /**
     * Method for calculating the time to wait until the event should occur
     */
    public long getTimeToWait(Clocks clocks, Time time) {
        return clocks.ticksToMillis(date - time.getTime()) + ((date - time.getTime()) / 12);
    }
}
