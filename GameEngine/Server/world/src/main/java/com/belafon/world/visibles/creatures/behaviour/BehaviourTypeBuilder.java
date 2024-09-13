package com.belafon.world.visibles.creatures.behaviour;

import java.util.HashMap;
import java.util.Map;

import com.belafon.world.visibles.creatures.behaviour.BehaviourType.IngredientsCounts;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public class BehaviourTypeBuilder {
    private Map<BehavioursPossibleRequirement, IngredientsCounts> requirements = new HashMap<>();
    private String name;
    private String description;
    private Class<? extends Behaviour> behaviourClass;
    private BehaviourBuilder builder;

    public BehaviourTypeBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // , BehaviourBuilder builder, Class<? extends Behaviour> behaviourClass
    public BehaviourTypeBuilder setBehaviourBuilder(BehaviourBuilder builder) {
        this.builder = builder;
        return this;
    }

    public BehaviourTypeBuilder setBehaviourClass(Class<? extends Behaviour> behaviourClass) {
        this.behaviourClass = behaviourClass;
        return this;
    }

    public BehaviourTypeBuilder addRequirement(BehavioursPossibleRequirement req,
            IngredientsCounts number) {
        requirements.put(req, number);
        return this;
    }

    public BehaviourType build() {
        // check if all parameters are set
        if (builder == null)
            throw new IllegalArgumentException("BehaviourBuilder is not set.");
        
        if (behaviourClass == null)
            throw new IllegalArgumentException("BehaviourClass is not set.");

        var object = new BehaviourType(behaviourClass.getSimpleName(), name,
                description, builder, requirements, behaviourClass);

        if (object.requirements.size() == 0)
            BehaviourType.ALL_BEHAVIOUR_TYPES_WITHOUT_REQUIREMENT.add(object);
        BehaviourType.ALL_BEHAVIOUR_TYPES.put(object.idName, object);
        requirements = null;
        
        for (var req : object.requirements.keySet()) {
            req.behaviours.add(object);
        }
        return object;
    }
}