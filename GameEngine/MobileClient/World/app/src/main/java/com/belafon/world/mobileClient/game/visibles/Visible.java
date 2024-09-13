package com.belafon.world.mobileClient.game.visibles;

import java.util.Set;

import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

/**
 * Holds information about concrete visible.
 */
public abstract class Visible extends BehavioursPossibleIngredient {
    public final String name;

    public Visible(Set<BehavioursRequirement> requirements, String name) {
        super(requirements);
        this.name = name;
    }

    /**
     * @return method that should be called
     * when a title fragment in list of visibles
     * is clicked.
     */
    public abstract Runnable getOnTitleClick();

    @Override 
    public String getName() {
        return name;
    }
}
