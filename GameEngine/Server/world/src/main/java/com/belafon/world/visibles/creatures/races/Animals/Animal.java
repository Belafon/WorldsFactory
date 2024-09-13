package com.belafon.world.visibles.creatures.races.Animals;

import com.belafon.world.World;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.inventory.Gear;
import com.belafon.world.visibles.creatures.inventory.Inventory;
import com.belafon.world.visibles.creatures.races.Animals.animalRaces.AnimalRace;
import com.belafon.world.visibles.items.types.Clothes;

public class Animal extends Creature {
    public final AnimalRace race;

    public Animal(World game, String name, UnboundedPlace position, String appearence,
            int weight, AnimalRace race) {
        super(game, name, position, appearence, race.sendMessage, weight);
        this.race = race;
    }

    @Override
    protected void setInventory(UnboundedPlace position) {
        inventory = new Inventory(new Gear() {

            @Override
            public boolean putOn(Clothes clothes) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'putOn'");
            }

            @Override
            public boolean putOff(Clothes clothes) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'putOff'");
            }
        }, this, position);
    }
}
