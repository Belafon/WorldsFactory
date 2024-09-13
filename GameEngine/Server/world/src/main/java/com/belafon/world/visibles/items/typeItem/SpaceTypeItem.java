package com.belafon.world.visibles.items.typeItem;

import com.belafon.world.visibles.items.ListOfAllItemTypes.NamesOfSpaceItemTypes;

/**
 * It represents item which is binded with 
 * some unbounded place.
 * It can represent an entry to other map, or
 * item like bag, or wardrobe. 
 */
public class SpaceTypeItem extends TypeItem {
    public final int volume;

    public SpaceTypeItem(NamesOfSpaceItemTypes name, int weight, int toss, 
            int visibility, String look, int volume) {
        super(name.name(), weight, toss, visibility, look);
        this.volume = volume;
    }
}
