package com.belafon.world.maps.weather;

import com.belafon.likeliness.Dice;
import com.belafon.world.calendar.events.EventChangeWeather;
import com.belafon.world.calendar.events.EventMoveClouds;
import com.belafon.world.maps.Map;
import com.belafon.world.maps.place.Place;

/**
 * Represents a simulation of a sky above 
 * some concrete map.
 * 
 * It also hold info about the weather 
 * in all places of the map.
 * 
 */
public class Sky {
    public final Weather[][] weather;
    public volatile int directionOfWind = 0;
    
    // duration to next update of weather 
    public volatile int strengthOfWind = 1000;
    public volatile int originX = 0;
    public volatile int originY = 0;
    private final int SPEED_OF_WEATHER_CHANGE = 60;
    public final Map map;
    public final EventMoveClouds eventMoveClouds;
    //public final EventChangeWeather eventChangeWeather;
    public volatile boolean printCloudsInLoop = false;
    public volatile boolean printWeatherInLoop = false;

    /**
     * Represents a simulation of a sky above 
     * some concrete map.
     * @param weather
     * @param directionOfWind
     * @param strengthOfWind
     * @param map
     */
    public Sky(Weather[][] weather, int directionOfWind, int strengthOfWind, Map map) {
        this.weather = weather;
        this.directionOfWind = directionOfWind;
        this.strengthOfWind = strengthOfWind;
        this.map = map;

        // automatic update of weather and clouds
        //eventChangeWeather = new EventChangeWeather(SPEED_OF_WEATHER_CHANGE + 500, map.game, this, printWeatherInLoop);
        eventMoveClouds = new EventMoveClouds(strengthOfWind + 500, map.game, map, printCloudsInLoop);
        //map.game.calendar.add(eventChangeWeather);
        map.game.calendar.add(eventMoveClouds);
    }

    public void setAllClouds(int clouds) {
        for (int i = 0; i < map.sizeX; i++)
            for (int j = 0; j < map.sizeY; j++)
                weather[i][j].setClouds(clouds, map.places[i][j]);
    }

    public void setAllWeather(int weather) {
        for (int i = 0; i < map.sizeX; i++)
            for (int j = 0; j < map.sizeY; j++)
                this.weather[i][j].setWeather(weather, map.places[i][j]);
    }

    /**
     * Returns a weather in concrete place.
     * @param positionX
     * @param positionY
     * @return
     */
    public Weather getWeather(int positionX, int positionY) {
        return weather[(originX + positionX) % map.sizeX][(originY + positionY) % map.sizeY];
    }

    /**
     * Makes an update of clouds.
     * All clouds are moved according the strength of the wind.
     * New EventMoveClouds is planed according the size of wind.
     */
    public void moveClouds() {
        switch (directionOfWind) {
            case 0:
                for (int i = 0; i < map.sizeY; i++)
                    weather[originX][i] = new Weather(0, 0, 0);
                setOriginX(originX + 1);
                break;
            case 1:
                for (int i = 0; i < map.sizeY; i++)
                    weather[originX][i] = new Weather(0, 0, 0);
                setOriginX(originX + 1);
                for (int i = 0; i < map.sizeX; i++)
                    weather[i][originY] = new Weather(0, 0, 0);
                setOriginY(originY + 1);
                break;
            case 2:
                for (int i = 0; i < map.sizeX; i++)
                    weather[i][originY] = new Weather(0, 0, 0);
                setOriginY(originY + 1);
                break;
            case 3:
                for (int i = 0; i < map.sizeX; i++)
                    weather[i][originY] = new Weather(0, 0, 0);
                setOriginY(originY + 1);
                setOriginX(originX - 1);
                for (int i = 0; i < map.sizeY; i++)
                    weather[originX][i] = new Weather(0, 0, 0);
                break;
            case 4:
                setOriginX(originX - 1);
                for (int i = 0; i < map.sizeY; i++)
                    weather[originX][i] = new Weather(0, 0, 0);
                break;
            case 5:
                setOriginX(originX - 1);
                for (int i = 0; i < map.sizeY; i++)
                    weather[originX][i] = new Weather(0, 0, 0);
                setOriginY(originY - 1);
                for (int i = 0; i < map.sizeX; i++)
                    weather[i][originY] = new Weather(0, 0, 0);
                break;
            case 6:
                setOriginY(originY - 1);
                for (int i = 0; i < map.sizeX; i++)
                    weather[i][originY] = new Weather(0, 0, 0);
                break;
            case 7:
                setOriginY(originY - 1);
                for (int i = 0; i < map.sizeX; i++)
                    weather[i][originY] = new Weather(0, 0, 0);
                for (int i = 0; i < map.sizeY; i++)
                    weather[originX][i] = new Weather(0, 0, 0);
                setOriginX(originX + 1);
                break;
        }
        map.game.calendar
                .add(new EventMoveClouds(map.game.time.getTime() + strengthOfWind, map.game, map, printCloudsInLoop));
    }

