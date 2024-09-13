package com.belafon.world.maps;

import com.belafon.world.World;
import com.belafon.world.maps.mapGenerators.MapGenerator;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.weather.Sky;

public class Map {
    public final int id;
    private static int nextId = 0;
    public final int sizeX;
    public final int sizeY;
    public final Place[][] places;
    public final Sky sky;
    
    public World game;
    
    // distance between places in meters
    private final float distanceBetweenPlaces;

    public Map(int sizeX, int sizeY, MapGenerator mapGenerator, int directionOfWind, int strengthOfWind, float distanceBetweenPlaces, World game) {
        this.id = nextId++;
        this.distanceBetweenPlaces = distanceBetweenPlaces;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.game = game;
        sky = new Sky(mapGenerator.generateClouds(sizeX, sizeY), directionOfWind, strengthOfWind, this);
        places = mapGenerator.generateMap(sizeX, sizeY, this);
    }

    public Map(int sizeX, int sizeY, MapGenerator mapGenerator, float distanceBetweenPlaces) {
        this.id = nextId++;
        this.distanceBetweenPlaces = distanceBetweenPlaces;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        places = mapGenerator.generateMap(sizeX, sizeY, this);
        sky = null;
    }

    public String logMap(){
        String log = "";
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                log += places[i][j].log();
            }
            log += "\n";
        }
        return log;
    }

    public String logTemperature() {
        String log = "";
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++)
                log += places[i][j].getTemperature() + " ";
            log += "\n";
        }
        return log;
    }
    
    public float getDistanceBetweenPlaces(){
        return distanceBetweenPlaces;
    }
}
