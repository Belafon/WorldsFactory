package com.belafon.world.visibles.resources;

import java.util.ArrayList;
import java.util.List;

import com.belafon.world.maps.placeEffects.PlaceEffect;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.BehavioursPossibleIngredientID;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public class TypeOfResource extends BehavioursPossibleRequirement implements BehavioursPossibleIngredient {
    public static BehavioursPossibleRequirement REQUIREMENT_RESOURCE_IS_KNOWN = new BehavioursPossibleRequirement(
            "A type of resource is known.") {
    };
    // final stats
    public final ListOfAllTypesOfResources.NamesOfTypesOfResources name;
    public final PlaceEffect[] effects;

    // (in cz "nápadnoost"), than with more resources with heigher consp., to find
    // the resource with less consicousness it takes more time (viz. speedOfFinding
    // in Resource)
    // differece of conspiciousness = nuber of ticks to wait to find it
    public final int conspicuousness;
    public final int id;

    public TypeOfResource(ListOfAllTypesOfResources.NamesOfTypesOfResources name, PlaceEffect[] effects,
            int conspicuousness, int id) {
        super("type of resource is visible");
        this.name = name;
        this.effects = effects;
        this.conspicuousness = conspicuousness;
        this.id = id;
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        List<BehavioursPossibleRequirement> result = new ArrayList<>();
        synchronized (creature.memory.typeResources) {
            if (creature.memory.typeResources.contains(this))
                result.add(REQUIREMENT_RESOURCE_IS_KNOWN);
        }
        result.add(REQUIREMENT_RESOURCE_IS_KNOWN);
        return result;
    }

    @Override
    public BehavioursPossibleIngredientID getBehavioursPossibleIngredientID() {
        return new BehavioursPossibleIngredientID(TypeOfResource.class, id + "");
    }
}
