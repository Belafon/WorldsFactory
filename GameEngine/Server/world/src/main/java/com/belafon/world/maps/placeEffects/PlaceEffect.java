package com.belafon.world.maps.placeEffects;

/**
 * Place effect represents an information about something 
 * that is happening in some place. 
 * 
 * This information can be seen from
 * surrounding places.
 * 
 */
public class PlaceEffect {
    public final PlaceEffects.PlaceEffectName name;
	public final int visibility; // 0-100 -> 89 < je vyd�t z jin�ho place!!
    public final String look;
    
    /**
     * Place effect represents information about something 
     * that is happening in some place. 
     * 
     * This information can be seen from
     * surrounding places.
     * 
     * @param name
     * @param visibility
     * @param look
     */
	public PlaceEffect(PlaceEffects.PlaceEffectName name, int visibility, String look) {
		this.name = name;
		this.look = look;
		this.visibility = visibility;
	}
}
