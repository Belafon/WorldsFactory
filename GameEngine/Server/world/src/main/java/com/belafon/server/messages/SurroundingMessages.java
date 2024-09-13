package com.belafon.server.messages;

import com.belafon.world.maps.Map;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.maps.weather.Weather;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.time.DailyLoop.NamePartOfDay;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.PlayersLookAround;
import com.belafon.world.visibles.resources.Resource;

public interface SurroundingMessages {
    public default void setPartOfDay(NamePartOfDay partOfDay) {
    }

    public default void setWeather(Weather weather) {
    }

    public default void setClouds(Place place) {
    }

    public default void setInfoAboutSurrounding(PlayersLookAround surrounding) {
    }

    public default void setResources(UnboundedPlace position) {
    }

    public default void setResource(Resource resource) {
    }

    public default void setResourceNotFound(Resource resource) {
    }

    public default void setItems(UnboundedPlace position) {
    }

    public default void setTypeOfPlaceInfoDrawableSound(UnboundedPlace position) {
    }

    public default void setNewMap(Map map, int sizeX, int sizeY) {
    }

    public default void addVisibleInSight(Visible value, Creature watcher) {
    }

    public default void removeVisibleFromSight(Visible value) {
    }

    public default void removePlaceFromSight(Place lastPlace, int xInRelativeMap, int yInRelativeMap) {
    }

    /**
     * This informs player about his current position.
     * Should be called only when the position changes.
     * @param place
     */
    public default void setCurrentPositionInfo(UnboundedPlace place) {
    }
}
