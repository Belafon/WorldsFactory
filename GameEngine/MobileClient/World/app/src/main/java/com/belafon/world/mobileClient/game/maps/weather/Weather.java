package com.belafon.world.mobileClient.game.maps.weather;

import java.util.HashMap;

import android.util.Log;
import android.view.View;
import com.belafon.world.mobileClient.game.maps.weather.ColorViewTransition.Color;
import com.belafon.world.mobileClient.logs.Logs;
import java.util.ArrayList;
import com.belafon.world.mobileClient.game.maps.weather.Cloud;

public class Weather {
    private static final String TAG = "Weather";
    private int cloudsIndex = 0;
    private int weather = 0;
    private NamePartOfDay partOfDay = NamePartOfDay.unknown;
    private WeatherFragment fragment;

    public void setWeatherView(View view) {
        this.fragment = new WeatherFragment(view);
    }

    public enum NamePartOfDay {
        after_midnight, // starts at 3.0 hours today -> 60 of ticks today
        sunrise, // 5.5 -> 110
        morning, // 7.0 -> 140
        afternoon, // 15.0 -> 300
        sunset_1, // 18.5 -> 370
        sunset_2, // 19.25 -> 385
        night, // 20.0 -> 400
        unknown
    }

    private final HashMap<NamePartOfDay, PartOfDay> partsOfDay = new HashMap<>() {
        {
            put(NamePartOfDay.unknown, new PartOfDay(NamePartOfDay.unknown, new Color(0, 0, 50, 120), 1200));
            put(NamePartOfDay.after_midnight, new PartOfDay(NamePartOfDay.after_midnight, new Color(0, 0, 30, 180), 1000));
            put(NamePartOfDay.sunrise, new PartOfDay(NamePartOfDay.sunrise, new Color(255, 10, 0, 30), 600));
            put(NamePartOfDay.morning, new PartOfDay(NamePartOfDay.morning, new Color(255, 255, 0, 10), 750));
            put(NamePartOfDay.afternoon, new PartOfDay(NamePartOfDay.afternoon, new Color(255, 200, 0, 10), 1020));
            put(NamePartOfDay.sunset_1, new PartOfDay(NamePartOfDay.sunset_1, new Color(255, 10, 0, 40), 600));
            put(NamePartOfDay.sunset_2, new PartOfDay(NamePartOfDay.sunset_2, new Color(15, 5, 80, 150), 540));
            put(NamePartOfDay.night, new PartOfDay(NamePartOfDay.night, new Color(0, 0, 10, 155), 1200));
        }
    };

    public final Cloud[] cloudTypes = new Cloud[] {
            new Cloud(new Color(0, 0, 0, 0), 0, 0, 0, false, true, "clear sky"),
            new Cloud(new Color(-1200, -1200, -600, 60), 380, 300, 30, true, true, "clear sky with few small clouds"),
            new Cloud(new Color(-1200, -1200, -600, 80), 240, 300, 60, true, true, "sky with small clouds"),
            new Cloud(new Color(-1200, -1200, -600, 80), 160, 300, 100, true, true, "slightly translucent"),

            new Cloud(new Color(-1200, -1200, -600, 80), 120, 300, 200, true, true, "partly cloudy"),
            new Cloud(new Color(-1200, -1200, -600, 90), 120, 300, 200, true, true, "cloudy"),
            new Cloud(new Color(-1200, -1200, -600, 90), 0, 300, 1000, true, false, "very cloudy"),
            new Cloud(new Color(-1200, -1200, -600, 90), 0, 300, 1000, true, false, "dark cloudy")
    };

    private String[] cloudsNames = new String[] {
            "clear sky",
            "clear sky with few small clouds",
            "sky with small clouds",
            "slightly translucent",
            "partly cloudy",
            "cloudy",
            "very cloudy",
            "dark cloudy"
    };

    private String[] weatherNames = new String[] {
            "idle",
            "gentle rain",
            "rain",
            "heavy rain",
            "storm"
    };

    public enum WeatherType {
        idle(new Color(0, 0, 0, 0), 0),
        rain(new Color(-1200, -1200, -1200, 110), 0),
        heavy_rain(new Color(-1200, -1200, -1200, 120), 0),
        storm(new Color(-1200, -1200, -1200, 120), 60),
        thunderstorm(new Color(-1200, -1200, -1200, 120), 30);

        public final Color color;
        public final int frequencyOfLightnings;

        WeatherType(Color color, int frequencyOfLightnings) {
            this.color = color;
            this.frequencyOfLightnings = frequencyOfLightnings;
        }

    }

    /**
     * This changes current clouds in the current
     * context.
     * 
     * @param args is the message from the server.
     */
    public void setClouds(String[] args) {
        cloudsIndex = Integer.parseInt(args[2]);
        Cloud cloud = cloudTypes[cloudsIndex];
        if (fragment != null)
            fragment.setClouds(cloud);
    }

    /**
     * This changes weather in the current context.
     * All fragments are updated according the new
     * weather type.
     * 
     * @param args is the message from the server.
     */
    public void setWeather(String[] args) {
        weather = Integer.parseInt(args[2]);
        String weatherTextDescription = weatherNames[weather];
        WeatherType type = WeatherType.values()[weather];
        if (fragment != null)
            fragment.setWeather(weatherTextDescription, type);
    }

    /**
     * This changes weather in the current context.
     * All fragments are updated according the new
     * weather type.
     * 
     * @param args is the message from the server.
     */
    public void setPartOfDay(String[] args) {
        partOfDay = NamePartOfDay.valueOf(args[2]);

        if (Logs.WEATHER_FILTER)
            Log.d(TAG, "setPartOfDay: " + partOfDay.name());

        if (fragment != null)
            fragment.setPartOfDay(partsOfDay.get(partOfDay));
    }

    public WeatherFragment getFragment() {
        return fragment;
    }
}
