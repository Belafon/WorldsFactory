package com.belafon.world.mobileClient.game.maps.weather;

import com.belafon.world.mobileClient.game.maps.weather.ColorViewTransition.Color;

/**
 * Represents a level of cloudiness.
 */
public class Cloud {
    public final Color finalColor;
    public final int frequencyOfIdleClouds;
    public final int durationCloudTransition;
    // says how long the filter will be colored with full color of 
    // the finalColor then the backwards transition will start
    public final int durationOfCloud;
    // only the `clear sky` is not cloudy
    public final boolean isCloudy;
    // if false, the filter is colored with finalColor until 
    // the cloudness is changed
    public final boolean doesSunGoThrough;
    public final String description;

    public Cloud(Color finalColor, int frequencyOfIdleClouds,
            int durationCloudTransition, int durationOfCloud, boolean isCloudy,
            boolean doesSunGoThrough, String description) {
        this.finalColor = finalColor;
        this.frequencyOfIdleClouds = frequencyOfIdleClouds;
        this.durationCloudTransition = durationCloudTransition;
        this.durationOfCloud = durationOfCloud;
        this.isCloudy = isCloudy;
        this.description = description;
        this.doesSunGoThrough = doesSunGoThrough;
    }
}
