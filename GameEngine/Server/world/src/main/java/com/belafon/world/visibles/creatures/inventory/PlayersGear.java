package com.belafon.world.visibles.creatures.inventory;

import com.belafon.world.visibles.creatures.Player;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.items.typeItem.ClothesTypeItem;
import com.belafon.world.visibles.items.types.Clothes;

public class PlayersGear extends Gear {
    public volatile Clothes head;
    public volatile Clothes shoulders;
    public volatile Clothes arms;
    public volatile Clothes hands;
    public volatile Clothes body;
    public volatile Clothes legs;
    public volatile Clothes feets;
    public volatile Item finger;
    public volatile Item neck;

    @Override
    public boolean putOn(Clothes clothes) {
        super.warm += clothes.getType().warm;
        switch (clothes.getType().typeOfClothes) {
            case head:
                if (head != null)
                    warm -= head.getType().warm;
                head = clothes;
                break;
            case shoulders:
                if (shoulders != null)
                    warm -= shoulders.getType().warm;
                shoulders = clothes;
                break;
            case arms:
                if (arms != null)
                    warm -= arms.getType().warm;
                arms = clothes;
                break;
            case body:
                if (body != null)
                    warm -= body.getType().warm;
                body = clothes;
                break;
            case feets:
                if (feets != null)
                    warm -= feets.getType().warm;
                feets = clothes;
                break;
            case hands:
                if (hands != null)
                    warm -= hands.getType().warm;
                hands = clothes;
                break;
            case legs:
                if (legs != null)
                    warm -= legs.getType().warm;
                legs = clothes;
                break;
        }
        ((Player) clothes.owner).client.writer.inventory.ClothesPutOn(clothes);
        return false;
    }

    @Override
    public boolean putOff(Clothes clothes) {
        super.warm -= ((ClothesTypeItem) clothes.type).warm;
        switch (clothes.getType().typeOfClothes) {
            case head:
                head = null;
                break;
            case shoulders:
                shoulders = null;
                break;
            case arms:
                arms = null;
                break;
            case body:
                body = null;
                break;
            case feets:
                feets = null;
                break;
            case hands:
                hands = null;
                break;
            case legs:
                legs = null;
                break;
        }
        ((Player) clothes.owner).client.writer.inventory.ClothesPutOff(clothes);
        return false;
    }

}
