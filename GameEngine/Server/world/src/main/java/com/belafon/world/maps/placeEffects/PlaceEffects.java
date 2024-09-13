package com.belafon.world.maps.placeEffects;

/**
 * List of all PlaceEffects, that can occure.
 */
public class PlaceEffects {
    public static final PlaceEffect[] placeEffect = {
        new PlaceEffect(PlaceEffectName.fire, 80, "On the horizon you can see the big smoke rising up. Looks like there could be a fire.")
    };
    public static enum PlaceEffectName{
        fire
    }
}
