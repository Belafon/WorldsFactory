package com.belafon.world.time;

import com.belafon.world.World;
import com.belafon.world.calendar.events.EventPartOfDay;

/**
 * This class represents the daily loop in the game world. It is responsible for keeping track of the different
 * parts of the day and adding them to the game calendar as events. It uses a World object to access the game time and calendar.
 */
 public class DailyLoop {
    private World game;
    public volatile boolean isStarted = false;

    /**
     * Constructor for DailyLoop class. Initializes the World object and adds the first day's events to the calendar.
     *
     * @param game The World object for the game.
     */
    public DailyLoop(World game) {
        this.game = game;
    }

    public void start() throws NullPointerException{ 
        if(game.time == null)
            throw new NullPointerException("game.time is null");
        isStarted = true;
        addToCalendar(game.time.getTime() - 180);
    }

    /**
     * A list containing the different parts of the day with their start time, name, and temperature change.
     */
    public PartOfDay[] partsOfDay = {
        new PartOfDay(Time.percentsOfDayToTicks(3f / 24), NamePartOfDay.after_midnight, -5),
        new PartOfDay(Time.percentsOfDayToTicks(5.5f / 24f), NamePartOfDay.sunrise, -4),
        new PartOfDay(Time.percentsOfDayToTicks(7f / 24f), NamePartOfDay.morning, -2),
        new PartOfDay(Time.percentsOfDayToTicks(15f / 24f), NamePartOfDay.afternoon, 0),
        new PartOfDay(Time.percentsOfDayToTicks(18.5f / 24f), NamePartOfDay.sunset_1, -2),
        new PartOfDay(Time.percentsOfDayToTicks(19.25f / 24f), NamePartOfDay.sunset_2, -3),
        new PartOfDay(Time.percentsOfDayToTicks(20f / 24f), NamePartOfDay.night, -4)
    };

    public static enum NamePartOfDay {
        after_midnight, // starts at 3.0 hours today -> 60 of ticks today
        sunrise, // 5.5 -> 110
        morning, // 7.0 -> 140
        afternoon, // 15.0 -> 300
        sunset_1, // 18.5 -> 370
        sunset_2, // 19.25 -> 385
        night // 20.0 -> 400
    }

    /**
     * Adds the events for the next day's parts of the day to the game calendar.
     */
    public void addPlanToNextDay() {
        long startOfnextDayInTicks = Time.getDay(game.time.getTime()) + 1; // number of next day
        startOfnextDayInTicks = Time.daysToTicks(startOfnextDayInTicks); // transfer days to ticks
        addToCalendar(startOfnextDayInTicks);
    }

    /**
     * Adds the events for the parts of the day to the game calendar.
     *
     * @param startOfnextDayInTicks The start time of the next day in ticks.
     */
    private void addToCalendar(long startOfnextDayInTicks) {
        for (PartOfDay partOfDay : partsOfDay)
            game.calendar.add(new EventPartOfDay(partOfDay.start + startOfnextDayInTicks, partOfDay, game));
        //game.calendar.add(new EventPartOfDay(0, partsOfDay[0], game));
        //game.calendar.add(new EventPartOfDay(800, partsOfDay[1], game));
        //game.calendar.add(new EventPartOfDay(801, partsOfDay[2], game));
        //game.calendar.add(new EventPartOfDay(802, partsOfDay[3], game));
        //game.calendar.add(new EventPartOfDay(803, partsOfDay[4], game));
    }

    public static record PartOfDay (long start, NamePartOfDay name, int temperatureChange) {
    }

	public void setCurrentPartOfDay(PartOfDay currentPartOfDay) {
        game.calendar.add(new EventPartOfDay(currentPartOfDay.start, currentPartOfDay, game));
        if(currentPartOfDay.name ==  NamePartOfDay.sunset_1)
            game.calendar.add(new EventPartOfDay(currentPartOfDay.start + partsOfDay[5].start, partsOfDay[5], game));
	}

}
