package com.belafon.world.maps;

import com.belafon.world.World;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.Player;
import com.belafon.world.visibles.creatures.races.Animals.Animal;
import com.belafon.world.visibles.creatures.races.Animals.animalRaces.AnimalRace;
import com.belafon.world.visibles.items.ListOfAllItemTypes;
import com.belafon.world.visibles.items.itemsSpecialStats.SpecialFoodsProperties;
import com.belafon.world.visibles.items.types.Food;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources;
import com.belafon.world.visibles.resources.Resource;

/**
 * Debuging methods.
 */
public class GenerateVisibles {

    /**
     * Debuging method.
     */
    public static void spawnDeerAndAllCreaturesNotices(UnboundedPlace place, World game) {
        Animal deer = new Animal(game, "deer1", place,
                "Nice brown wealthy deer", 5, AnimalRace.deer);
        //place.addCreature(deer);
        synchronized (game.creatures) {
            game.creatures.add(deer);
        }
        synchronized (game.players) {
            for (Player player : game.players) {
                //player.addVisible(deer);
            }
        }
    }

    /**
     * Debuging method.
     */
    public static void spawnMashroomsAndAllCreaturesNotices(UnboundedPlace place, World game) {
        Resource mushrooms = place.addResource(
                ListOfAllTypesOfResources.typesOfResources
                        .get(ListOfAllTypesOfResources.NamesOfTypesOfResources.mushrooms),
                3);

        synchronized (game.players) {
            for (Player player : game.players) {
                player.addVisible(mushrooms);
            }
        }
    }

    /**
     * Debuging method.
     */
    public static void spawnAppleAndAllCreaturesNotices(UnboundedPlace place, World game) {
        Food apple = new Food(game, 0, 0,
                new SpecialFoodsProperties[0],
                ListOfAllItemTypes.foodTypes.get(ListOfAllItemTypes.NamesOfFoodItemTypes.apple),
                place);

        place.addItem(apple);

        synchronized (game.players) {
            for (Player player : game.players) {
                player.addVisible(apple);
            }
        }
    }
}
