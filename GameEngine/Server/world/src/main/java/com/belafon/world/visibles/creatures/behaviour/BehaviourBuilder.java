package com.belafon.world.visibles.creatures.behaviour;

import java.util.List;

import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;

@FunctionalInterface
public interface BehaviourBuilder {
    Behaviour build(Creature creature, List<BehavioursPossibleIngredient> ingredient);
}
