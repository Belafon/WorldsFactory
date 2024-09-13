package com.belafon.world.maps.place;

import com.belafon.world.World;
import com.belafon.world.visibles.creatures.behaviour.BehavioursPossibleIngredientID;
import com.belafon.world.visibles.items.Item;

/**
 * It is unbounded place, which is referenced with some item,
 * usually with Item of SpaceTypeItem type.
 * It can be used for representing a place like the inside
 * of bag, or space of wardrowbe, or back of creature.
 */
public class ItemPlace extends UnboundedPlace {
    private final Item item;

    public ItemPlace(TypeOfPlace typeOfPlace, World game, Item item) {
        super(typeOfPlace, game);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public int getTemperature() {
        return item.getLocation().getTemperature();
    }

    @Override
    public BehavioursPossibleIngredientID getBehavioursPossibleIngredientID() {
        return super.getBehavioursPossibleIngredientID();
    }

}
