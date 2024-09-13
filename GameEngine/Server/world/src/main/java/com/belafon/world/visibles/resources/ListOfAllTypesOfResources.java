package com.belafon.world.visibles.resources;

import java.util.Hashtable;
import java.util.Map;

import com.belafon.world.maps.placeEffects.PlaceEffect;

public class ListOfAllTypesOfResources {
    /**
     * All types of resources in the game that are knowen by creatures by default.
     */
    public static Map<NamesOfTypesOfResources, TypeOfResource> typesOfResources = new Hashtable<NamesOfTypesOfResources, TypeOfResource>();
    
    public static volatile int idOfResourceType = 0;   
    /**
     * There are defined all types of resources in the game.
     */
    public static void setUpAllResources(){
        typesOfResources.put(NamesOfTypesOfResources.blueberry, new TypeOfResource(NamesOfTypesOfResources.blueberry, new PlaceEffect[0], 1000, idOfResourceType++));
        typesOfResources.put(NamesOfTypesOfResources.mushrooms, new TypeOfResource(NamesOfTypesOfResources.mushrooms, new PlaceEffect[0], 996, idOfResourceType++));
        typesOfResources.put(NamesOfTypesOfResources.treeOak, new TypeOfResource(NamesOfTypesOfResources.treeOak, new PlaceEffect[0], 1010, idOfResourceType++));
        typesOfResources.put(NamesOfTypesOfResources.treePine, new TypeOfResource(NamesOfTypesOfResources.treePine, new PlaceEffect[0], 1010, idOfResourceType++));
        typesOfResources.put(NamesOfTypesOfResources.treeSpruce, new TypeOfResource(NamesOfTypesOfResources.treeSpruce, new PlaceEffect[0], 1010, idOfResourceType++));
    }
    public enum NamesOfTypesOfResources{
        blueberry,
        mushrooms,
        treeOak, // dub
        treePine,
        treeSpruce
    }
}
