package com.belafon.world.visibles.items.types;

import java.util.ArrayList;
import java.util.List;

import com.belafon.world.World;
import com.belafon.world.calendar.events.Event;
import com.belafon.world.calendar.events.EventItemChange;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.objectsMemory.ObjectsMemoryCell;
import com.belafon.world.objectsMemory.itemsMemory.FoodItemsMemory;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.items.itemsSpecialStats.SpecialFoodsProperties;
import com.belafon.world.visibles.items.typeItem.FoodTypeItem;

public class Food extends Item {
    private volatile int freshness;
    private volatile int warm;
    public ArrayList<SpecialFoodsProperties> specialProperties;
    EventItemChange changeFreshness;
    EventItemChange changeWarm;
    public final FoodItemsMemory memory = new FoodItemsMemory();
    public static final BehavioursPossibleRequirement REQUIREMENT = new BehavioursPossibleRequirement(
            "A Food is in inventory.") {
    };

    public Food(World game, int warm, int freshness, SpecialFoodsProperties[] specialProperties,
            FoodTypeItem type, UnboundedPlace position) {
        super(game, type, position);
        this.setFreshness(freshness, game);
        this.setWarm(warm, game);
        this.specialProperties = new ArrayList<SpecialFoodsProperties>();
        for (SpecialFoodsProperties s : specialProperties)
            this.specialProperties.add(s);
    }

    public int getWarm() {
        return warm;
    }

    public void setWarm(int warm, World game) {
        if (warm < 0)
            warm = 0;

        memory.warm.add(new ObjectsMemoryCell<Integer>(game.time.getTime(), warm));
        this.warm = warm;
        if (warm > 0 && changeWarm == null) {
            changeWarm = new EventItemChange(game.time.getTime() + getLocation().getTemperature(), game, this);
            game.calendar.add(changeWarm);
        } else if (warm == 0)
            changeWarm = null;
    }

    public int getFreshness() {
        return freshness;
    }

    public void setFreshness(int freshness, World game) {
        if (freshness < 0)
            freshness = 0;
        memory.freshness.add(new ObjectsMemoryCell<Integer>(game.time.getTime(), freshness));
        this.freshness = freshness;
        if (freshness > 0 && changeFreshness == null) {
            changeFreshness = new EventItemChange(game.time.getTime() + getType().SPEED_OF_DECAY, game, this);
            game.calendar.add(changeFreshness);
        } else if (freshness == 0)
            changeFreshness = null;
    }

    public FoodTypeItem getType() {
        return (FoodTypeItem) type;
    }

    @Override
    public synchronized int changeStats(Event event, World game) { // 0 -> stop it, else add to calendar
        /* TODO
         * if(event == changeFreshness){
         * setFreshness(freshness - 1, game);
         * if(freshness != 0) return getType().speedOfDecay;
         * }else{
         * setWarm(warm - 1, game);
         * if(warm != 0) return owner.getPosition().getTemperature();
         * }
         */
        return 0;
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        var itemsRequirements = super.getBehavioursPossibleRequirementType(creature);

        if (creature.inventory.ownsItem(this)) {
            itemsRequirements.add(REQUIREMENT);
        }
        return itemsRequirements;
    }
}
