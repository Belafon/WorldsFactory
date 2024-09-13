package com.belafon.world.mobileClient.game.maps.playersPlaceFragments;

import androidx.fragment.app.Fragment;
import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * All possible place effects and their informations are held here 
 * Place effect is something that is visible from the players 
 * position, but it is located in the other place.
 */
public class PlayersPlaceEffect extends BehavioursPossibleIngredient {
    public static final String visibleTypeName = "placeEffect";

    public final static Map<PlaceEffectName, PlayersPlaceEffect> allPlaceEffects;
    public final PlaceEffectName name;
    public final String look;
    public final int id;
    private PlayersPlaceEffect(PlaceEffectName name, String look, int id, Set<BehavioursRequirement> requirements) {
        super(requirements);
        this.name = name;
        this.look = look;
        this.id = id;
    }

    static {
        allPlaceEffects = new HashMap<>();
        allPlaceEffects.put(PlaceEffectName.fire, new PlayersPlaceEffectBuilder()
                .setName(PlaceEffectName.fire)
                .setLook("Fire with huge smoke")
                .setId(PlaceEffectName.fire.ordinal())
                .build());

    }

    @Override
    protected String getName() {
        return name.name();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getVisibleType() {
        return visibleTypeName;
    }

    @Override
    public int compareTo(BehavioursPossibleIngredient ingredient) {
        return 0;
    }

    public static enum PlaceEffectName {
        fire
    }

    /**
     * Retunrs a fragment with info about the PlaceEffect.
     * @return
     */
    public Fragment getInfoFragment(Fragment lastFragment, int container) {
        return new PlaceEffectInfoFragment(lastFragment, container, name, look, this);
    }

    public static class PlayersPlaceEffectBuilder {
        private PlaceEffectName name;
        private String look;
        private int id;
        private Set<BehavioursRequirement> requirements = new HashSet<>();

        public PlayersPlaceEffectBuilder setName(PlaceEffectName name) {
            this.name = name;
            return this;
        }

        public PlayersPlaceEffectBuilder setLook(String look) {
            this.look = look;
            return this;
        }

        public PlayersPlaceEffectBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public PlayersPlaceEffectBuilder addRequirement(BehavioursRequirement requirement) {
            this.requirements.add(requirement);
            return this;
        }

        public PlayersPlaceEffect build() {
            if (name == null || look == null || requirements.isEmpty()) {
                throw new IllegalStateException("Required values not provided");
            }

            return new PlayersPlaceEffect(name, look, id, requirements);
        }
    }

}
