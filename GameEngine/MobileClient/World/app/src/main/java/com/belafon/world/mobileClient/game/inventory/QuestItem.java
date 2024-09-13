package com.belafon.world.mobileClient.game.inventory;

import com.belafon.world.mobileClient.game.visibles.items.Item;

import java.util.HashSet;

public class QuestItem extends Item {
    public QuestItem(int id, String name, int weight, int visibility, int toss) {
        super(id, name, "", weight, visibility, toss, new HashSet<>());
    }
}
