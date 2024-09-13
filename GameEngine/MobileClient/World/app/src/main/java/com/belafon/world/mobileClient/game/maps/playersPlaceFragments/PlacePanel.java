package com.belafon.world.mobileClient.game.maps.playersPlaceFragments;

import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import com.belafon.world.mobileClient.game.maps.Place;

import java.util.List;
import java.util.Set;

/**
 * It is a place with coordinates.
 * So it can be displayed in a grid of places
 */
public class PlacePanel extends Place {
    public final int positionX;
    public final int positionY;

    public PlacePanel(String id, TypePlace typePlace, Set<BehavioursRequirement> requirements,
                      List<PlayersPlaceEffect> placeEffects, int positionX, int positionY) {
        super(id, typePlace, requirements, placeEffects);
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public static PlacePanel getUnknownPlace(int x, int y) {
        return new PlacePanel("UnboundedPlace|" + UNKNOWN.getId(), UNKNOWN.typePlace, UNKNOWN.requirements, UNKNOWN.placeEffects, x, y);
    }

    @Override
    public int compareTo(BehavioursPossibleIngredient other) {
        if(other instanceof PlacePanel otherPlace){
            if(positionX == otherPlace.positionX
                    && positionY == otherPlace.positionY)
                return 0;

            if(positionX < otherPlace.positionX)
                return -1;

            if(positionX == otherPlace.positionX
                    && positionY < otherPlace.positionY)
                return -1;
            return 1;
        } else throw new UnsupportedOperationException();
    }
}
