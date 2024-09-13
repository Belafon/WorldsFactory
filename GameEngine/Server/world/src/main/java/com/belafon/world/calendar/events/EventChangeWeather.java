package com.belafon.world.calendar.events;

import com.belafon.console.ConsolePrint;
import com.belafon.world.World;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.weather.Sky;
import com.belafon.world.maps.weather.Weather;
import com.belafon.world.visibles.creatures.Creature;

public class EventChangeWeather extends Event {
    private Sky sky;

    public EventChangeWeather(long date, World game, Sky sky, boolean printInLoop) {
        super(date, game);
        this.sky = sky;
        this.printInLoop = printInLoop;
    }

    private final boolean printInLoop;

    /**
     * This method updates the weather and the clouds for a given Sky object.
     * It then updates the creatures' surroundings with the new weather and cloud
     * information.
     * If the printInLoop parameter is set to true, it prints information about the
     * weather
     * and clouds before and after the update.
     */
    @Override
    public void action(World game) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("SetWeather");
                if (printInLoop) {
                    ConsolePrint.gameInfo("before weather change:");
                    ConsolePrint.gameInfo(sky.printWeathers());
                }
                sky.updateWeather();
                if (printInLoop) {
                    ConsolePrint.gameInfo("after weather change:");
                    ConsolePrint.gameInfo(sky.printWeathers());
                }
                // ConsolePrint.success("EventChangeWeather", "at map " + sky.map.id + " weather
                // changed ");
                synchronized (game.creatures) {
                    for (Creature creature : game.creatures) {
                        if (creature.getLocation() instanceof Place place) {
                            Weather weather = place.map.sky.getWeather(place);
                            if (place.map == sky.map) {
                                if (creature.memory.getLastWeather() != weather.getWeather()) {
                                    creature.writer.surrounding.setWeather(weather);
                                    creature.memory.setLastWeather(weather.getWeather());
                                }

                                if (creature.memory.getLastSizeOfClouds() != weather.getClouds()) {
                                    creature.writer.surrounding.setClouds(place);
                                    creature.memory.setLastSizeOfClouds(weather.getClouds());
                                }
                            }
                        }
                    }
                }
            } 
        }).start();
    }

    /**
     * this event should not been interupted.
     */
    @Override
    public void interrupt(World game) {

    }

}