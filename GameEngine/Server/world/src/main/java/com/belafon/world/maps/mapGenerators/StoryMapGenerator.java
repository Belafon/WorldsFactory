package com.belafon.world.maps.mapGenerators;

import com.belafon.world.maps.Map;
import com.belafon.world.maps.place.ListOfAllTypesOfPlaces;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.weather.Weather;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;

public class StoryMapGenerator implements MapGenerator {
    public Place[][] generateMap(int sizeX, int sizeY, Map map) {
        Place[][] mapPlace = new Place[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++){
                var placePath = new StringBuilder("places[" + i + "][" + j + "]");

                var descriptionPath = placePath + ".description";
                String description = (String)WorldsFactoryStoryManager.getProperty(descriptionPath.toString(), "map");
                var typePlaceName = ListOfAllTypesOfPlaces.NamesOfTypesOfPlaces.valueOf(description);
                var typePlace = ListOfAllTypesOfPlaces.typeOfPlaces.get(typePlaceName);


                // get name of the object in the story by its position 
                // in the story map
                var storyPlaceObjectNamePath = placePath + ".__object_name__";
                String storyPlaceObjectName = (String)WorldsFactoryStoryManager.getProperty(storyPlaceObjectNamePath.toString(), "map");

                mapPlace[i][j] = new Place(map, i, j, 0, typePlace, storyPlaceObjectName);
            }
        return mapPlace;
    }

    public Weather[][] generateClouds(int sizeX, int sizeY) {
        Weather[][] stats = new Weather[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++)
                stats[i][j] = new Weather(0, 0, 0);
        return stats;
    }

}