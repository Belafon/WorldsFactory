package com.belafon.world.mobileClient.game.inventory;

import com.belafon.world.mobileClient.game.visibles.items.Clothes;

public class ClothesablePartOfBody {
    public final String name;
    private volatile Clothes clothes;
    public ClothesablePartOfBody(String name) {
        this.name = name;
    }

    public void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }

    public Clothes getClothes() {
        return clothes;
    }
}