    private void setOriginX(int origin) {
        if (origin < 0)
            origin = map.sizeX + origin;
        if (origin >= map.sizeX)
            origin %= map.sizeX;
        originX = origin;
    }

    private void setOriginY(int origin) {
        if (origin < 0)
            origin = map.sizeY + origin;
        if (origin >= map.sizeY)
            origin %= map.sizeY;
        originY = origin;
    }

    /**
     * Updates weather in each place,
     * also updates the size of clouds.
     * 
     * It also plan next event, the duration to new update is regular. 
     * Three options, it will satrt rain, the cloud will grow, or nothing will.
     */
    public void updateWeather() {
        for (int i = 0; i < weather.length; i++) {
            for (int j = 0; j < weather.length; j++) {
                Weather w = weather[i][j];
                if (w.getWeather() == 0) { // 0 - 4
                    // three options, it will satrt rain, the cloud will grow, or nothing will
                    // happen
                    if (w.getClouds() > 1) {
                        Dice dice10 = new Dice(10 + w.getClouds());
                        int toss = dice10.toss() - 1;
                        if (toss > 11)
                            w.setWeather(w.getClouds() - 1, map.places[i][j]); // starts raining
                    }

                    if (w.getClouds() < 7) {
                        Dice dice20 = new Dice(20);
                        if (dice20.toss() + map.places[i][j].typeOfPlace.evaporationRate > 19)
                            w.setClouds(w.getClouds() + 1, map.places[i][j]); // cloud grows
                    }
                } else {
                    // will stop rain, will rain harder or nothing will happen
                    // for stop depends on size of cloud and duration of rain and strength of rain
                    Dice dice10 = new Dice(8 + (w.durationOfRain * 2));
                    if (dice10.toss() > 7) {
                        // rain is over
                        w.durationOfRain = 0;
                        w.setClouds(0, map.places[i][j]);
                        w.setWeather(0, map.places[i][j]);
                    } else
                        w.durationOfRain++;
                }
            }
        }
        map.game.calendar.add(new EventChangeWeather(map.game.time.getTime() + SPEED_OF_WEATHER_CHANGE, map.game, this, printWeatherInLoop));
    }

    public synchronized String printClouds() {
        String draw = "\n";
        for (int i = 0; i < map.sizeX; i++) {
            for (int j = 0; j < map.sizeY; j++)
                draw += getWeather(i, j).getClouds() + " ";
            draw += "\n";
        }
        return draw;
    }

    public synchronized String printWeathers() {
        String draw = "\n";
        for (int i = 0; i < map.sizeX; i++) {
            for (int j = 0; j < map.sizeY; j++)
                draw += getWeather(i, j).getWeather() + " ";
            draw += "\n";
        }
        return draw;
    }

    /** 
     * returns weatheer in concrete place.
     */
    public Weather getWeather(Place place) {
        return getWeather(place.positionX, place.positionY);
    }
}
