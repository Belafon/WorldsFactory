package com.belafon.world.visibles.resources;

import com.belafon.likeliness.Dice;

/**
 * Holds informations that are unique for each (TypeOfResource, TypeOfPlace), 
 * Where UnboundedPlace with concrete TypeOfPlace can be generated with
 * this Type of resource. 
 * 
 * Forexample holds info about likeliness of pressence 
 * of a type of resource in a TypeOfPlace.
 */
public class TypeOfResourceOfTypeOfPlace {
    public final int likelinessOfPressence; // 0 - 100, ...of the resource in specific place, more means heigher probability
    public final TypeOfResource typeOfResource;
    public int startAmount;
    public TypeOfResourceOfTypeOfPlace(int likelinessOfPressence, int startAmount, TypeOfResource typeOfResource) {
        this.likelinessOfPressence = likelinessOfPressence;
        this.typeOfResource = typeOfResource;
        this.startAmount = startAmount;
    }

    public boolean isHereResourceGenerate(){
        Dice dice = new Dice(101);
        return dice.toss() - 1 <= likelinessOfPressence ? true : false; 
    }
}
