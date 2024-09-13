package com.belafon.world.visibles.creatures.behaviour.behaviours;

import java.util.List;
import java.util.Map;

import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.Behaviour;
import com.belafon.world.visibles.creatures.behaviour.BehaviourBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.BehaviourTypeBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType.IngredientsCounts;
import com.belafon.world.visibles.items.types.Food;

public class Eat extends Behaviour {
    private final Food food;

    private static boolean checkIngredients(List<BehavioursPossibleIngredient> ingredients) {
        if (ingredients.size() != 1)
            throw new IllegalArgumentException("Eat behaviour can have only one ingredient.");
        if (!(ingredients.get(0) instanceof Food))
            throw new IllegalArgumentException("Eat behaviour can have only food as ingredient.");
        return true;
    }
    public static final BehaviourType type;
    public static final BehaviourBuilder builder = (Creature creature,
            List<BehavioursPossibleIngredient> ingredients) -> {
        checkIngredients(ingredients);
        return new Eat(creature, (Food) ingredients.get(0));
    };

    static {
        type = new BehaviourTypeBuilder("Eat", "Lets eat a food to stop hunger.")
                .setBehaviourBuilder(builder)
                .setBehaviourClass(Eat.class)
                .addRequirement(Food.REQUIREMENT, new IngredientsCounts(null, 1, 0))
                .build();
    }

    public Eat(Creature creature, Food food) {
        super(creature.game, 0, 0, creature);
        this.food = food;
    }

    @Override
    public void execute() {
        creature.actualCondition.setHunger(creature.actualCondition.getHunger() - food.getType().getFilling());
        creature.actualCondition.setHeat(creature.actualCondition.getHeat() + food.getWarm());
        creature.inventory.removeItem(food);
    }

    @Override
    public Map<BehavioursPossibleRequirement, IngredientsCounts> getRequirements() {
        return type.requirements;
    }

    @Override
    public BehaviourType getType() {
        return type;
    }

}
