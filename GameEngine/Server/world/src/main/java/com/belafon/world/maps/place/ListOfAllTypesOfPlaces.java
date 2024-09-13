package com.belafon.world.maps.place;

import java.util.ArrayList;
import java.util.Hashtable;

import com.belafon.likeliness.Dice;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources;
import com.belafon.world.visibles.resources.TypeOfResourceOfTypeOfPlace;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources.NamesOfTypesOfResources;

/**
 * Setup all types of places
 */
public class ListOfAllTypesOfPlaces {
    public static Hashtable<NamesOfTypesOfPlaces, TypeOfPlace> typeOfPlaces = new Hashtable<NamesOfTypesOfPlaces, TypeOfPlace>();
    public static NamesOfTypesOfPlacesList[] typeOfPlacesInTheSpecificAltitude = new NamesOfTypesOfPlacesList[5];

    public static class NamesOfTypesOfPlacesList {
        public final ArrayList<NamesOfTypesOfPlaces> list = new ArrayList<NamesOfTypesOfPlaces>();
    }

    /**
     * Setup all types of places
     */
    public static void setUpAllTypesOfPlaces() {
        typeOfPlaces.put(NamesOfTypesOfPlaces.forest_leafy, new TypeOfPlace(NamesOfTypesOfPlaces.forest_leafy,
                new String[] { "forest_leafy_1" },
                new String[] { "forest_leafy_1", "forest_leafy_2", "forest_leafy_3" }, new int[] { 0, 1, 2, 3 },
                new TypeOfResourceOfTypeOfPlace[] {
                        new TypeOfResourceOfTypeOfPlace(50, 1,
                                ListOfAllTypesOfResources.typesOfResources.get(NamesOfTypesOfResources.blueberry)),
                        new TypeOfResourceOfTypeOfPlace(50, 1,
                                ListOfAllTypesOfResources.typesOfResources.get(NamesOfTypesOfResources.mushrooms)),
                        new TypeOfResourceOfTypeOfPlace(100, 1,
                                ListOfAllTypesOfResources.typesOfResources.get(NamesOfTypesOfResources.treeOak))
                },
                2, 0x6dff2e, "Fresh deciduous forest covered with fallen leaves and forest plants."));

        typeOfPlaces.put(NamesOfTypesOfPlaces.forest_spurce, new TypeOfPlace(NamesOfTypesOfPlaces.forest_spurce,
                new String[] { "forest_spurce_1" },
                new String[] { "forest_spurce_1" }, new int[] { 3, 4 },
                new TypeOfResourceOfTypeOfPlace[0],
                2, 0x6dff2e, "Dark coniferous forest with a smell of resin."));

        typeOfPlaces.put(NamesOfTypesOfPlaces.meadow, new TypeOfPlace(NamesOfTypesOfPlaces.meadow,
                new String[] { "meadow_1"},
                new String[] { "meadow_1" }, new int[] { 0, 1, 2 },
                new TypeOfResourceOfTypeOfPlace[0],
                3, 0xb8fc30, "Meadow abounding with tall grass and full of buzzing insects."));

        typeOfPlaces.put(NamesOfTypesOfPlaces.mountain_meadow, new TypeOfPlace(NamesOfTypesOfPlaces.mountain_meadow,
                new String[] { "mountain_meadow_1"},
                new String[] { "meadow_2" }, new int[] { 3, 4 },
                new TypeOfResourceOfTypeOfPlace[0],
                2, 0xc6f538, "Mountain slope overgrown with tall grass and mountain aromatic herbs."));

        typeOfPlaces.put(NamesOfTypesOfPlaces.lake, new TypeOfPlace(NamesOfTypesOfPlaces.lake,
                new String[] { "lake_1" },
                new String[] { "lake_1" }, new int[] { 0, 1, 2 },
                new TypeOfResourceOfTypeOfPlace[0],
                3, 0xc6f538, "Lake with clear water."));

        typeOfPlaces.put(NamesOfTypesOfPlaces.cliff, new TypeOfPlace(NamesOfTypesOfPlaces.cliff,
                new String[] { "cliff_1" },
                new String[] { "cliff_1" }, new int[] { 3, 4 },
                new TypeOfResourceOfTypeOfPlace[0],
                2, 0xc6f538, "Steep rock wall."));

        // a space where items can be stored is represented by a UnboundedPlace, it can has its own type
        typeOfPlaces.put(NamesOfTypesOfPlaces.back_space, new TypeOfPlace(NamesOfTypesOfPlaces.back_space,
                new String[] {},
                new String[] {},
                new int[] {},
                new TypeOfResourceOfTypeOfPlace[0],
                2, 0xc6f538, "Smelly musty leather bag."));

        typeOfPlaces.put(NamesOfTypesOfPlaces.leather_bag, new TypeOfPlace(NamesOfTypesOfPlaces.leather_bag,
                new String[] {},
                new String[] {},
                new int[] {},
                new TypeOfResourceOfTypeOfPlace[0],
                2, 0xc6f538, "Smelly musty leather bag."));

        for (int i = 0; i < typeOfPlacesInTheSpecificAltitude.length; i++)
            typeOfPlacesInTheSpecificAltitude[i] = new NamesOfTypesOfPlacesList();
        // this will assort the types of places by altitude
        for (TypeOfPlace typeOfPlace : typeOfPlaces.values())
            for (int altitude : typeOfPlace.altitudesOfPressence)
                typeOfPlacesInTheSpecificAltitude[altitude].list.add(typeOfPlace.name);
    }

    public enum NamesOfTypesOfPlaces {
        forest_leafy,
        forest_spurce,
        forest_pine,
        river,
        stream,
        lake,
        mountain_lake,
        mountain_river,
        mountain_stream,
        mountain_meadow,
        wetland,
        swamp_land,
        moorland,
        meadow,
        cave,
        cliff,
        rock_land,
        plateau_of_bushes,

        // names of types of space items
        back_space,
        leather_bag,
        EMPTY
    }

    /**
     * Returns random altitude of type of place.
     * Each type of place has its own range of altitudes,
     * where it could be located.
     * 
     * @param altitude
     * @return
     */
    public static TypeOfPlace getRandomTypeOfPlaceAtAltitude(int altitude) {
        Dice dice = new Dice(typeOfPlacesInTheSpecificAltitude[altitude].list.size());
        NamesOfTypesOfPlaces name = typeOfPlacesInTheSpecificAltitude[altitude].list.get(dice.toss() - 1);
        return typeOfPlaces.get(name);
    }
}
/*
 * 1. 4x4, náhodně podle altitude do 5 kategorií
 * - 0 - 200 - > louka, bažina, mokřad, forest_borovice, forest_leafy, lake,
 * stream, river
 * - 200 - 400 - > louka, forest_borovice, forest_leafy, lake, stream, river
 * - 400 - 600 - > louka, forest_borovice, forest_leafy, stream, river,
 * forest_smrk, cave
 * - 600 - 800 - > mountein_meadow, louka, forest_borovice, forest_smrk, cave,
 * mountain_lake, mountain_river, mountain_stream, cliff, rock_land, moorland
 * - 800 - 1000 - > mountein_meadow, forest_smrk, cave,
 * mountain_lake, mountain_stream, cliff, rock_land
 */
