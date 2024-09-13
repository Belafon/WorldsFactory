package com.belafon.world.calendar;

import java.util.concurrent.PriorityBlockingQueue;

import com.belafon.world.World;
import com.belafon.world.calendar.events.*;

public class Calendar {
    private World game;

    public Calendar(World game) {
        this.game = game;
    }

    // A heap that stores events in chronological order
    public PriorityBlockingQueue<Event> heap = new PriorityBlockingQueue<Event>();

    // Adds an event to the calendar and interrupts the loop thread if necessary
    public void add(Event event) {
        heap.add(event);
        if ((heap.size() == 1 || event == heap.peek())
                && game.calendarsLoop.loopThread != null)

            game.calendarsLoop.loopThread.interrupt();
    }

    public void remove(Event event) {
        Event peekedEvent = heap.peek();
        heap.remove(event);
        if (event == peekedEvent)
            game.calendarsLoop.loopThread.interrupt();
        event.interrupt(game);
    }

    public void check() {
        Event event = heap.peek();
        // if(event.getDate() <= game.time.getTime()){
        heap.remove(event);
        event.action(game);
        // }
    }

    public Event getNextEvent() {
        return heap.peek();
    }
}
