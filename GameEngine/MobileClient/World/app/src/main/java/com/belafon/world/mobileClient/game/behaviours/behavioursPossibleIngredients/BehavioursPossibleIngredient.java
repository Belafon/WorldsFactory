package com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients;

import java.util.Collections;
import java.util.Set;

import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;

/**
 * Can be used for satisfying a behaviour requirement.
 */
public abstract class BehavioursPossibleIngredient implements Comparable<BehavioursPossibleIngredient>{
    public final Set<BehavioursRequirement> requirements;

    public BehavioursPossibleIngredient(Set<BehavioursRequirement> requirements) {
        this.requirements = Collections.unmodifiableSet(requirements);
    }

    public String toString() {
        return getName() + " " + getId();
    }

    protected abstract String getName();
    public abstract String getId();

    public String getMessageId(){
        return "" + getId();
    }
    public abstract String getVisibleType();
}
