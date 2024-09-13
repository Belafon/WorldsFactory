package com.belafon.world.maps.weather;

import com.belafon.world.maps.place.Place;
import com.belafon.world.visibles.creatures.Creature;

public class Weather {
    /**
     * Number of types of weather is 
     * the ordinal number.
     */
    public enum TypeWeather {
        idle,
        gentle_rain,
        rain,
        heavy_rain,
        storm
    }

    /**
     * Number of types of clouds is 
     * the ordinal number * 2.
     */
    public enum TypeClouds {
        clear,
        few,
        scattered,
        overcast
    }

    public volatile int durationOfRain = 0;
    /**
     * Type of clouds is represented by a number from 0 to 7,
     * where 0 is clear and higher number means more clouds.
     */
    private volatile int clouds = 0;

    /**
     * Type of weather is represented by a number from 0 to 4,
     * where 0 is idle, 1 is gentle rain, 2 is rain, 3 is heavy rain, 4 is storm.
     */
    private int weather = 0;
    public volatile int visibility = 0; // can be changed by fog for example

    /**
     * Represents weather in concrete position.
     * this object is moving with the wind in the sky.
     * 
     * Type of weather is represented by a number from 0 to 4,
     * where 0 is idle, 1 is gentle rain, 2 is rain, 3 is heavy rain, 4 is storm.
     * 
     * Type of clouds is represented by a number from 0 to 7,
     * where 0 is clear and higher number means more clouds.
     * 
     * @param durationOfRain
     * @param clouds
     * @param weather
     */
    public Weather(int durationOfRain, int clouds, int weather) {
        this.durationOfRain = durationOfRain;
        this.clouds = clouds;
        this.weather = weather;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(TypeWeather weather, Place place) {
        int weatherNumber = weather.ordinal();
        setWeather(weatherNumber, place);
    }

    public void setWeather(int weather, Place place) {
        this.weather = weather;
        synchronized (place.creatures) {
            for (Creature creature : place.creatures)
                creature.writer.surrounding.setWeather(this);
        }

        place.setStoryWeather(TypeWeather.values()[weather]);
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds, Place place) {
        this.clouds = clouds;
        synchronized (place.creatures) {
            for (Creature creature : place.creatures)
                creature.writer.surrounding.setClouds(place);
        }

        place.setStoryClouds(TypeClouds.values()[clouds / 2]);

    }

    public int getVisibility() {
        return visibility;
    }
}
