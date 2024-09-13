package com.belafon.world.visibles.creatures.condition.knowledge;

import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public class TypeKnowledge extends BehavioursPossibleRequirement {
    public final String name;

    public TypeKnowledge(String name) {
        super("Type of knowledge is known.");
        this.name = name;
    }
}
