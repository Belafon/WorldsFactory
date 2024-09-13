package com.belafon.world.calendar.events;

import com.belafon.world.World;
import com.belafon.world.time.DailyLoop.NamePartOfDay;
import com.belafon.world.time.DailyLoop.PartOfDay;

public class EventPartOfDay extends Event {
    private PartOfDay partOfDay;

    /**
     * @param date      the date of the event
     * @param partOfDay the new part of the day to set in the game's time object
     * @param game
     */
    public EventPartOfDay(long date, PartOfDay partOfDay, World game) {
        super(date, game);
        this.partOfDay = partOfDay;
    }

    /**
     * This method changes the part of the day in the game's time object and
     * notifies the creature about the new part of the day.
     * If the new part of the day is night, it adds a plan for the next day in the
     * game's daily loop.
     * 
     * @param game
     */
    @Override
    public void action(World game) {
        // ConsolePrint.success("EventPartOfDay", "part of day changed " +
        // partOfDay.name());

        game.time.setCurrentPartOfDay(partOfDay);
        if (game.dailyLoop.isStarted && partOfDay.name() == NamePartOfDay.night)
            game.dailyLoop.addPlanToNextDay();

    }

    @Override
    public void interrupt(World game) {

    }

}
