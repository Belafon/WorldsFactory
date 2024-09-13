package com.belafon.world.visibles.creatures.inventory;

import com.belafon.world.visibles.items.types.Clothes;

public abstract class Gear {
    // returns false if the clothes is not compatible with creatures body
    public abstract boolean putOn(Clothes clothes);
    public abstract boolean putOff(Clothes clothes);
    public volatile int warm = 0;

}
