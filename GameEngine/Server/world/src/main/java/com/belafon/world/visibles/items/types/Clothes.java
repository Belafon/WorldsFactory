package com.belafon.world.visibles.items.types;

import com.belafon.world.World;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.objectsMemory.ObjectsMemoryCell;
import com.belafon.world.objectsMemory.itemsMemory.ClothesItemMemory;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.items.typeItem.ClothesTypeItem;

public class Clothes extends Item {
    private int dirty;
    public final ClothesItemMemory memory = new ClothesItemMemory();

    public Clothes(World game, ClothesTypeItem type, int dirty, UnboundedPlace position) {
        super(game, type, position);
        this.dirty = dirty;
    }

    public ClothesTypeItem getType() {
        return (ClothesTypeItem) type;
    }

    public int getDirty() {
        return dirty;
    }

    public void setDirty(int dirty) {
        memory.dirty.add(new ObjectsMemoryCell<Integer>(location.game.time.getTime(), dirty));
        this.dirty = dirty;
    }
}
