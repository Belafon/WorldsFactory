package com.belafon.world.visibles.items.typeItem;

import com.belafon.world.visibles.creatures.behaviour.behaviours.ConsumableBehavioursPossibleRequirement;

public abstract class TypeItem {
    public final String name;
    public final int regularWeight;
    public final int toss; // > 0 -> throwable, more describes demage
    public final int visibility;
    public final String look;
    public final ConsumableBehavioursPossibleRequirement requirementIsVisible;
    public final ConsumableBehavioursPossibleRequirement requirementIsInInventory;

    public TypeItem(String name, int regularWeight, int toss, int visibility, String look) {
        requirementIsVisible = new ConsumableBehavioursPossibleRequirement("Item type " + name + " is visilbe.");
        requirementIsInInventory = new ConsumableBehavioursPossibleRequirement(
                "Item type " + name + " is in inventory.");
        this.name = name;
        this.regularWeight = regularWeight;
        this.toss = toss;
        this.visibility = visibility;
        this.look = look;
    }
}
