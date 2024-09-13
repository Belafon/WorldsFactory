package com.belafon.world.visibles.creatures.condition.knowledge;

import java.util.Arrays;
import java.util.List;

import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.BehavioursPossibleIngredientID;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public class Knowledge implements BehavioursPossibleIngredient {
    public final TypeKnowledge type;
    public volatile int degree; // stupeň znalosti, degree of knowledge
    public final int id;

    public Knowledge(TypeKnowledge type, int degree, int id) {
        this.type = type;
        this.degree = degree;
        this.id = id;
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        return Arrays.asList(type);
    }

    @Override
    public BehavioursPossibleIngredientID getBehavioursPossibleIngredientID() {
        return new BehavioursPossibleIngredientID(Knowledge.class, id + "");
    }
}
