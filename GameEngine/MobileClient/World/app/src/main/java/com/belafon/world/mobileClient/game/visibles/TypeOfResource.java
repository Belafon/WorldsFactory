package com.belafon.world.mobileClient.game.visibles;

import android.media.UnsupportedSchemeException;

import java.util.Set;

import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

/**
 * Holds information about concrete type of resoruce.
 */
public class TypeOfResource extends BehavioursPossibleIngredient {
    public final String name;
    public final String id;

    public TypeOfResource(String name, String id, Set<BehavioursRequirement> requirements) {
        super(requirements);
        this.name = name;
        this.id = id;
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    public String getId() {
        return " " + name.hashCode();
    }

    @Override
    public String getVisibleType() {
        return "TypeOfResource";
    }

    @Override
    public String getMessageId(){
        return id;
    }

    @Override
    public int compareTo(BehavioursPossibleIngredient behavioursPossibleIngredient) {
        throw new UnsupportedOperationException();
    }
}
