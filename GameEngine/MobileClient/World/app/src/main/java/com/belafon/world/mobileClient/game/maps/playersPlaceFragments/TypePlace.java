package com.belafon.world.mobileClient.game.maps.playersPlaceFragments;

import android.graphics.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TypePlace {
    public final String name;
    public final String description;
    /**
     * Background color typical for this type
     * of place, it is used for example as a color
     * of point in the map.
     */
    public final int backgroundColor;

    public TypePlace(String name, String description, int backgroundColor) {
        this.name = name;
        this.description = description;
        this.backgroundColor = backgroundColor;
    }

    public static final Map<String, TypePlace> allTypes;
    static {
        Map<String, TypePlace> types = new HashMap<>();
        types.put("mountain_meadow",
                new TypePlace("mountain_meadow", "description meadow",
                        Color.rgb(60, 180, 5)));

        types.put("meadow",
                new TypePlace("meadow", "description meadow",
                        Color.rgb(60, 120, 2)));

        types.put("forest_leafy",
                new TypePlace("forest_leafy", "description forest_leafy",
                        Color.rgb(10, 100, 10)));

        types.put("forest_spurce",
                new TypePlace("forest_spurce", "description forest_spurce",
                        Color.rgb(10, 100, 10)));

        types.put("cliff",
                new TypePlace("cliff", "description cliff",
                        Color.rgb(10, 100, 10)));

        types.put("lake",
                new TypePlace("lake", "description lake",
                        Color.rgb(30, 80, 255)));

        types.put("unknown",
                new TypePlace("unknown", "unknown place",
                        Color.rgb(100, 100, 100)));

        allTypes = Collections.unmodifiableMap(types);
    }
}
