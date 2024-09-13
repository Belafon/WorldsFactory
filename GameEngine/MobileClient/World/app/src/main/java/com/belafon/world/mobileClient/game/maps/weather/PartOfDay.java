package com.belafon.world.mobileClient.game.maps.weather;

import com.belafon.world.mobileClient.game.maps.weather.ColorViewTransition.Color;


public class PartOfDay {
    public final Weather.NamePartOfDay name;
    public final Color colorLevel;
    public final int durationInNuberOfChecks;
    public PartOfDay(Weather.NamePartOfDay name, Color colorLevel, int durationInNuberOfChecks) {
        this.name = name;
        this.colorLevel = colorLevel;
        this.durationInNuberOfChecks = durationInNuberOfChecks;
    }
}
