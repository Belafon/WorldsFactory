package com.belafon.world.visibles.creatures.behaviour.behaviours;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.belafon.world.visibles.creatures.behaviour.BehaviourType;

/**
 * This represents a property of a creature. If a creature has 
 * this proeprty, it can enable some behaviours to be executed.
 */
public abstract class BehavioursPossibleRequirement {
    /**
     * Each BehaviourPossibleRequirement holds the set of
     * behaviours, that are dependent on that. The reason is
     * better performance when creature counts possible
     * behaviours that can be done, when some BehaviourPossibleIngredient
     * changes.
     */
    public Set<BehaviourType> behaviours = new HashSet<>();

    public final String name;
    public final String idName;

    public BehavioursPossibleRequirement(String name) {
        this.name = name;
        this.idName = name
                .replaceAll(" ", "_")
                .replaceAll("[.]", "")
                .toLowerCase();
        BehaviourType.setupNewRequirement(this);
    }

    /**
     * should be called after all BehaviourTypes are created
     */
    public void makeUnmodifiable() {
        behaviours = Collections.unmodifiableSet(behaviours);
    }
}
