package com.belafon.settings;

import com.belafon.world.World;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.Creature;

public class TestingCreature extends Creature {

    public TestingCreature(World game, String name, Place position, 
        String appearence, int weight) {
        super(game, name, position, appearence,
            TestingMessages.createMessageSender(), weight);
            
    }

    @Override
    protected void setInventory(UnboundedPlace unboundedPlace) {
    } 
    
}
