package com.belafon.world.visibles.creatures.behaviour.behaviours;

import java.util.List;
import java.util.Map;

import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.Behaviour;
import com.belafon.world.visibles.creatures.behaviour.BehaviourBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.BehaviourTypeBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType.IngredientsCounts;
import com.belafon.world.visibles.items.Item;

public class PickUpItem extends Behaviour {
    public static final BehaviourType type;

    public static boolean checkIngredients(List<BehavioursPossibleIngredient> ingredients) {
        if (ingredients.size() != 1)
            throw new IllegalArgumentException("PickUpItem behaviour can have only one ingredient.");
        if (!(ingredients.get(0) instanceof Item))
            throw new IllegalArgumentException("PickUpItem behaviour can have only food as ingredient.");
        return true;
    }

    public static final BehaviourBuilder builder = (Creature creature,
            List<BehavioursPossibleIngredient> ingredients) -> {
        checkIngredients(ingredients);
        return new PickUpItem(creature, 0, 0, (Item) ingredients.get(0));
    };

    static {
        type = new BehaviourTypeBuilder("Pick up an item", "Lets pick an item up into the inventory...")
                .setBehaviourBuilder(builder)
                .setBehaviourClass(PickUpItem.class)
                .addRequirement(Item.REQUIREMENT_IS_VISIBLE, new IngredientsCounts(null, 1, 0))
                .build();
    }
    private Item item;

    public PickUpItem(Creature creature, int duration, int bodyStrain, Item item) {
        super(creature.game, duration, bodyStrain, creature);
        this.item = item;
    }

    @Override
    public void execute() {
        creature.inventory.addItem(item);
    }

    @Override
    public String canCreatureDoThis() {
        if (!creature.seesVisibleObject(creature))
            return "do_not_have_required_item";
        return null;
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
