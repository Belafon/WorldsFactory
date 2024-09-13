package com.belafon.world.maps.mapGenerators;

import com.belafon.likeliness.Dice;
import com.belafon.world.maps.Map;
import com.belafon.world.maps.place.ListOfAllTypesOfPlaces;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.weather.Weather;

public class BasicGenerator implements MapGenerator {

    /**
     * FIrstly generates small altitude map, 
     * then according these altitudes all Places are 
     * generated.
     */
    @Override
    public Place[][] generateMap(int sizeX, int sizeY, Map map) {
        int primitivMap[][] = new int[sizeX / 2][sizeY / 2];
        Dice dice5 = new Dice(5);
        Dice dice3 = new Dice(3);
        primitivMap[0][0] = dice5.toss() - 1;
        for (int i = 1; i < sizeX / 2; i++) {
            primitivMap[i][0] = Math.abs(primitivMap[i - 1][0] + dice3.toss() - 2);
            if(primitivMap[i][0] > 4)primitivMap[i][0] = 4;
        }
        for (int i = 1; i < sizeY / 2; i++) {
            primitivMap[0][i] = Math.abs(primitivMap[0][i - 1] + dice3.toss() - 2);
            if(primitivMap[0][i] > 4)primitivMap[0][i] = 4;
        } 
        for (int i = 1; i < sizeX / 2; i++)
            for (int j = 1; j < sizeY / 2; j++) {
                float averagef = (primitivMap[i - 1][j - 1] + primitivMap[i - 1][j] + primitivMap[i][j - 1]) / 3f;
                int average = Math.round(averagef);
                primitivMap[i][j] = Math.abs(average + dice3.toss() - 2);
                if(primitivMap[i][j] > 4) primitivMap[i][j] = 4;
            }
        
        /*for (int i = 0; i < sizeX / 2; i++) {
            for (int j = 0; j < sizeY / 2; j++) {
                System.out.print(primitivMap[i][j] + " ");
            }
            System.out.println();
        }*/

        Place[][] mapPlace = new Place[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++)
                mapPlace[i][j] = new Place(map, i, j, getAltitude(primitivMap[i / 2][j / 2]), ListOfAllTypesOfPlaces.getRandomTypeOfPlaceAtAltitude(primitivMap[i / 2][j / 2]));
        return mapPlace;
    }

    private int getAltitude(int altitude){
        Dice dice = new Dice(201);
        return altitude * 200 + dice.toss() - 1;
    }

    @Override
    public Weather[][] generateClouds(int sizeX, int sizeY) {
        Weather[][] stats = new Weather[sizeX][sizeY];
        Dice dice = new Dice(5);
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++)
                stats[i][j] = new Weather(0, dice.toss() - 1, 0);
        return stats;
    }
    
}
