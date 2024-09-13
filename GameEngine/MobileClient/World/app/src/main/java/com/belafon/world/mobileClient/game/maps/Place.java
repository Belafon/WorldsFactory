package com.belafon.world.mobileClient.game.maps;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlaceInfoFragment;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlacePanel;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlayersPlaceEffect;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.TypePlace;

/**
 * Represents a place in the world, that doesnt have to be in a map.
 */
public class Place extends BehavioursPossibleIngredient {
    public static final Place UNKNOWN = new Place("UnboundedPlace|unknown$" + Integer.MIN_VALUE,
            TypePlace.allTypes.get("unknown"),
            new HashSet<>(), new ArrayList<>());
    public final int id;
    public final String mapId;
    public TypePlace typePlace;
    public List<PlayersPlaceEffect> placeEffects;

    public Place(String id, TypePlace typePlace, Set<BehavioursRequirement> requirements,
            List<PlayersPlaceEffect> placeEffects) {
        super(requirements);
        var idTypeSplit = id.split("\\|");
        var idSplit = idTypeSplit[1].split("\\$");
        this.mapId = idSplit[0];
        this.id = Integer.parseInt(idSplit[1]);
        this.typePlace = typePlace;
        this.placeEffects = placeEffects;
    }

    @Override
    protected String getName() {
        if (this instanceof PlacePanel fragment)
            return typePlace.name + " [" + fragment.positionX + ";" + fragment.positionY + "]";
        return typePlace.name;
    }

    @Override
    public String getId() {
        return mapId + "$" + id;
    }

    @Override
    public String getVisibleType() {
        return "UnboundedPlace";
    }

    /**
     * @return a fragment with all detailed informations about the place.
     */
    public PlaceInfoFragment getInfoFragment(Fragment lastFragment, int fragmentContainerId) {
        if(this instanceof PlacePanel fragment)
            return new PlaceInfoFragment(lastFragment, fragmentContainerId, typePlace.name, typePlace.description, placeEffects, this);
        
        return new PlaceInfoFragment(lastFragment, fragmentContainerId, typePlace.name, typePlace.description, placeEffects, this);
    }

    @Override
    public int compareTo(BehavioursPossibleIngredient other) {
        throw new UnsupportedOperationException();
    }
}
