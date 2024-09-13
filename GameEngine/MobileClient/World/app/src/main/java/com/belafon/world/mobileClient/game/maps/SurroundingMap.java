package com.belafon.world.mobileClient.game.maps;

import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlacePanel;

public class SurroundingMap {
    public static final int NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS = 7;
    private PlacePanel[][] places = new PlacePanel[NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS][NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS];

    public PlacePanel getPlaceFragment(int x, int y) {
        return places[x][y];
    }

    public void updatePlace(int x, int y, PlacePanel place) {
        places[x][y] = place;
    }

    public void setPlaceUnknown(int x, int y) {
        places[x][y] = null;
    }
}
