package com.belafon.world.visibles.resources;

import java.util.Arrays;
import java.util.List;

import com.belafon.world.World;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;
import com.belafon.world.visibles.creatures.behaviour.behaviours.FindConcreteResource;

/**
 * Represents something, creature can interact with.
 * Usually represents group of sometihing that is
 * in concrete place and it describes the place.
 * 
 * It could be trees, mushrooms, berries, flowers...
 */
public class Resource extends Visible {
    public volatile int durationOfFinding;
    public final TypeOfResource type;
    private volatile int amount; // 100 -> 100% of durationOfFinding, 100 -> regular amount
    public final int id;
    private UnboundedPlace location;

    public Resource(TypeOfResource typeOfResource, int amount, World game, UnboundedPlace location) {
        this.type = typeOfResource;
        this.amount = amount;
        this.id = game.visibleIds.getResourceId();
        this.location = location;
    }

    public int getConspicuousness() {
        return (type.conspicuousness * amount) / 100;
    }

    public void setAmount(int amount, UnboundedPlace place) {
        this.amount = amount;
        try {
            FindConcreteResource.setResourcesDurationOfFinding(place, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UnboundedPlace getLocation() {
        return location;
    }

    @Override
    public void setLocation(UnboundedPlace place) {
        this.location = place;
        place.addResource(type, amount);
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        return Arrays.asList(type);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int getVisibility() {
        return getConspicuousness();
    }

    @Override
    public Class<? extends Visible> getClassType() {
        return Resource.class;
    }

    @Override
    protected int getIdNumber() {
        return id;
    }


}