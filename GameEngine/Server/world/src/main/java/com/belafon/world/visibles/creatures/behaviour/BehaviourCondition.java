package com.belafon.world.visibles.creatures.behaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.belafon.console.ConsolePrint;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public class BehaviourCondition {
    private Creature creature;
    private final Map<BehavioursPossibleRequirement, ArrayList<BehavioursPossibleIngredient>> behavioursIngredients = new ConcurrentHashMap<>();
    private final Set<BehaviourType> feasibleBehaviours = new HashSet<>();
    /**
     * new ingredient is added:
     * 1. when new visible is spotted
     * 2. when new knowledge is gained
     * 3. when new item is picked up
     */
    public final Map<BehavioursPossibleIngredientID, BehavioursPossibleIngredient> allIngredients = new HashMap<>();

    public BehaviourCondition(Creature creature) {
        this.creature = creature;
    }

    public void sendInfoAboutAllBehavioursWithNoRequirements() {
        for (BehaviourType behaviourType : BehaviourType.ALL_BEHAVIOUR_TYPES_WITHOUT_REQUIREMENT)
            creature.writer.behavioursMessages.newFeasibleBehaviour(behaviourType);
    }

    /**
     * checks wether the creature can do some other behaviour
     * and add the behaviours possible ingredient.
     * 
     * This is called when behavioursProperties keys updated,
     * or when new Visible spotted, or lost from sight
     */
    public void addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(BehavioursPossibleRequirement requirement,
            BehavioursPossibleIngredient ingredient) {

        if (behavioursIngredients.containsKey(requirement)) {
            behavioursIngredients.get(requirement).add(ingredient);
        } else {
            ArrayList<BehavioursPossibleIngredient> list = new ArrayList<>();
            list.add(ingredient);
            behavioursIngredients.put(requirement, list);
        }
        updateFeasibleBehaviours(requirement);
    }

    public void removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(BehavioursPossibleRequirement requirement,
            BehavioursPossibleIngredient ingredient) {

        if (!behavioursIngredients.containsKey(requirement)) {
            ConsolePrint.error(
                    "Creature.removeBehavioursPossibleRequirement: behavioursPossibleRequirement is not a key in behavioursProperties");
            return;
        }

        if (!behavioursIngredients.get(requirement).contains(ingredient)) {
            ConsolePrint.warning(
                    "Creature.removeBehavioursPossibleRequirement: behavioursProperties.get(behavioursPossibleRequirement) does not contain behavioursPossibleIngredients");
            return;
        }

        behavioursIngredients.get(requirement).remove(ingredient);
        checkPossibleBehavioursAfterBehavioursPossibleRequirementRemoved(requirement);
    }

    private void checkPossibleBehavioursAfterBehavioursPossibleRequirementRemoved(
            BehavioursPossibleRequirement removedRequirement) {
        for (final BehaviourType behaviourType : removedRequirement.behaviours) {
            if (!canCreatureDoTheBehaviour(behaviourType))
                removeUnfeasibleBehaviour(behaviourType);
        }
    }

    /**
     * sends informations to creature if it is
     * capable to do some new behaviour.
     * 
     * @param newRequirement
     */
    private void updateFeasibleBehaviours(
            BehavioursPossibleRequirement newRequirement) {

        for (final BehaviourType behaviourType : newRequirement.behaviours) {
            if (canCreatureDoTheBehaviour(behaviourType)) {
                synchronized (feasibleBehaviours) {
                    if (!feasibleBehaviours.contains(behaviourType))
                        addNewFeasibleBehaviour(behaviourType);
                }
            } else {
                if (feasibleBehaviours.contains(behaviourType))
                    removeUnfeasibleBehaviour(behaviourType);
            }
        }

    }

    public boolean canCreatureDoTheBehaviour(BehaviourType behaviourType) {
        return behaviourType.requirements.entrySet().stream().filter((x) -> {
            if (behavioursIngredients.containsKey(x.getKey())
                    && behavioursIngredients.get(x.getKey()).size() >= x.getValue().numOfSpecificIngredients()
                            + x.getValue().numOfGeneralIngredients())
                return false;
            return true;
        }).count() > 0 ? false : true;
    }

    private void addNewFeasibleBehaviour(BehaviourType behaviourType) {
        synchronized (feasibleBehaviours) {
            feasibleBehaviours.add(behaviourType);
        }
        creature.writer.behavioursMessages.newFeasibleBehaviour(behaviourType);
    }

    private void removeUnfeasibleBehaviour(BehaviourType behaviourType) {
        synchronized (feasibleBehaviours) {
            feasibleBehaviours.remove(behaviourType);
        }
        creature.writer.behavioursMessages.removeFeasibleBehaviour(behaviourType);
    }

    public void updateFeasibleBehaviours(Visible visible) {
        visible.getBehavioursPossibleRequirementType(creature).forEach((requirement) -> {
            addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(requirement, visible);
        });
    }
}
