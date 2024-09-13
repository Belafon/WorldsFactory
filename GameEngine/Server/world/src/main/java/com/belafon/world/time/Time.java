package com.belafon.world.time;

import com.belafon.console.ConsolePrint;
import com.belafon.world.World;
import com.belafon.world.time.DailyLoop.PartOfDay;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

/**
 * It has all methods to transfer number of ticks to our well known time,
 * It also has also has method getTime which returns number of ticks of the
 * Game,
 * when the Game is started, the current time from Clocks is saved,
 * getTime returns value current time from clocks - the time saved when the Game
 * started
 */
@WorldsFactoryClass(className = "Time")
public class Time {
    private static final long TICKS_PER_HOUR = 60;
    private static final long TICKS_PER_DAY = 24 * TICKS_PER_HOUR; // 1440
    private static final long TICKS_PER_MONTH = 30 * TICKS_PER_DAY;

    private long inception;
    private Clocks clocks;
    private PartOfDay partOfDay;
    private World game;

    public Time(Clocks clocks, World game) {
        inception = clocks.getTime();
        this.clocks = clocks;
        this.game = game;
        partOfDay = game.dailyLoop.partsOfDay[0];
        storyObjectName = "time";
        WorldsFactoryStoryManager.bindObject(storyObjectName, this);
    }

    @WorldsFactoryObjectsName
    public String storyObjectName;

    public Clocks getClocks() {
        return clocks;
    }

    public long getTime() {
        return clocks.getTime() - inception;
    }

    public long ticksOfToday() {
        return getTime() % TICKS_PER_DAY;
    }

    public long tickOfThisMonth() {
        return getTime() % TICKS_PER_MONTH;
    }

    public long ticksOfThisHour() {
        return getTime() % TICKS_PER_HOUR;
    }

    public String logDate() {
        return "Now is " + (getMonth(getTime()) + 1) + "th Month, " + (getDay(tickOfThisMonth()) + 1)
                + "th Day of this Month, " + (getHours(ticksOfToday()) + 1) + "th hour of this day and "
                + (getMinutes(ticksOfThisHour()) + 1) + "th minute of this hour.";
    }

    public long[] getDate() {
        return new long[] {
                getMonth(getTime()) + 1,
                getDay(tickOfThisMonth()) + 1,
                getHours(ticksOfToday()) + 1,
                getMinutes(ticksOfThisHour()) + 1
        };
    }

    public static long getMinutes(long numberOfTicks) {
        return numberOfTicks;
    }

    public static float getHoursFloat(long numberOfTicks) {
        return ((float) numberOfTicks) / ((float) TICKS_PER_HOUR);
    }

    public static long getHours(long numberOfTicks) {
        return numberOfTicks / TICKS_PER_HOUR;
    }

    public static float getDayFloat(long numberOfTicks) {
        return ((float) numberOfTicks) / TICKS_PER_DAY;
    }

    public static long getDay(long numberOfTicks) {
        return numberOfTicks / TICKS_PER_DAY;
    }

    public static float getMonthFloat(long numberOfTicks) {
        return ((float) numberOfTicks) / TICKS_PER_MONTH;
    }

    public static long getMonth(long numberOfTicks) {
        return numberOfTicks / TICKS_PER_MONTH;
    }

    public static long hoursToTicks(long hours) {
        return hours * TICKS_PER_HOUR;
    }

    public static long daysToTicks(long days) {
        return days * TICKS_PER_DAY;
    }

    public static long monthsToTicks(long months) {
        return months * TICKS_PER_MONTH;
    }

    public static long percentsOfDayToTicks(float percents) {
        return (long) (percents * TICKS_PER_DAY);
    }

    public void setCurrentPartOfDay(PartOfDay currentPartOfDay) {
        setCurrentPartOfDayFromStory(currentPartOfDay.name().name());
    }

    @WorldsFactoryPropertySetter(name = "currentPartOfDay")
    public void setCurrentPartOfDayFromStory(String currentPartOfDay) {
        var partOfDay = DailyLoop.NamePartOfDay.valueOf(currentPartOfDay);
        if (game.dailyLoop.partsOfDay[partOfDay.ordinal()] == null)
            ConsolePrint.error_big("Time: cannot find part of the day " + currentPartOfDay);
        setCurrentPartOfDayValue(game.dailyLoop.partsOfDay[partOfDay.ordinal()]);
        WorldsFactoryStoryManager.setProperty("currentPartOfDay", currentPartOfDay, storyObjectName);
    }

    private void setCurrentPartOfDayValue(PartOfDay currentPartOfDay) {
        this.partOfDay = currentPartOfDay;
        synchronized (game.creatures) {
            for (Creature creature : game.creatures)
                creature.writer.surrounding.setPartOfDay(partOfDay.name());
        }
    }

    public PartOfDay getPartOfDay() {
        return partOfDay;
    }
}
