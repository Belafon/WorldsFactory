package com.belafon.world.mobileClient.game.inventory;

import com.belafon.world.mobileClient.game.visibles.items.Item;
import com.belafon.world.mobileClient.game.visibles.items.Clothes;

import java.util.HashMap;
import java.util.Map;

public class PlayersGear {
    private Map<String, ClothesablePartOfBody> partOfBodies = new HashMap<>();

    {
        partOfBodies.put("Head", new ClothesablePartOfBody("Head"));
        partOfBodies.put("Shoulders", new ClothesablePartOfBody("Shoulders"));
        partOfBodies.put("Arms", new ClothesablePartOfBody("Arms"));
        partOfBodies.put("Hands", new ClothesablePartOfBody("Hands"));
        partOfBodies.put("Body", new ClothesablePartOfBody("Body"));
        partOfBodies.put("Legs", new ClothesablePartOfBody("Legs"));
        partOfBodies.put("Feet", new ClothesablePartOfBody("Feet"));
    }

    private volatile Item finger; // TODO
    private volatile Item neck;

    public boolean putOn(Clothes clothes) {
        if(clothes.partOfBody.getClothes() != clothes){
            clothes.partOfBody.setClothes(clothes);
            return true;
        }
        return false;
    }

    public boolean putOff(Clothes clothes) {
        if(clothes == clothes.partOfBody.getClothes()){
            clothes.partOfBody.setClothes(null);
            return true;
        }
        return false;
    }

    public ClothesablePartOfBody getPartOfBodyByName(String name) {
        return partOfBodies.get(name);
    }

}
