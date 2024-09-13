package com.belafon.world.time;

import com.belafon.console.ConsolePrint;
import com.belafon.server.Server;
import com.belafon.world.World;
import com.belafon.world.calendar.events.Event;

/**
 * The CalendaryLoop class implements a thread that continuously loops through events in the game calendar,
 * waiting for each event's scheduled time to be reached and then triggering the event. 
 */
public class CalendaryLoop implements Runnable{
    private World game;
    public Thread loopThread;

    public CalendaryLoop(World game){
        this.game = game;
    }

    public volatile boolean logLoopThread = false;

    @Override
    public void run() {
        loopThread = Thread.currentThread();
        Thread.currentThread().setName("CalendaryLoop");
        
        while(game.isRunning){
            Thread.interrupted();
            Event nextEvent = game.calendar.getNextEvent();
            
            // If there are no events in the calendar, wait indefinitely
            if(nextEvent == null){
                try { // calendar is empty, lets just wait
                    if(!Thread.interrupted())Thread.sleep(Long.MAX_VALUE);
                    else continue;
                } catch (InterruptedException e) {
                    continue;
                }
            } else {
                long durationOfSleep = nextEvent.getTimeToWait(Server.clocks, game.time);
                if (logLoopThread)
                    ConsolePrint.gameInfo("Loop is waiting for ... " + nextEvent.getClass().getSimpleName()
                            + " duration of sleep = " + durationOfSleep);
                
                if (durationOfSleep > 0) {
                    try {
                        if (!Thread.interrupted())
                            Thread.sleep(durationOfSleep);
                        else continue;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                
                // If the next event in the calendar is the same as the one retrieved earlier
                // and the scheduled time for the event has already passed, trigger the event
                if (nextEvent == game.calendar.getNextEvent()
                        && game.calendar.heap.peek().getDate() <= game.time.getTime()) {
                            
                    if (logLoopThread)
                        ConsolePrint.gameInfo("done " + game.calendar.heap.peek().getDate() + " " + game.time.getTime()
                                + " " + (game.calendar.heap.peek().getDate() - game.time.getTime()));
                    
                    game.calendar.check();
                }
            }
        }
    }
}
