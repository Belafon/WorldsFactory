package com.belafon.world.visibles.items;

import java.util.ArrayList;
import java.util.List;

import com.belafon.world.World;
import com.belafon.world.calendar.events.Event;
import com.belafon.world.calendar.events.EventItemChange;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;
import com.belafon.world.visibles.items.typeItem.TypeItem;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

@WorldsFactoryClass(className = "Item", autoRegister = false)
public class Item extends Visible {
    public static final BehavioursPossibleRequirement REQUIREMENT_IS_VISIBLE = new BehavioursPossibleRequirement(
            "An item is visible") {
    };
    public static final BehavioursPossibleRequirement REQUIREMENT_IS_IN_INVENTORY = new BehavioursPossibleRequirement(
            "An item is in inventory") {
    };

    public final int id;
    public final TypeItem type;
    public volatile Creature owner;
    public volatile EventItemChange eventItemChange;
    protected UnboundedPlace location; // TODO change location, when is moving
    private int weight;

    @WorldsFactoryObjectsName
    private String storyObjectName;

    public Item(World game, TypeItem type, UnboundedPlace location) {
        this.id = game.visibleIds.getItemId();
        this.type = type;
        this.weight = type.regularWeight;
        this.location = location;
    }

    public Item(World game, TypeItem type, UnboundedPlace location, String storyObjectName) {
        this.id = game.visibleIds.getItemId();
        this.type = type;
        this.weight = type.regularWeight;
        this.location = location;
        this.storyObjectName = storyObjectName;

        WorldsFactoryStoryManager.bindObject(storyObjectName, this);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Counts duration to next event, when stats should be updated
     */
    public synchronized int changeStats(Event event, World game) {
        return 0;
    }

    @Override
    public UnboundedPlace getLocation() {
        return location;
    }


    @WorldsFactoryPropertySetter(name = "location")
    public void setStoryLocation(Place place) {
        if(place != null)
            setLocation(place);
        // else location of the item is in some creature inventory
        WorldsFactoryStoryManager.setProperty("location", place, storyObjectName);
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        List<BehavioursPossibleRequirement> result = new ArrayList<>();
        if (creature.inventory.ownsItem(this)) {
            result.add(type.requirementIsInInventory);
            result.add(REQUIREMENT_IS_IN_INVENTORY);
        } else {
            result.add(type.requirementIsVisible);
            result.add(REQUIREMENT_IS_VISIBLE);
        }
        return result;
    }

    @Override
    public int getVisibility() {
        return type.visibility;
    }

    @Override
    public Class<? extends Visible> getClassType() {
        return Item.class;
    }

    @Override
    protected int getIdNumber() {
        return id;
    }

    @Override
    public void setLocation(UnboundedPlace place) {
        this.location = place;
        place.addItem(this);
    }
}