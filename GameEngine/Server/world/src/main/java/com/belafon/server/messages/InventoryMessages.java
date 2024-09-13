package com.belafon.server.messages;

import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.items.types.*;
public interface InventoryMessages {

    public default void ClothesPutOn(Clothes clothes) {
	}
    public default void ClothesPutOff(Clothes clothes) {
    }
    public default void setAddItem(Item item, Creature creature) {
    }
	public default void setRemoveItem(Item item) {
    }
}
